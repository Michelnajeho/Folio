/* ══════════════════════════════════
   Scene Transitions
══════════════════════════════════ */
var currentScene = 'landing'; // landing | login | join
var transitioning = false;
var TRANSITION_MS = 350;

function goLogin() {
    currentScene = 'login';
    document.getElementById('landing').classList.add('fade-out');
    document.getElementById('shader-bg').classList.add('dimmed');
    document.getElementById('loginScene').classList.add('active');
    document.getElementById('joinScene').classList.remove('active');
    document.getElementById('joinScene').classList.remove('join-active');
    document.getElementById('btnBack').classList.add('visible');
    setTimeout(function () {
        document.getElementById('username').focus();
    }, 600);
}

function goJoin() {
    if (transitioning) return;
    transitioning = true;
    currentScene = 'join';
    document.getElementById('loginScene').classList.remove('active');
    setTimeout(function () {
        document.getElementById('joinScene').classList.add('active');
        document.getElementById('joinScene').classList.add('join-active');
        transitioning = false;
        setTimeout(function () {
            document.getElementById('joinLoginId').focus();
        }, 200);
    }, TRANSITION_MS);
}

function goLoginFromJoin() {
    if (transitioning) return;
    transitioning = true;
    currentScene = 'login';
    document.getElementById('joinScene').classList.remove('active');
    document.getElementById('joinScene').classList.remove('join-active');
    setTimeout(function () {
        document.getElementById('loginScene').classList.add('active');
        transitioning = false;
        setTimeout(function () {
            document.getElementById('username').focus();
        }, 200);
    }, TRANSITION_MS);
}

function goBack() {
    if (transitioning) return;
    if (currentScene === 'join') {
        transitioning = true;
        currentScene = 'landing';
        document.getElementById('joinScene').classList.remove('active');
        document.getElementById('joinScene').classList.remove('join-active');
        document.getElementById('btnBack').classList.remove('visible');
        setTimeout(function () {
            document.getElementById('landing').classList.remove('fade-out');
            document.getElementById('shader-bg').classList.remove('dimmed');
            transitioning = false;
        }, TRANSITION_MS);
        return;
    }
    currentScene = 'landing';
    document.getElementById('landing').classList.remove('fade-out');
    document.getElementById('shader-bg').classList.remove('dimmed');
    document.getElementById('loginScene').classList.remove('active');
    document.getElementById('btnBack').classList.remove('visible');
}

/* 씬 바깥 클릭 시 뒤로가기 */
document.getElementById('loginScene').addEventListener('click', function (e) {
    if (e.target === this) { goBack(); }
});
document.getElementById('joinScene').addEventListener('click', function (e) {
    if (e.target === this) { goBack(); }
});

/* 에러 시 자동으로 로그인 화면 */
if (window.location.search.indexOf('error') !== -1) {
    goLogin();
}

/* ══════════════════════════════════
   Login Form — AJAX Submit
══════════════════════════════════ */
window.submitLoginForm = function (e) {
    e.preventDefault();

    var usernameVal = document.getElementById('username').value.trim();
    var passwordVal = document.getElementById('password').value;
    if (!usernameVal || !passwordVal) return false;

    var btn = document.getElementById('btnLoginSubmit');
    btn.disabled = true;
    btn.textContent = 'Signing in...';

    var csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    var csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
    var headers = { 'Content-Type': 'application/x-www-form-urlencoded' };
    headers[csrfHeader] = csrfToken;

    fetch('/login', {
        method: 'POST',
        headers: headers,
        body: 'username=' + encodeURIComponent(usernameVal) + '&password=' + encodeURIComponent(passwordVal)
    })
    .then(function (r) { return r.json(); })
    .then(function (res) {
        if (res.success) {
            document.getElementById('loginError').style.display = 'none';
            /* 로그인 씬 페이드아웃 → 날짜 인트로 */
            document.getElementById('loginScene').classList.remove('active');
            document.getElementById('btnBack').classList.remove('visible');
            setTimeout(function () {
                showDateIntro();
            }, TRANSITION_MS);
        } else {
            document.getElementById('loginError').style.display = 'block';
            btn.disabled = false;
            btn.textContent = 'Sign In';
        }
    })
    .catch(function () {
        document.getElementById('loginError').style.display = 'block';
        btn.disabled = false;
        btn.textContent = 'Sign In';
    });

    return false;
};

