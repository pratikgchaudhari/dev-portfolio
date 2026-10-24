(() => {
    'use strict';

    const panels = [...document.querySelectorAll('[data-article-engagement]')];
    if (!panels.length) return;
    const counts = panels.map(panel => panel.querySelector('[data-share-count]'));
    let busy = false;
    let copySequence = 0;

    for (const panel of panels) {
        const button = panel.querySelector('[data-share-article]');
        const label = button.querySelector('[data-share-label]');
        const status = panel.querySelector('[data-share-status]');
        const fallback = panel.querySelector('[data-share-fallback]');
        const field = fallback.querySelector('[data-share-input]');
        const countMessage = panel.querySelector('[data-share-count-message]');
        // The server supplies the canonical URL, or a clean article path when SITE_URL is unset.
        const url = new URL(button.dataset.shareUrl, window.location.origin).href;
        field.value = url;
        let resetTimer;

        async function recordShare(sequence) {
            const controller = new AbortController();
            const timeout = setTimeout(() => controller.abort(), 10000);
            try {
                const response = await fetch(`${panel.dataset.api}/shares`, {
                    method: 'POST',
                    credentials: 'same-origin',
                    cache: 'no-store',
                    headers: {'X-Portfolio-Request': '1'},
                    keepalive: true,
                    signal: controller.signal
                });
                if (!response.ok) throw new Error('Share count could not be saved');
                const stats = await response.json();
                // Concurrent copies and view/like responses can arrive out of order.
                const shares = Math.max(stats.shares, ...counts.map(count => parseInt(count.textContent, 10) || 0));
                counts.forEach(count => { count.textContent = `${shares} ${shares === 1 ? 'share' : 'shares'}`; });
            } catch (_) {
                // Copying still succeeded. Do not retry: the server may have saved the count.
                if (sequence === copySequence) {
                    countMessage.textContent = 'Link copied. The share count could not be updated.';
                }
            } finally {
                clearTimeout(timeout);
            }
        }

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
            const sequence = ++copySequence;
            panels.forEach(row => { row.querySelector('[data-share-count-message]').textContent = ''; });
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
                await recordShare(sequence);
            } else {
                fallback.hidden = false;
                field.focus();
                field.select();
                status.textContent = 'Couldn’t copy automatically. Copy the selected link below.';
            }
        });

        button.hidden = false;
    }
})();
