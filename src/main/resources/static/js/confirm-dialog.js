/**
 * 공통 Confirm Dialog (shadcn AlertDialog 스타일)
 *
 * 사용법:
 *   showConfirm({
 *       title: '정말 삭제하시겠습니까?',
 *       description: '관련된 모든 데이터가 삭제됩니다.',
 *       icon: 'danger',           // 'danger' | 'warning' | 'info' (기본: 'danger')
 *       confirmText: 'Delete',    // 확인 버튼 텍스트 (기본: 'Confirm')
 *       cancelText: 'Cancel',     // 취소 버튼 텍스트 (기본: 'Cancel')
 *       confirmClass: 'danger',   // 확인 버튼 스타일 (기본: '' → 흰색, 'danger' → 빨강)
 *       onConfirm: function () { ... }
 *   });
 */
(function () {
    /* ── DOM 생성 (한 번만) ── */
    var overlay = document.createElement('div');
    overlay.className = 'confirm-overlay';
    overlay.id = 'confirmDialog';
    overlay.innerHTML =
        '<div class="confirm-dialog">' +
            '<div class="confirm-body">' +
                '<div class="confirm-body-row">' +
                    '<div class="confirm-icon" id="confirmIcon"></div>' +
                    '<div class="confirm-text">' +
                        '<p class="confirm-title" id="confirmTitle"></p>' +
                        '<p class="confirm-description" id="confirmDescription"></p>' +
                    '</div>' +
                '</div>' +
            '</div>' +
            '<div class="confirm-footer">' +
                '<button class="confirm-btn cancel" id="confirmCancelBtn" type="button"></button>' +
                '<button class="confirm-btn action" id="confirmActionBtn" type="button"></button>' +
            '</div>' +
        '</div>';
    document.addEventListener('DOMContentLoaded', function () {
        document.body.appendChild(overlay);
    });

    /* ── 아이콘 SVG ── */
    var icons = {
        danger: '<svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>',
        warning: '<svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>',
        info: '<svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>'
    };

    /* ── 공통 함수 ── */
    var currentCallback = null;

    function closeConfirm() {
        overlay.classList.remove('active');
        document.body.style.overflow = '';
        currentCallback = null;
    }

    window.showConfirm = function (opts) {
        var icon = opts.icon || 'danger';
        var confirmText = opts.confirmText || 'Confirm';
        var cancelText = opts.cancelText || 'Cancel';
        var confirmClass = opts.confirmClass || '';

        document.getElementById('confirmTitle').textContent = opts.title || '';
        document.getElementById('confirmDescription').textContent = opts.description || '';

        /* 아이콘 */
        var iconEl = document.getElementById('confirmIcon');
        iconEl.className = 'confirm-icon ' + icon;
        iconEl.innerHTML = icons[icon] || icons.danger;

        /* 버튼 */
        var actionBtn = document.getElementById('confirmActionBtn');
        actionBtn.textContent = confirmText;
        actionBtn.className = 'confirm-btn action ' + confirmClass;

        document.getElementById('confirmCancelBtn').textContent = cancelText;

        currentCallback = opts.onConfirm || null;

        overlay.classList.add('active');
        document.body.style.overflow = 'hidden';
    };

    window.closeConfirm = closeConfirm;

    /* ── 이벤트 ── */
    document.addEventListener('DOMContentLoaded', function () {
        /* Cancel 버튼 */
        document.getElementById('confirmCancelBtn').addEventListener('click', closeConfirm);

        /* Action 버튼 */
        document.getElementById('confirmActionBtn').addEventListener('click', function () {
            var cb = currentCallback;
            closeConfirm();
            if (cb) cb();
        });

        /* Overlay 클릭 */
        overlay.addEventListener('click', function (e) {
            if (e.target === overlay) closeConfirm();
        });

        /* ESC 키 */
        document.addEventListener('keydown', function (e) {
            if (e.key === 'Escape' && overlay.classList.contains('active')) {
                closeConfirm();
            }
        });
    });
})();
