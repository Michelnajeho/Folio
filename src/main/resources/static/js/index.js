/* ══════════════════════════════════
   Scene Transitions
══════════════════════════════════ */
function goLogin() {
    document.getElementById('landing').classList.add('fade-out');
    document.getElementById('shader-bg').classList.add('dimmed');
    document.getElementById('loginScene').classList.add('active');
    document.getElementById('btnBack').classList.add('visible');
    setTimeout(function () {
        document.getElementById('username').focus();
    }, 600);
}

function goBack() {
    document.getElementById('landing').classList.remove('fade-out');
    document.getElementById('shader-bg').classList.remove('dimmed');
    document.getElementById('loginScene').classList.remove('active');
    document.getElementById('btnBack').classList.remove('visible');
}

/* 로그인 씬 바깥 클릭 시 뒤로가기 */
document.getElementById('loginScene').addEventListener('click', function (e) {
    if (e.target === this) {
        goBack();
    }
});

/* 에러 시 자동으로 로그인 화면 */
if (window.location.search.indexOf('error') !== -1) {
    goLogin();
}

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