/* ══════════════════════════════════
   Date Wheel Intro
══════════════════════════════════ */
function showDateIntro() {
    var ITEM_HEIGHT = 60;
    var VISIBLE_ITEMS = 3; /* 위 1 + 가운데 1 + 아래 1 */
    var DOW_NAMES = ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT'];

    var QUOTES = [
        { text: '추세는 당신의 친구입니다.', author: 'Ed Seykota' },
        { text: '손실은 짧게, 수익은 길게.', author: '' },
        { text: '매매를 계획하고, 계획대로 매매하라.', author: '' },
        { text: '리스크는 자신이 뭘 하고 있는지 모를 때 생긴다.', author: 'Warren Buffett' },
        { text: '시장은 당신이 버틸 수 있는 것보다 더 오래 비합리적일 수 있다.', author: 'John Maynard Keynes' },
        { text: '트레이딩에서 불가능한 일은 1년에 두 번쯤 일어난다.', author: 'Henri M. Simoes' },
        { text: '성공적인 트레이더의 목표는 최선의 매매를 하는 것이다.', author: 'Alexander Elder' },
        { text: '규율은 목표와 성취 사이의 다리이다.', author: 'Jim Rohn' },
        { text: '옳고 그름이 중요한 게 아니라, 옳을 때 얼마나 버느냐가 중요하다.', author: 'George Soros' },
        { text: '주식시장은 참을성 없는 사람의 돈을 참을성 있는 사람에게 옮기는 장치이다.', author: 'Warren Buffett' }
    ];

    /* 랜덤 격언 선택 */
    var pick = QUOTES[Math.floor(Math.random() * QUOTES.length)];
    document.getElementById('dateQuote').textContent = '"' + pick.text + '"';
    var authorEl = document.getElementById('dateAuthor');
    authorEl.textContent = pick.author ? '— ' + pick.author : '';
    authorEl.style.display = pick.author ? '' : 'none';

    var now = new Date();
    var yesterday = new Date(now);
    yesterday.setDate(yesterday.getDate() - 1);

    var targetYear = now.getFullYear();
    var targetMonth = now.getMonth() + 1;
    var targetDay = now.getDate();
    var targetDow = now.getDay();

    /* 각 휠에 들어갈 값 생성 */
    function buildItems(values, targetIdx) {
        /* targetIdx 위치에 타겟 값. 앞뒤로 버퍼 */
        var items = [];
        for (var i = 0; i < values.length; i++) {
            items.push(values[i]);
        }
        return { items: items, targetIdx: targetIdx };
    }

    /* 연도: 작년 ~ 내년 */
    var years = [targetYear - 2, targetYear - 1, targetYear, targetYear + 1, targetYear + 2];
    var yearData = buildItems(years, 2);

    /* 월: 1~12 */
    var months = [];
    for (var m = 1; m <= 12; m++) months.push(m);
    var monthData = buildItems(months, targetMonth - 1);

    /* 일: 전날 주변부터 오늘까지 */
    var daysInMonth = new Date(targetYear, targetMonth, 0).getDate();
    var days = [];
    for (var d = 1; d <= daysInMonth; d++) days.push(d);
    var dayData = buildItems(days, targetDay - 1);

    /* 요일: 전체 7일 */
    var dowData = buildItems(DOW_NAMES, targetDow);

    /* 어제 인덱스 */
    var yesterdayDayIdx = yesterday.getDate() - 1;
    var yesterdayDowIdx = yesterday.getDay();
    var yesterdayMonthIdx = yesterday.getMonth(); /* 0-based month, 이미 0-indexed */
    var yesterdayYearIdx = years.indexOf(yesterday.getFullYear());

    /* 휠 스트립 채우기 */
    function populateWheel(wheelId, data, formatFn) {
        var strip = document.querySelector('#' + wheelId + ' .date-wheel-strip');
        strip.innerHTML = '';
        for (var i = 0; i < data.items.length; i++) {
            var div = document.createElement('div');
            div.className = 'wheel-item';
            div.textContent = formatFn ? formatFn(data.items[i]) : data.items[i];
            strip.appendChild(div);
        }
        return strip;
    }

    function pad2(n) { return n < 10 ? '0' + n : '' + n; }

    var yearStrip = populateWheel('wheelYear', yearData, function (v) { return '' + v; });
    var monthStrip = populateWheel('wheelMonth', monthData, pad2);
    var dayStrip = populateWheel('wheelDay', dayData, pad2);
    var dowStrip = populateWheel('wheelDow', dowData, function (v) { return v; });

    /* 초기 위치: 어제 날짜 */
    function setPosition(strip, idx) {
        var offset = -(idx * ITEM_HEIGHT) + ITEM_HEIGHT; /* 가운데 = 1번째 슬롯 (0-indexed 에서 offset) */
        strip.style.transition = 'none';
        strip.style.transform = 'translateY(' + offset + 'px)';
    }

    function setActiveItem(strip, idx) {
        var items = strip.querySelectorAll('.wheel-item');
        for (var i = 0; i < items.length; i++) {
            items[i].classList.remove('active');
        }
        if (items[idx]) items[idx].classList.add('active');
    }

    /* 초기: 어제 위치 */
    setPosition(yearStrip, yesterdayYearIdx);
    setActiveItem(yearStrip, yesterdayYearIdx);
    setPosition(monthStrip, yesterdayMonthIdx);
    setActiveItem(monthStrip, yesterdayMonthIdx);
    setPosition(dayStrip, yesterdayDayIdx);
    setActiveItem(dayStrip, yesterdayDayIdx);
    setPosition(dowStrip, yesterdayDowIdx);
    setActiveItem(dowStrip, yesterdayDowIdx);

    /* 인트로 씬 페이드인 */
    var dateIntro = document.getElementById('dateIntro');
    dateIntro.classList.add('active');

    /* 잠시 후 오늘 날짜로 스핀 */
    setTimeout(function () {
        function spinTo(strip, targetIdx, delay) {
            setTimeout(function () {
                var offset = -(targetIdx * ITEM_HEIGHT) + ITEM_HEIGHT;
                strip.style.transition = 'transform 1.0s cubic-bezier(0.22, 1, 0.36, 1)';
                strip.style.transform = 'translateY(' + offset + 'px)';
                setActiveItem(strip, targetIdx);
            }, delay);
        }

        spinTo(yearStrip, yearData.targetIdx, 0);
        spinTo(monthStrip, monthData.targetIdx, 100);
        spinTo(dayStrip, dayData.targetIdx, 200);
        spinTo(dowStrip, dowData.targetIdx, 300);

        /* 애니메이션 완료 후 대시보드로 이동 (대시보드 page-entry-overlay가 자연스럽게 페이드인 처리) */
        setTimeout(function () {
            window.location.href = '/dashboard';
        }, 1800);

    }, 500);
}

