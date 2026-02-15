/**
 * 공통 Alert Modal (성공 / 에러 / 경고 알림)
 *
 * 사용법:
 *   showAlert({
 *       type: 'error',             // 'success' | 'error' | 'warning' (기본: 'error')
 *       message: '잔고가 부족합니다.',
 *       duration: 2000             // 자동 닫힘 ms (기본: 2000, 0이면 수동 닫기)
 *   });
 *
 *   showAlert('간단한 에러 메시지');  // 문자열만 전달하면 error 타입으로 표시
 */
(function () {
    /* ── DOM 생성 (한 번만) ── */
    var overlay = document.createElement('div');
    overlay.className = 'alert-modal-overlay';
    overlay.id = 'alertModal';
    overlay.innerHTML =
        '<div class="alert-modal">' +
            '<div class="alert-modal-body">' +
                '<div class="alert-modal-icon" id="alertModalIcon"></div>' +
                '<p class="alert-modal-message" id="alertModalMessage"></p>' +
            '</div>' +
        '</div>';
    document.addEventListener('DOMContentLoaded', function () {
        document.body.appendChild(overlay);

        overlay.addEventListener('click', function (e) {
            if (e.target === overlay) closeAlert();
        });
    });

    /* ── 아이콘 SVG ── */
    var icons = {
        success: '<svg viewBox="0 0 24 24" width="36" height="36" fill="none" stroke="#4ade80" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="8 12 11 15 16 9"/></svg>',
        error: '<svg viewBox="0 0 24 24" width="36" height="36" fill="none" stroke="#f87171" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>',
        warning: '<svg viewBox="0 0 24 24" width="36" height="36" fill="none" stroke="#fbbf24" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>'
    };

    var autoCloseTimer = null;

    function closeAlert() {
        if (autoCloseTimer) { clearTimeout(autoCloseTimer); autoCloseTimer = null; }
        overlay.classList.remove('active');
    }

    window.showAlert = function (opts) {
        if (typeof opts === 'string') { opts = { message: opts }; }
        var type = opts.type || 'error';
        var message = opts.message || '';
        var duration = opts.duration !== undefined ? opts.duration : 2000;

        document.getElementById('alertModalIcon').innerHTML = icons[type] || icons.error;
        document.getElementById('alertModalMessage').textContent = message;

        overlay.classList.add('active');

        if (autoCloseTimer) { clearTimeout(autoCloseTimer); autoCloseTimer = null; }
        if (duration > 0) {
            autoCloseTimer = setTimeout(function () { closeAlert(); }, duration);
        }
    };

    window.closeAlert = closeAlert;
})();
