const {test} = require('node:test');
const assert = require('node:assert/strict');
const {readFileSync} = require('node:fs');
const {join} = require('node:path');
const {runInNewContext} = require('node:vm');

const script = readFileSync(join(__dirname, '../../main/resources/static/code-highlight.js'), 'utf8');

function setup(classNames, {known = ['javascript', 'jsx', 'js', 'java'], failOn = null} = {}) {
    const blocks = classNames.map(className => ({className}));
    const appended = [];
    const highlighted = [];
    const document = {
        querySelectorAll: selector => {
            assert.equal(selector, '.prose pre > code');
            return blocks;
        },
        createElement: tag => {
            const listeners = {};
            return {tag, listeners, addEventListener: (name, handler) => { listeners[name] = handler; }};
        },
        head: {appendChild: element => appended.push(element)}
    };
    const window = {};
    const hljs = {
        getLanguage: name => known.includes(name) ? {} : undefined,
        highlightElement: code => {
            if (code.className === failOn) throw new Error('boom');
            highlighted.push(code.className);
        }
    };
    runInNewContext(script, {document, window});
    const load = () => {
        window.hljs = hljs;
        appended[0].listeners.load();
    };
    return {appended, highlighted, load};
}

test('does not load the highlighter when no code block names a language', () => {
    const {appended} = setup(['', 'hljs-free']);
    assert.equal(appended.length, 0);
});

test('loads the vendored highlighter asynchronously when a tagged block exists', () => {
    const {appended} = setup(['language-java']);
    assert.equal(appended.length, 1);
    assert.equal(appended[0].tag, 'script');
    assert.equal(appended[0].src, '/vendor/highlight/highlight.min.js');
    assert.equal(appended[0].async, true);
});

test('highlights only blocks with a known language', () => {
    const {highlighted, load} = setup(['language-jsx', '', 'language-unknownlang', 'language-java']);
    load();
    assert.deepEqual(highlighted, ['language-jsx', 'language-java']);
});

test('keeps highlighting other blocks when one fails', () => {
    const {highlighted, load} = setup(['language-js', 'language-java'], {failOn: 'language-js'});
    load();
    assert.deepEqual(highlighted, ['language-java']);
});

test('does nothing if the library loads without defining hljs', () => {
    const {appended} = setup(['language-java']);
    assert.doesNotThrow(() => appended[0].listeners.load());
});