/* ══════════════════════════════════
   Join Form — Validation
══════════════════════════════════ */
(function () {
    var fields = {
        loginId:         { valid: false },
        password:        { valid: false },
        passwordConfirm: { valid: false },
        nickname:        { valid: false },
        email:           { valid: false }
    };

    var ID_REGEX = /^[a-zA-Z0-9]{4,20}$/;
    var EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    /* ── helpers ── */
    function setMsg(id, text, type) {
        var el = document.getElementById(id);
        el.textContent = text;
        el.className = 'field-msg ' + type;
    }

    function clearMsg(id) {
        var el = document.getElementById(id);
        el.textContent = '';
        el.className = 'field-msg';
    }

    function setInputState(input, state) {
        input.classList.remove('input-error', 'input-success');
        if (state) input.classList.add(state);
    }

    function updateSubmitBtn() {
        var allValid = true;
        for (var key in fields) {
            if (!fields[key].valid) { allValid = false; break; }
        }
        document.getElementById('btnJoinSubmit').disabled = !allValid;
    }

    /* ── ID ── */
    var loginIdInput = document.getElementById('joinLoginId');
    var idCheckTimer = null;

    loginIdInput.addEventListener('blur', function () { validateLoginId(); });
    loginIdInput.addEventListener('input', function () {
        clearTimeout(idCheckTimer);
        var val = this.value.trim();
        if (val.length === 0) {
            clearMsg('msgLoginId');
            setInputState(this, null);
            fields.loginId.valid = false;
            updateSubmitBtn();
            return;
        }
        if (!ID_REGEX.test(val)) {
            setMsg('msgLoginId', '4~20자 영문, 숫자만 사용 가능합니다.', 'error');
            setInputState(this, 'input-error');
            fields.loginId.valid = false;
            updateSubmitBtn();
            return;
        }
        clearMsg('msgLoginId');
        setInputState(this, null);
        idCheckTimer = setTimeout(function () { checkLoginIdDup(val); }, 500);
    });

    function validateLoginId() {
        var val = loginIdInput.value.trim();
        if (val.length === 0) {
            setMsg('msgLoginId', 'ID를 입력해주세요.', 'error');
            setInputState(loginIdInput, 'input-error');
            fields.loginId.valid = false;
            updateSubmitBtn();
            return;
        }
        if (!ID_REGEX.test(val)) {
            setMsg('msgLoginId', '4~20자 영문, 숫자만 사용 가능합니다.', 'error');
            setInputState(loginIdInput, 'input-error');
            fields.loginId.valid = false;
            updateSubmitBtn();
            return;
        }
        checkLoginIdDup(val);
    }

    function checkLoginIdDup(val) {
        fetch('/api/member/check-login-id?loginId=' + encodeURIComponent(val))
            .then(function (r) { return r.json(); })
            .then(function (res) {
                if (res.data.available) {
                    setMsg('msgLoginId', '사용 가능한 ID입니다.', 'success');
                    setInputState(loginIdInput, 'input-success');
                    fields.loginId.valid = true;
                } else {
                    setMsg('msgLoginId', '이미 사용 중인 ID입니다.', 'error');
                    setInputState(loginIdInput, 'input-error');
                    fields.loginId.valid = false;
                }
                updateSubmitBtn();
            })
            .catch(function () {
                fields.loginId.valid = false;
                updateSubmitBtn();
            });
    }

    /* ── Password ── */
    var pwInput = document.getElementById('joinPassword');

    pwInput.addEventListener('blur', function () { validatePassword(); });
    pwInput.addEventListener('input', function () {
        var val = this.value;
        if (val.length === 0) {
            clearMsg('msgPassword');
            setInputState(this, null);
            fields.password.valid = false;
        } else if (val.length < 8) {
            setMsg('msgPassword', '8자 이상 입력해주세요.', 'error');
            setInputState(this, 'input-error');
            fields.password.valid = false;
        } else {
            setMsg('msgPassword', '', 'success');
            clearMsg('msgPassword');
            setInputState(this, 'input-success');
            fields.password.valid = true;
        }
        updateSubmitBtn();
        if (document.getElementById('joinPasswordConfirm').value.length > 0) {
            validatePasswordConfirm();
        }
    });

    function validatePassword() {
        var val = pwInput.value;
        if (val.length === 0) {
            setMsg('msgPassword', '비밀번호를 입력해주세요.', 'error');
            setInputState(pwInput, 'input-error');
            fields.password.valid = false;
        } else if (val.length < 8) {
            setMsg('msgPassword', '8자 이상 입력해주세요.', 'error');
            setInputState(pwInput, 'input-error');
            fields.password.valid = false;
        } else {
            clearMsg('msgPassword');
            setInputState(pwInput, 'input-success');
            fields.password.valid = true;
        }
        updateSubmitBtn();
    }

    /* ── Password Confirm ── */
    var pwConfirmInput = document.getElementById('joinPasswordConfirm');

    pwConfirmInput.addEventListener('blur', function () { validatePasswordConfirm(); });
    pwConfirmInput.addEventListener('input', function () { validatePasswordConfirm(); });

    function validatePasswordConfirm() {
        var val = pwConfirmInput.value;
        var pw = pwInput.value;
        if (val.length === 0) {
            clearMsg('msgPasswordConfirm');
            setInputState(pwConfirmInput, null);
            fields.passwordConfirm.valid = false;
        } else if (val !== pw) {
            setMsg('msgPasswordConfirm', '비밀번호가 일치하지 않습니다.', 'error');
            setInputState(pwConfirmInput, 'input-error');
            fields.passwordConfirm.valid = false;
        } else {
            setMsg('msgPasswordConfirm', '비밀번호가 일치합니다.', 'success');
            setInputState(pwConfirmInput, 'input-success');
            fields.passwordConfirm.valid = true;
        }
        updateSubmitBtn();
    }

    /* ── Nickname ── */
    var nickInput = document.getElementById('joinNickname');

    nickInput.addEventListener('blur', function () { validateNickname(); });
    nickInput.addEventListener('input', function () {
        var val = this.value.trim();
        if (val.length === 0) {
            clearMsg('msgNickname');
            setInputState(this, null);
            fields.nickname.valid = false;
        } else if (val.length < 2 || val.length > 30) {
            setMsg('msgNickname', '2~30자로 입력해주세요.', 'error');
            setInputState(this, 'input-error');
            fields.nickname.valid = false;
        } else {
            clearMsg('msgNickname');
            setInputState(this, 'input-success');
            fields.nickname.valid = true;
        }
        updateSubmitBtn();
    });

    function validateNickname() {
        var val = nickInput.value.trim();
        if (val.length === 0) {
            setMsg('msgNickname', '닉네임을 입력해주세요.', 'error');
            setInputState(nickInput, 'input-error');
            fields.nickname.valid = false;
        } else if (val.length < 2 || val.length > 30) {
            setMsg('msgNickname', '2~30자로 입력해주세요.', 'error');
            setInputState(nickInput, 'input-error');
            fields.nickname.valid = false;
        } else {
            clearMsg('msgNickname');
            setInputState(nickInput, 'input-success');
            fields.nickname.valid = true;
        }
        updateSubmitBtn();
    }

    /* ── Email ── */
    var emailInput = document.getElementById('joinEmail');
    var emailCheckTimer = null;

    emailInput.addEventListener('blur', function () { validateEmail(); });
    emailInput.addEventListener('input', function () {
        clearTimeout(emailCheckTimer);
        var val = this.value.trim();
        if (val.length === 0) {
            clearMsg('msgEmail');
            setInputState(this, null);
            fields.email.valid = false;
            updateSubmitBtn();
            return;
        }
        if (!EMAIL_REGEX.test(val)) {
            setMsg('msgEmail', '올바른 이메일 형식이 아닙니다.', 'error');
            setInputState(this, 'input-error');
            fields.email.valid = false;
            updateSubmitBtn();
            return;
        }
        clearMsg('msgEmail');
        setInputState(this, null);
        emailCheckTimer = setTimeout(function () { checkEmailDup(val); }, 500);
    });

    function validateEmail() {
        var val = emailInput.value.trim();
        if (val.length === 0) {
            setMsg('msgEmail', '이메일을 입력해주세요.', 'error');
            setInputState(emailInput, 'input-error');
            fields.email.valid = false;
            updateSubmitBtn();
            return;
        }
        if (!EMAIL_REGEX.test(val)) {
            setMsg('msgEmail', '올바른 이메일 형식이 아닙니다.', 'error');
            setInputState(emailInput, 'input-error');
            fields.email.valid = false;
            updateSubmitBtn();
            return;
        }
        checkEmailDup(val);
    }

    function checkEmailDup(val) {
        fetch('/api/member/check-email?email=' + encodeURIComponent(val))
            .then(function (r) { return r.json(); })
            .then(function (res) {
                if (res.data.available) {
                    setMsg('msgEmail', '사용 가능한 이메일입니다.', 'success');
                    setInputState(emailInput, 'input-success');
                    fields.email.valid = true;
                } else {
                    setMsg('msgEmail', '이미 사용 중인 이메일입니다.', 'error');
                    setInputState(emailInput, 'input-error');
                    fields.email.valid = false;
                }
                updateSubmitBtn();
            })
            .catch(function () {
                fields.email.valid = false;
                updateSubmitBtn();
            });
    }

    /* ── Submit ── */
    window.submitJoinForm = function (e) {
        e.preventDefault();

        for (var key in fields) {
            if (!fields[key].valid) return false;
        }

        var btn = document.getElementById('btnJoinSubmit');
        btn.disabled = true;
        btn.textContent = 'Processing...';

        var body = {
            loginId: loginIdInput.value.trim(),
            password: pwInput.value,
            passwordConfirm: pwConfirmInput.value,
            nickname: nickInput.value.trim(),
            email: emailInput.value.trim()
        };

        var csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        var csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
        var headers = { 'Content-Type': 'application/json' };
        headers[csrfHeader] = csrfToken;

        fetch('/api/member/join', {
            method: 'POST',
            headers: headers,
            body: JSON.stringify(body)
        })
        .then(function (r) { return r.json(); })
        .then(function (res) {
            if (res.success) {
                alert('회원가입이 완료되었습니다. 로그인해주세요.');
                document.getElementById('joinForm').reset();
                resetFieldStates();
                goLoginFromJoin();
            } else {
                alert(res.message || '회원가입에 실패했습니다.');
                btn.disabled = false;
                btn.textContent = 'Sign Up';
            }
        })
        .catch(function () {
            alert('서버 오류가 발생했습니다. 다시 시도해주세요.');
            btn.disabled = false;
            btn.textContent = 'Sign Up';
        });

        return false;
    };

    function resetFieldStates() {
        for (var key in fields) { fields[key].valid = false; }
        var ids = ['msgLoginId', 'msgPassword', 'msgPasswordConfirm', 'msgNickname', 'msgEmail'];
        ids.forEach(function (id) { clearMsg(id); });
        var inputs = [loginIdInput, pwInput, pwConfirmInput, nickInput, emailInput];
        inputs.forEach(function (input) { setInputState(input, null); });
        updateSubmitBtn();
    }
})();

