(() => {
    const panel = document.querySelector('[data-article-engagement]');
    if (!panel) return;

    const button = panel.querySelector('.heart-button');
    const likeCount = panel.querySelector('[data-like-count]');
    const viewCount = panel.querySelector('[data-view-count]');
    const message = panel.querySelector('[data-engagement-message]');
    let busy = false;
    let viewRequested = false;

    function render(stats) {
        button.setAttribute('aria-pressed', String(stats.liked));
        button.setAttribute('aria-label', stats.liked ? 'Unlike this article' : 'Like this article');
        likeCount.textContent = `${stats.likes} ${stats.likes === 1 ? 'like' : 'likes'}`;
        viewCount.textContent = `${stats.views} ${stats.views === 1 ? 'view' : 'views'}`;
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
        busy = true;
        button.disabled = true;
        try {
            render(await request('views', 'POST'));
        } catch (error) {
            message.textContent = 'The view count could not be updated. You can still read the article.';
        } finally {
            busy = false;
            button.disabled = false;
        }
    }

    button.addEventListener('click', async () => {
        if (busy) return;
        busy = true;
        button.disabled = true;
        message.textContent = '';
        const liked = button.getAttribute('aria-pressed') !== 'true';
        try {
            render(await request('like', 'PUT', {liked}));
            message.textContent = liked ? 'Thanks for the love!' : 'Like removed.';
        } catch (error) {
            message.textContent = `Your reaction could not be saved. ${error.message === 'Please allow cookies, then reload this page.' ? error.message : 'Please try again.'}`;
        } finally {
            busy = false;
            button.disabled = false;
        }
    });

    // Hidden or prefetched tabs count only when the reader actually opens them.
    document.addEventListener('visibilitychange', recordView);
    window.addEventListener('pageshow', async event => {
        if (!event.persisted || busy) return;
        busy = true;
        button.disabled = true;
        try {
            render(await request('stats', 'GET'));
        } catch (error) {
            message.textContent = 'Counts could not be refreshed. Please reload this page.';
        } finally {
            busy = false;
            button.disabled = false;
        }
    });
    recordView();
})();
