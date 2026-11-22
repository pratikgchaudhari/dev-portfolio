const {test} = require('node:test');
const assert = require('node:assert/strict');
const {readFileSync} = require('node:fs');
const {join} = require('node:path');
const {runInNewContext} = require('node:vm');

const script = readFileSync(join(__dirname, '../../main/resources/static/reading-progress.js'), 'utf8');

// articleEndTop is the marker's position in the document; the viewport is 800px tall.
function setup({articleEndTop = 2800, scrollY = 0, withBar = true, withEnd = true, resizeObserver = true} = {}) {
    const bar = {hidden: true, style: {}};
    const listeners = {};
    const frames = [];
    const observed = [];
    const window = {
        scrollY,
        innerHeight: 800,
        addEventListener: (name, handler, options) => { listeners[name] = {handler, options}; },
        requestAnimationFrame: callback => { frames.push(callback); return frames.length; }
    };
    if (resizeObserver) {
        window.ResizeObserver = class {
            constructor(callback) { this.callback = callback; }
            observe(target) { observed.push({target, callback: this.callback}); }
        };
    }
    const articleEnd = {getBoundingClientRect: () => ({top: articleEndTop - window.scrollY})};
    const body = {};
    const document = {
        body,
        querySelector: selector => ({
            '[data-reading-progress]': withBar ? bar : null,
            '[data-article-reading-end]': withEnd ? articleEnd : null
        })[selector]
    };
    runInNewContext(script, {window, document});
    const scrollTo = y => {
        window.scrollY = y;
        listeners.scroll.handler();
    };
    const runFrames = () => frames.splice(0).forEach(callback => callback());
    return {bar, window, listeners, frames, observed, body, scrollTo, runFrames};
}

const scale = bar => Number(/scaleX\(([\d.]+)\)/.exec(bar.style.transform)[1]);

test('shows the bar and starts empty at the top of the article', () => {
    const {bar} = setup();
    assert.equal(bar.hidden, false);
    assert.equal(scale(bar), 0);
});

test('fills in proportion to how far the article end is from the viewport bottom', () => {
    // Finished at scrollY 2000 (2800 - 800).
    const {bar, scrollTo, runFrames} = setup();
    scrollTo(500);
    runFrames();
    assert.equal(scale(bar), 0.25);
    scrollTo(1000);
    runFrames();
    assert.equal(scale(bar), 0.5);
    scrollTo(2000);
    runFrames();
    assert.equal(scale(bar), 1);
});

test('stays full while reading related articles and the footer', () => {
    const {bar, scrollTo, runFrames} = setup();
    scrollTo(3500);
    runFrames();
    assert.equal(scale(bar), 1);
});

test('restored scroll positions render the correct progress immediately', () => {
    const {bar} = setup({scrollY: 1500});
    assert.equal(scale(bar), 0.75);
});

test('a short article that fits on screen is already complete', () => {
    const {bar} = setup({articleEndTop: 600});
    assert.equal(scale(bar), 1);
});

test('batches scroll events into one update per animation frame', () => {
    const {bar, scrollTo, frames, runFrames} = setup();
    scrollTo(200);
    scrollTo(400);
    scrollTo(1000);
    assert.equal(frames.length, 1);
    runFrames();
    assert.equal(scale(bar), 0.5);
});

test('listens to scroll passively and reacts to layout changes', () => {
    const {listeners, observed, body} = setup();
    assert.equal(listeners.scroll.options.passive, true);
    assert.ok(listeners.resize && listeners.load);
    assert.equal(observed.length, 1);
    assert.equal(observed[0].target, body);
});

test('works without ResizeObserver', () => {
    const {bar} = setup({resizeObserver: false});
    assert.equal(bar.hidden, false);
});

test('does nothing when the bar or the article end marker is missing', () => {
    assert.equal(setup({withBar: false}).listeners.scroll, undefined);
    const {bar, listeners} = setup({withEnd: false});
    assert.equal(bar.hidden, true);
    assert.equal(listeners.scroll, undefined);
});