/* ══════════════════════════════════
   Three.js Shader Background
══════════════════════════════════ */
(function () {
    var container = document.getElementById('shader-bg');

    var camera = new THREE.Camera();
    camera.position.z = 1;

    var scene = new THREE.Scene();
    var geometry = new THREE.PlaneBufferGeometry(2, 2);

    var uniforms = {
        time:       { type: 'f', value: 1.0 },
        resolution: { type: 'v2', value: new THREE.Vector2() }
    };

    var vertexShader = [
        'void main() {',
        '    gl_Position = vec4(position, 1.0);',
        '}'
    ].join('\n');

    var fragmentShader = [
        '#define TWO_PI 6.2831853072',
        '#define PI 3.14159265359',
        'precision highp float;',
        'uniform vec2 resolution;',
        'uniform float time;',
        '',
        'float random(in float x) {',
        '    return fract(sin(x) * 1e4);',
        '}',
        '',
        'float random(vec2 st) {',
        '    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);',
        '}',
        '',
        'void main(void) {',
        '    vec2 uv = (gl_FragCoord.xy * 2.0 - resolution.xy) / min(resolution.x, resolution.y);',
        '',
        '    vec2 fMosaicScal = vec2(4.0, 2.0);',
        '    vec2 vScreenSize = vec2(256.0, 256.0);',
        '    uv.x = floor(uv.x * vScreenSize.x / fMosaicScal.x) / (vScreenSize.x / fMosaicScal.x);',
        '    uv.y = floor(uv.y * vScreenSize.y / fMosaicScal.y) / (vScreenSize.y / fMosaicScal.y);',
        '',
        '    float t = time * 0.06 + random(uv.x) * 0.4;',
        '    float lineWidth = 0.0008;',
        '    vec3 color = vec3(0.0);',
        '    for (int j = 0; j < 3; j++) {',
        '        for (int i = 0; i < 5; i++) {',
        '            color[j] += lineWidth * float(i * i) / abs(fract(t - 0.01 * float(j) + float(i) * 0.01) * 1.0 - length(uv));',
        '        }',
        '    }',
        '    gl_FragColor = vec4(color[2], color[1], color[0], 1.0);',
        '}'
    ].join('\n');

    var material = new THREE.ShaderMaterial({
        uniforms: uniforms,
        vertexShader: vertexShader,
        fragmentShader: fragmentShader
    });

    var mesh = new THREE.Mesh(geometry, material);
    scene.add(mesh);

    var renderer = new THREE.WebGLRenderer();
    renderer.setPixelRatio(window.devicePixelRatio);
    container.appendChild(renderer.domElement);

    function onResize() {
        renderer.setSize(window.innerWidth, window.innerHeight);
        uniforms.resolution.value.x = renderer.domElement.width;
        uniforms.resolution.value.y = renderer.domElement.height;
    }
    onResize();
    window.addEventListener('resize', onResize, false);

    function animate() {
        requestAnimationFrame(animate);
        uniforms.time.value += 0.05;
        renderer.render(scene, camera);
    }
    animate();
})();
