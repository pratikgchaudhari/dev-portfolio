(() => {
    'use strict';

    // Progressive enhancement: without JavaScript, code blocks keep their plain styling.
    const languageClass = /(?:^|\s)language-([\w+#.-]+)/;
    const blocks = Array.from(document.querySelectorAll('.prose pre > code'))
        .filter(code => languageClass.test(code.className));
    if (blocks.length === 0) return;

    function highlight() {
        const hljs = window.hljs;
        if (!hljs) return;
        blocks.forEach(code => {
            // Only highlight blocks that name a known language; never guess from the content.
            if (!hljs.getLanguage(languageClass.exec(code.className)[1])) return;
            try {
                hljs.highlightElement(code);
            } catch (_) {
                // Leave the block as plain text if highlighting fails.
            }
        });
    }

    // Load the highlighter only on articles that contain a code block.
    const script = document.createElement('script');
    script.src = '/vendor/highlight/highlight.min.js';
    script.async = true;
    script.addEventListener('load', highlight);
    document.head.appendChild(script);
})();
