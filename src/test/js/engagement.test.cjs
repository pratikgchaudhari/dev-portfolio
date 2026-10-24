const {test} = require('node:test');
const assert = require('node:assert/strict');
const {readFileSync} = require('node:fs');
const {join} = require('node:path');
const {runInNewContext} = require('node:vm');

const script = readFileSync(join(__dirname, '../../main/resources/static/engagement.js'), 'utf8');
const flush = () => new Promise(resolve => setImmediate(resolve));

async function setup({liked = false, requestLike, observerSupported = true} = {}) {
    function element() {
        const attrs = new Map([['aria-pressed', 'false']]);
        const classes = new Set();
        const listeners = {};
        return {textContent: '', disabled: true, attrs, classes, listeners,
            classList: {add: name => classes.add(name), remove: name => classes.delete(name)},
            setAttribute: (name, value) => attrs.set(name, value), getAttribute: name => attrs.get(name),
            addEventListener: (name, handler) => { listeners[name] = handler; }};
    }
    const button = element(), bottomButton = element();
    const likeCount = element(), bottomCount = element();
    const message = element(), bottomMessage = element(), shareCount = element();
    const viewCount = element(), bottomViews = element(), bottomShares = element();
    shareCount.textContent = '0 shares';
    const controls = {'.heart-button': button, '[data-like-count]': likeCount,
        '[data-view-count]': viewCount, '[data-share-count]': shareCount, '[data-engagement-message]': message};
    const panel = {dataset: {api: '/api/articles/article'}, querySelector: selector => controls[selector]};
    const bottomControls = {'.heart-button': bottomButton, '[data-like-count]': bottomCount,
        '[data-view-count]': bottomViews, '[data-share-count]': bottomShares, '[data-engagement-message]': bottomMessage};
    const bottomPanel = {dataset: panel.dataset, querySelector: selector => bottomControls[selector]};
    const bottom = {...element(), hidden: true, querySelector: selector => bottomControls[selector],
        contains: target => target === bottomButton};
    const marker = {};
    const elements = {'[data-article-engagement]': panel, '[data-article-end-engagement]': bottom,
        '[data-article-reading-end]': marker};
    const document = {activeElement: null, visibilityState: 'visible',
        querySelector: selector => elements[selector],
        querySelectorAll: () => [panel, bottomPanel], addEventListener() {}};
    const windowEvents = {};
    const requests = [];
    const state = {views: 1, shares: 0, likes: liked ? 1 : 0, liked};
    let observer;
    class Observer {
        constructor(callback, options) { observer = {callback, options}; }
        observe(target) { assert.equal(target, marker); }
    }
    const window = {innerHeight: 736, addEventListener: (name, handler) => { windowEvents[name] = handler; }};
    if (observerSupported) window.IntersectionObserver = Observer;
    runInNewContext(script, {document, window, IntersectionObserver: Observer, AbortController,
        setTimeout: () => 1, clearTimeout() {}, requestAnimationFrame: callback => callback(),
        fetch: async (url, options) => {
            requests.push({url, options});
            if (url.endsWith('/like')) {
                if (requestLike) return requestLike(options);
                state.liked = JSON.parse(options.body).liked;
                state.likes = state.liked ? 1 : 0;
            }
            return {ok: true, json: async () => ({...state})};
        }});
    await flush();
    return {button, bottomButton, likeCount, bottomCount, bottom,
        message, bottomMessage, viewCount, bottomViews, shareCount, bottomShares, document, requests, state, observer,
        click: control => control.listeners.click({currentTarget: control}),
        reach: top => observer.callback([{boundingClientRect: {top}, rootBounds: {bottom: 640}}]),
        restore: () => windowEvents.pageshow({persisted: true})};
}

test('appears at the end of the article, stays below it, and hides on scrolling back', async () => {
    const ui = await setup();
    assert.equal(ui.bottom.hidden, true);
    ui.reach(900);
    assert.equal(ui.bottom.hidden, true);
    ui.reach(630);
    assert.equal(ui.bottom.hidden, false);
    ui.reach(-100);
    assert.equal(ui.bottom.hidden, false);
    ui.reach(900);
    assert.equal(ui.bottom.hidden, true);
    assert.equal(ui.requests.filter(request => request.url.endsWith('/like')).length, 0);
});

test('restored scroll positions and short articles can show the button immediately', async () => {
    const ui = await setup();
    ui.reach(-20);
    assert.equal(ui.bottom.hidden, false);
    assert.equal(ui.bottomButton.disabled, false);
});

