/* ══════════════════════════════════
   Theme Manager (Dark / Light)
══════════════════════════════════ */

(function () {
    var STORAGE_KEY = 'folio-theme';

    function getTheme() {
        return localStorage.getItem(STORAGE_KEY) || 'dark';
    }

    function applyTheme(theme) {
        document.documentElement.setAttribute('data-theme', theme);
        localStorage.setItem(STORAGE_KEY, theme);

        // 토글 UI 동기화
        var toggles = document.querySelectorAll('[data-theme-toggle]');
        toggles.forEach(function (el) {
            el.checked = (theme === 'light');
        });
    }

    function toggleTheme() {
        var current = getTheme();
        applyTheme(current === 'dark' ? 'light' : 'dark');
    }

    // 즉시 적용 (FOUC 방지)
    applyTheme(getTheme());

    // 전역 함수로 노출
    window.FolioTheme = {
        toggle: toggleTheme,
        set: applyTheme,
        get: getTheme
    };
})();
