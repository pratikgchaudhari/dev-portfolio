const {test} = require('node:test');
const assert = require('node:assert/strict');
const {readFileSync} = require('node:fs');
const {join} = require('node:path');
const {runInNewContext} = require('node:vm');

const script = readFileSync(join(__dirname, '../../main/resources/static/share.js'), 'utf8');

function setup({writeText, legacy = () => false, secure = true,
                url = 'https://notnullpratik.dev/blog/generics-in-java', article = true} = {}) {
    const classes = new Set();
    const attrs = new Map();
    const timers = new Map();
    const transient = new Set();
    const label = {textContent: 'Share'};
    const status = {textContent: ''};
    let click;
    let nextTimer = 0;
    let legacyCalls = 0;
    const button = {
        hidden: true, dataset: {shareUrl: url}, offsetWidth: 142,
        classList: {add: key => classes.add(key), remove: key => classes.delete(key)},
        querySelector: () => label,
        addEventListener: (event, handler) => { if (event === 'click') click = handler; },
        setAttribute: (key, value) => attrs.set(key, value),
        removeAttribute: key => attrs.delete(key),
        focus: () => { document.activeElement = button; }
    };
    const field = {
        value: '', selected: false,
        focus: () => { document.activeElement = field; },
        select: () => { field.selected = true; }
    };
    const fallback = {hidden: true, querySelector: () => field};
    const elements = {'[data-share-article]': article ? button : null,
        '[data-share-status]': status, '[data-share-fallback]': fallback};
    const document = {
        activeElement: button,
        querySelector: selector => elements[selector],
        createElement: () => {
            const input = {style: {}, focus: () => { document.activeElement = input; },
                select() {}, setSelectionRange() {}, remove: () => transient.delete(input)};
            return input;
        },
        body: {appendChild: input => transient.add(input)},
        execCommand: command => {
            assert.equal(command, 'copy');
            legacyCalls++;
            return legacy();
        }
    };
    runInNewContext(script, {
        document, navigator: {clipboard: writeText ? {writeText} : undefined}, URL,
        window: {location: {origin: 'https://preview.example',
            href: 'https://preview.example/blog/generics-in-java?utm_source=test#details'},
            isSecureContext: secure, getSelection: () => null},
        setTimeout: callback => { const id = ++nextTimer; timers.set(id, callback); return id; },
        clearTimeout: id => timers.delete(id)
    });
    return {button, label, status, fallback, field, classes, attrs, timers, transient, document,
        click: () => click(), legacyCalls: () => legacyCalls,
        expire: () => { const callbacks = [...timers.values()]; timers.clear(); callbacks.forEach(fn => fn()); }};
}

test('copies the canonical article URL and confirms only after clipboard success', async () => {
    const writes = [];
    const ui = setup({writeText: async value => writes.push(value)});
    assert.equal(ui.button.hidden, false);
    await ui.click();
    assert.deepEqual(writes, ['https://notnullpratik.dev/blog/generics-in-java']);
    assert.equal(ui.label.textContent, 'Link copied');
    assert.equal(ui.classes.has('is-copied'), true);
    assert.equal(ui.status.textContent, 'Article link copied to clipboard.');
    assert.equal(ui.fallback.hidden, true);
    assert.equal(ui.legacyCalls(), 0);
    ui.expire();
    assert.equal(ui.label.textContent, 'Share');
    assert.equal(ui.classes.has('is-copied'), false);
});

test('ignores repeat clicks while clipboard permission is pending', async () => {
    let finish;
    let calls = 0;
    const ui = setup({writeText: () => { calls++; return new Promise(resolve => { finish = resolve; }); }});
    const pending = ui.click();
    await ui.click();
    assert.equal(calls, 1);
    assert.equal(ui.attrs.get('aria-busy'), 'true');
    assert.equal(ui.classes.has('is-copied'), false);
    finish();
    await pending;
    assert.equal(ui.attrs.has('aria-busy'), false);
    assert.equal(ui.label.textContent, 'Link copied');
});

test('offers a selected URL when clipboard and legacy copying both fail', async () => {
    const ui = setup({writeText: async () => { throw new Error('denied'); }});
    await ui.click();
    assert.equal(ui.fallback.hidden, false);
    assert.equal(ui.field.value, 'https://notnullpratik.dev/blog/generics-in-java');
    assert.equal(ui.field.selected, true);
    assert.equal(ui.document.activeElement, ui.field);
    assert.equal(ui.classes.has('is-copied'), false);
    assert.equal(ui.label.textContent, 'Share');
    assert.equal(ui.timers.size, 0);
    assert.match(ui.status.textContent, /Couldn’t copy automatically/);
    assert.equal(ui.transient.size, 0);
});

test('uses legacy copy when Clipboard API is absent and restores focus', async () => {
    const ui = setup({legacy: () => true});
    await ui.click();
    assert.equal(ui.legacyCalls(), 1);
    assert.equal(ui.label.textContent, 'Link copied');
    assert.equal(ui.document.activeElement, ui.button);
    assert.equal(ui.transient.size, 0);
});

test('uses legacy copying on insecure origins instead of Clipboard API', async () => {
    let modernCalls = 0;
    const ui = setup({secure: false, writeText: async () => { modernCalls++; }, legacy: () => true});
    await ui.click();
    assert.equal(modernCalls, 0);
    assert.equal(ui.legacyCalls(), 1);
    assert.equal(ui.label.textContent, 'Link copied');
});

test('recovers from denied modern clipboard access when legacy copying works', async () => {
    const ui = setup({writeText: async () => { throw new Error('denied'); }, legacy: () => true});
    await ui.click();
    assert.equal(ui.label.textContent, 'Link copied');
    assert.equal(ui.fallback.hidden, true);
});

test('repeated successful copies restart the confirmation timer', async () => {
    const ui = setup({writeText: async () => {}});
    await ui.click();
    const firstTimer = [...ui.timers.keys()][0];
    await ui.click();
    assert.equal(ui.timers.size, 1);
    assert.equal(ui.timers.has(firstTimer), false);
    ui.expire();
    assert.equal(ui.status.textContent, '');
});

test('uses the clean article path when no public origin is configured', async () => {
    let copied;
    const ui = setup({url: '/blog/generics-in-java', writeText: async value => { copied = value; }});
    await ui.click();
    assert.equal(copied, 'https://preview.example/blog/generics-in-java');
});

test('handles legacy errors and does nothing on pages without the share button', async () => {
    const ui = setup({legacy: () => { throw new Error('unsupported'); }});
    await ui.click();
    assert.equal(ui.fallback.hidden, false);
    assert.equal(ui.transient.size, 0);
    assert.doesNotThrow(() => setup({article: false}));
});