test('liking from either button updates both counts and pressed states', async () => {
    const ui = await setup();
    await ui.click(ui.bottomButton);
    for (const button of [ui.button, ui.bottomButton]) {
        assert.equal(button.attrs.get('aria-pressed'), 'true');
        assert.equal(button.attrs.get('aria-label'), 'Unlike this article');
        assert.equal(button.classes.has('is-celebrating'), true);
    }
    assert.equal(ui.likeCount.textContent, '1 like');
    assert.equal(ui.bottomCount.textContent, '1 like');
    assert.equal(ui.bottomButton.attrs.get('aria-label'), 'Unlike this article');
    assert.equal(ui.message.textContent, '');
    assert.equal(ui.bottomMessage.textContent, 'Thanks for the love!');
    await ui.click(ui.button);
    assert.equal(ui.bottomButton.attrs.get('aria-pressed'), 'false');
    assert.equal(ui.bottomCount.textContent, '0 likes');
    assert.equal(ui.bottomButton.attrs.get('aria-label'), 'Like this article');
    assert.equal(ui.message.textContent, 'Like removed.');
    assert.equal(ui.bottomMessage.textContent, '');
});

test('both buttons share one pending request and cannot double-submit', async () => {
    let finish;
    const ui = await setup({requestLike: () => new Promise(resolve => { finish = resolve; })});
    const pending = ui.click(ui.bottomButton);
    assert.equal(ui.button.disabled, true);
    assert.equal(ui.bottomButton.disabled, true);
    await ui.click(ui.button);
    assert.equal(ui.requests.filter(request => request.url.endsWith('/like')).length, 1);
    finish({ok: true, json: async () => ({views: 1, shares: 0, likes: 1, liked: true})});
    await pending;
    assert.equal(ui.button.disabled, false);
    assert.equal(ui.bottomButton.disabled, false);
});

test('failed reactions show feedback beside the bottom button without changing counts', async () => {
    const ui = await setup({requestLike: async () => ({ok: false, status: 400})});
    await ui.click(ui.bottomButton);
    assert.match(ui.bottomMessage.textContent, /Please allow cookies/);
    assert.equal(ui.bottomCount.textContent, '0 likes');
    assert.equal(ui.bottomButton.attrs.get('aria-pressed'), 'false');
    assert.equal(ui.bottomButton.disabled, false);
    assert.equal(ui.message.textContent, '');
});

test('existing likes and back-forward refreshes update both controls', async () => {
    const ui = await setup({liked: true});
    assert.equal(ui.bottomButton.attrs.get('aria-label'), 'Unlike this article');
    assert.equal(ui.bottomCount.textContent, '1 like');
    ui.state.liked = false;
    ui.state.likes = 0;
    await ui.restore();
    assert.equal(ui.bottomCount.textContent, '0 likes');
    assert.equal(ui.button.attrs.get('aria-pressed'), 'false');
    assert.equal(ui.bottomButton.attrs.get('aria-pressed'), 'false');
});

test('keeps a focused bottom button available until focus leaves it', async () => {
    const ui = await setup();
    ui.reach(500);
    ui.document.activeElement = ui.bottomButton;
    ui.reach(900);
    assert.equal(ui.bottom.hidden, false);
    ui.document.activeElement = null;
    ui.bottom.listeners.focusout();
    assert.equal(ui.bottom.hidden, true);
});

test('keeps the original like button working without IntersectionObserver', async () => {
    const ui = await setup({observerSupported: false});
    assert.equal(ui.bottom.hidden, true);
    await ui.click(ui.button);
    assert.equal(ui.likeCount.textContent, '1 like');
});


test('both rows show views but revealing the footer does not record another visit', async () => {
    const ui = await setup();
    assert.equal(ui.viewCount.textContent, '1 view');
    assert.equal(ui.bottomViews.textContent, '1 view');
    ui.reach(500);
    ui.reach(900);
    ui.reach(500);
    ui.state.views = 12;
    ui.state.shares = 3;
    await ui.restore();
    assert.equal(ui.viewCount.textContent, '12 views');
    assert.equal(ui.bottomViews.textContent, '12 views');
    assert.equal(ui.shareCount.textContent, '3 shares');
    assert.equal(ui.bottomShares.textContent, '3 shares');
    assert.equal(ui.requests.filter(request => request.url.endsWith('/views')).length, 1);
});

test('a delayed like response preserves newer share totals in both rows', async () => {
    let finish;
    const ui = await setup({requestLike: () => new Promise(resolve => { finish = resolve; })});
    const pending = ui.click(ui.bottomButton);
    ui.bottomShares.textContent = '2 shares';
    finish({ok: true, json: async () => ({views: 1, shares: 0, likes: 1, liked: true})});
    await pending;
    assert.equal(ui.shareCount.textContent, '2 shares');
    assert.equal(ui.bottomShares.textContent, '2 shares');
});
