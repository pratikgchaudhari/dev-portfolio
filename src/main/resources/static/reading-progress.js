(() => {
    'use strict';

    // The bar stays hidden without JavaScript; the article reads the same either way.
    const bar = document.querySelector('[data-reading-progress]');
    const articleEnd = document.querySelector('[data-article-reading-end]');
    if (!bar || !articleEnd) return;

    let frame = 0;

    function progress() {
        // Finished when the end of the article body reaches the bottom of the viewport,
        // so related articles and the footer do not count as unread text.
        const scrolled = window.scrollY;
        const finish = scrolled + articleEnd.getBoundingClientRect().top - window.innerHeight;
        if (finish <= 0) return 1;
        return Math.min(1, Math.max(0, scrolled / finish));
    }

    function update() {
        frame = 0;
        bar.style.transform = `scaleX(${progress().toFixed(4)})`;
    }

    function schedule() {
        if (!frame) frame = window.requestAnimationFrame(update);
    }

    window.addEventListener('scroll', schedule, {passive: true});
    window.addEventListener('resize', schedule);
    window.addEventListener('load', schedule);
    // Lazy images and highlighted code can change the article height without scrolling.
    if ('ResizeObserver' in window) new window.ResizeObserver(schedule).observe(document.body);

    update();
    bar.hidden = false;
})();
