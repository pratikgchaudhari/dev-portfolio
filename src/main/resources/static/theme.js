(() => {
    'use strict';

    const storageKey = 'portfolio-theme';
    const root = document.documentElement;
    const systemTheme = window.matchMedia('(prefers-color-scheme: dark)');
    const validTheme = value => value === 'dark' || value === 'light';
    let preference = null;
    let button;

    try {
        const saved = localStorage.getItem(storageKey);
        if (validTheme(saved)) preference = saved;
    } catch (_) {
        // The switch still works when browser storage is unavailable.
    }

    function applyTheme() {
        const theme = preference || (systemTheme.matches ? 'dark' : 'light');
        root.dataset.theme = theme;
        if (button) {
            const next = theme === 'dark' ? 'light' : 'dark';
            button.setAttribute('aria-label', `Switch to ${next} mode`);
            button.querySelector('[data-theme-label]').textContent = next === 'dark' ? 'Dark' : 'Light';
        }
    }

    // Run in the head, before styles load, to avoid flashing the wrong theme.
    applyTheme();
    systemTheme.addEventListener('change', () => {
        if (!preference) applyTheme();
    });
    window.addEventListener('storage', event => {
        if (event.key !== storageKey && event.key !== null) return;
        preference = validTheme(event.newValue) ? event.newValue : null;
        applyTheme();
    });
    window.addEventListener('pageshow', applyTheme);

    document.addEventListener('DOMContentLoaded', () => {
        button = document.querySelector('[data-theme-toggle]');
        if (!button) return;
        applyTheme();
        button.hidden = false;
        button.addEventListener('click', () => {
            preference = root.dataset.theme === 'dark' ? 'light' : 'dark';
            applyTheme();
            try {
                localStorage.setItem(storageKey, preference);
            } catch (_) {
                // Keep the selected theme for this page even if it cannot be saved.
            }
        });
    });
})();
