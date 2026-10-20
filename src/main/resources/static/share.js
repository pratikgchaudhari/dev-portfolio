(() => {
    'use strict';

    const button = document.querySelector('[data-share-article]');
    if (!button) return;

    const label = button.querySelector('[data-share-label]');
    const status = document.querySelector('[data-share-status]');
    const fallback = document.querySelector('[data-share-fallback]');
    const field = fallback.querySelector('[data-share-input]');
    // The server supplies the canonical URL, or a clean article path when SITE_URL is unset.
    const url = new URL(button.dataset.shareUrl, window.location.origin).href;
    field.value = url;
    let busy = false;
    let resetTimer;

    function legacyCopy() {
        const active = document.activeElement;
        const selection = window.getSelection();
        const ranges = [];
        for (let i = 0; selection && i < selection.rangeCount; i++) {
            ranges.push(selection.getRangeAt(i).cloneRange());
        }
        const input = document.createElement('textarea');
        input.value = url;
        input.readOnly = true;
        input.tabIndex = -1;
        // Kept in the viewport for iOS selection, without affecting layout or scroll.
        input.style.cssText = 'position:fixed;top:0;left:0;width:1px;height:1px;opacity:0;font-size:16px';
        document.body.appendChild(input);
        try {
            input.focus({preventScroll: true});
            input.select();
            input.setSelectionRange(0, url.length);
            return document.execCommand('copy');
        } catch (_) {
            return false;
        } finally {
            input.remove();
            if (active) active.focus({preventScroll: true});
            if (selection) {
                selection.removeAllRanges();
                ranges.forEach(range => selection.addRange(range));
            }
        }
    }

    function reset() {
        button.classList.remove('is-copied');
        label.textContent = 'Share';
        status.textContent = '';
    }

    button.addEventListener('click', async () => {
        if (busy) return;
        busy = true;
        button.setAttribute('aria-busy', 'true');
        clearTimeout(resetTimer);
        reset();
        fallback.hidden = true;
        let copied = false;
        try {
            // Invoke directly in the click handler to preserve Safari’s user activation.
            if (navigator.clipboard && window.isSecureContext) {
                await navigator.clipboard.writeText(url);
                copied = true;
            } else {
                copied = legacyCopy();
            }
        } catch (_) {
            copied = legacyCopy();
        } finally {
            busy = false;
            button.removeAttribute('aria-busy');
        }

        if (copied) {
            // Restart the short animation when the reader copies again during confirmation.
            void button.offsetWidth;
            button.classList.add('is-copied');
            label.textContent = 'Link copied';
            status.textContent = 'Article link copied to clipboard.';
            resetTimer = setTimeout(reset, 2400);
        } else {
            fallback.hidden = false;
            field.focus();
            field.select();
            status.textContent = 'Couldn’t copy automatically. Copy the selected link below.';
        }
    });

    button.hidden = false;
})();
