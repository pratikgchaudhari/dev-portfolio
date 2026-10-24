(() => {
    const rows = [...document.querySelectorAll('[data-article-engagement]')].map(panel => ({
        panel,
        button: panel.querySelector('.heart-button'),
        likeCount: panel.querySelector('[data-like-count]'),
        viewCount: panel.querySelector('[data-view-count]'),
        shareCount: panel.querySelector('[data-share-count]'),
        message: panel.querySelector('[data-engagement-message]')
    }));
    if (!rows.length) return;

    const {panel, button, message} = rows[0];
    const buttons = rows.map(row => row.button);
    const endActions = document.querySelector('[data-article-end-engagement]');
    let busy = false;
    let viewRequested = false;
    let celebrationTimer;

    function setBusy(value) {
        busy = value;
        buttons.forEach(control => { control.disabled = value; });
    }

    function stopCelebration() {
        clearTimeout(celebrationTimer);
        buttons.forEach(control => control.classList.remove('is-celebrating'));
    }

    function celebrateLike() {
        buttons.forEach(control => control.classList.add('is-celebrating'));
        celebrationTimer = setTimeout(stopCelebration, 750);
    }

    function render(stats) {
        buttons.forEach(control => {
            control.setAttribute('aria-pressed', String(stats.liked));
            control.setAttribute('aria-label', stats.liked ? 'Unlike this article' : 'Like this article');
        });
        // A delayed view/like response must not undo a newer share total in either row.
        const shares = Math.max(stats.shares, ...rows.map(row => parseInt(row.shareCount.textContent, 10) || 0));
        rows.forEach(row => {
            row.likeCount.textContent = `${stats.likes} ${stats.likes === 1 ? 'like' : 'likes'}`;
            row.viewCount.textContent = `${stats.views} ${stats.views === 1 ? 'view' : 'views'}`;
            row.shareCount.textContent = `${shares} ${shares === 1 ? 'share' : 'shares'}`;
        });
    }

    async function request(path, method, body) {
        const controller = new AbortController();
        const timeout = setTimeout(() => controller.abort(), 10000);
        try {
            const response = await fetch(`${panel.dataset.api}/${path}`, {
                method,
                credentials: 'same-origin',
                cache: 'no-store',
                headers: {'Content-Type': 'application/json', 'X-Portfolio-Request': '1'},
                body: body === undefined ? undefined : JSON.stringify(body),
                signal: controller.signal
            });
            if (!response.ok) {
                throw new Error(response.status === 400 ? 'Please allow cookies, then reload this page.' : 'Please try again.');
            }
            return await response.json();
        } finally {
            clearTimeout(timeout);
        }
    }

    async function recordView() {
        if (viewRequested || document.visibilityState !== 'visible') return;
        viewRequested = true;
        setBusy(true);
        try {
            render(await request('views', 'POST'));
        } catch (error) {
            message.textContent = 'The view count could not be updated. You can still read the article.';
        } finally {
            setBusy(false);
        }
    }

    async function toggleLike(event) {
        if (busy) return;
        stopCelebration();
        setBusy(true);
        rows.forEach(row => { row.message.textContent = ''; });
        const feedback = rows.find(row => row.button === event.currentTarget).message;
        const liked = button.getAttribute('aria-pressed') !== 'true';
        try {
            const stats = await request('like', 'PUT', {liked});
            render(stats);
            if (liked && stats.liked) celebrateLike();
            feedback.textContent = liked ? 'Thanks for the love!' : 'Like removed.';
        } catch (error) {
            feedback.textContent = `Your reaction could not be saved. ${error.message === 'Please allow cookies, then reload this page.' ? error.message : 'Please try again.'}`;
        } finally {
            setBusy(false);
        }
    }
    buttons.forEach(control => control.addEventListener('click', toggleLike));

    const articleEnd = document.querySelector('[data-article-reading-end]');
    if (endActions && articleEnd && 'IntersectionObserver' in window) {
        let atEnd = false;
        const updateEndActions = () => {
            // Preserve keyboard focus until the reader moves to another control.
            endActions.hidden = !atEnd && !endActions.contains(document.activeElement);
        };
        const observer = new IntersectionObserver(entries => {
            const entry = entries[entries.length - 1];
            // Keep the row visible after passing the marker, including restored scroll positions.
            atEnd = entry.boundingClientRect.top <= (entry.rootBounds?.bottom ?? window.innerHeight - 96);
            updateEndActions();
        }, {rootMargin: '0px 0px -96px 0px'});
        observer.observe(articleEnd);
        endActions.addEventListener('focusout', () => requestAnimationFrame(updateEndActions));
    }

    // Hidden or prefetched tabs count only when the reader actually opens them.
    document.addEventListener('visibilitychange', recordView);
    window.addEventListener('pageshow', async event => {
        if (!event.persisted || busy) return;
        setBusy(true);
        try {
            render(await request('stats', 'GET'));
        } catch (error) {
            message.textContent = 'Counts could not be refreshed. Please reload this page.';
        } finally {
            setBusy(false);
        }
    });
    recordView();
})();
