// ========== 全局变量 ==========
let currentPage = 1;

/**
 * ConfirmDialog - 通用确认弹窗插件
 * 用法: ConfirmDialog.show('标题', '内容', () => { /* 确认回调 *\/ })
 */
const ConfirmDialog = (function () {
    let callback = null;
    let overlay = null;

    function create() {
        if (overlay) return;

        // 创建样式
        const style = document.createElement('style');
        style.textContent = `
            .cd-overlay {
                display: none;
                position: fixed;
                top: 0; left: 0; right: 0; bottom: 0;
                background: rgba(0,0,0,0.5);
                z-index: 1001;
                justify-content: center;
                align-items: center;
            }
            .cd-overlay.active { display: flex; }
            .cd-dialog {
                background: #fff;
                border-radius: 16px;
                padding: 24px;
                width: 85%;
                max-width: 320px;
                box-shadow: 0 8px 32px rgba(0,0,0,0.2);
                text-align: center;
                animation: cdPopIn 0.2s ease-out;
            }
            @keyframes cdPopIn {
                from { transform: scale(0.9); opacity: 0; }
                to { transform: scale(1); opacity: 1; }
            }
            .cd-icon { font-size: 48px; margin-bottom: 12px; }
            .cd-title { font-size: 18px; font-weight: 600; color: #333; margin-bottom: 8px; }
            .cd-message { font-size: 14px; color: #666; margin-bottom: 20px; line-height: 1.5; }
            .cd-buttons { display: flex; gap: 12px; }
            .cd-buttons button {
                flex: 1;
                padding: 10px 16px;
                border: none;
                border-radius: 8px;
                font-size: 14px;
                cursor: pointer;
                transition: all 0.2s;
            }
            .cd-cancel { background: #f0f0f0; color: #666; }
            .cd-cancel:hover { background: #e0e0e0; }
            .cd-ok { background: linear-gradient(135deg, #ff4757 0%, #ff3344 100%); color: #fff; }
            .cd-ok:hover { opacity: 0.9; }
        `;
        document.head.appendChild(style);

        // 创建 DOM
        overlay = document.createElement('div');
        overlay.className = 'cd-overlay';
        overlay.id = 'confirmOverlay';
        overlay.innerHTML = `
            <div class="cd-dialog">
                <div class="cd-icon">&#9888;</div>
                <div class="cd-title" id="cdTitle"></div>
                <div class="cd-message" id="cdMessage"></div>
                <div class="cd-buttons">
                    <button class="cd-cancel" id="cdCancel">取消</button>
                    <button class="cd-ok" id="cdOk">确定</button>
                </div>
            </div>
        `;
        document.body.appendChild(overlay);

        // 事件绑定
        document.addEventListener('click', function(e) {
            if (e.target && e.target.id === 'cdCancel') {
                hide();
            }
            if (e.target && e.target.id === 'cdOk') {
                const cb = callback; // 先保存回调
                hide(); // 再隐藏
                if (cb) cb(); // 最后执行
            }
            if (e.target === overlay) {
                hide();
            }
        });
    }

    function show(title, message, onOk) {
        if (!overlay) create();
        document.getElementById('cdTitle').textContent = title || '确认操作';
        document.getElementById('cdMessage').textContent = message || '确定要执行此操作吗？';
        callback = onOk || null;
        overlay.classList.add('active');
    }

    function hide() {
        if (overlay) {
            overlay.classList.remove('active');
            callback = null;
        }
    }

    return { show, hide };
})();
const pageSize = 10;
let pendingImages = [];      // 待上传的图片文件
let pendingVideo = null;      // 待上传的视频文件
let uploadedImages = [];      // 已上传的图片key
let uploadedVideo = null;     // 已上传的视频key
let userCurrentTab = 'posts';
let userCurrentPage = 1;
let currentUserName = '';

// ========== 上传进度弹窗 ==========
const UploadProgress = (function () {
    let overlay = null;
    let progressBar = null;
    let progressText = null;
    let currentResolve = null;
    let currentReject = null;

    function create() {
        if (overlay) return;

        const style = document.createElement('style');
        style.textContent = `
            .upload-overlay {
                display: none;
                position: fixed;
                top: 0; left: 0; right: 0; bottom: 0;
                background: rgba(0,0,0,0.6);
                z-index: 1002;
                justify-content: center;
                align-items: center;
            }
            .upload-overlay.active { display: flex; }
            .upload-dialog {
                background: #fff;
                border-radius: 16px;
                padding: 28px;
                width: 85%;
                max-width: 360px;
                box-shadow: 0 8px 32px rgba(0,0,0,0.25);
                text-align: center;
            }
            .upload-title { font-size: 16px; font-weight: 600; color: #333; margin-bottom: 16px; }
            .upload-progress-bg {
                background: #e0e0e0;
                border-radius: 8px;
                height: 12px;
                overflow: hidden;
                margin-bottom: 12px;
            }
            .upload-progress-bar {
                background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
                height: 100%;
                width: 0%;
                transition: width 0.2s;
                border-radius: 8px;
            }
            .upload-progress-text { font-size: 14px; color: #666; }
            .upload-file-name { font-size: 12px; color: #999; margin-top: 8px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
        `;
        document.head.appendChild(style);

        overlay = document.createElement('div');
        overlay.className = 'upload-overlay';
        overlay.id = 'uploadOverlay';
        overlay.innerHTML = `
            <div class="upload-dialog">
                <div class="upload-title" id="uploadTitle">&#128227; 正在上传...</div>
                <div class="upload-progress-bg">
                    <div class="upload-progress-bar" id="uploadProgressBar"></div>
                </div>
                <div class="upload-progress-text" id="uploadProgressText">0%</div>
                <div class="upload-file-name" id="uploadFileName"></div>
            </div>
        `;
        document.body.appendChild(overlay);

        progressBar = document.getElementById('uploadProgressBar');
        progressText = document.getElementById('uploadProgressText');
    }

    function show(title, fileName) {
        if (!overlay) create();
        document.getElementById('uploadTitle').textContent = title || '正在上传...';
        document.getElementById('uploadFileName').textContent = fileName || '';
        setProgress(0);
        overlay.classList.add('active');
    }

    function setProgress(percent) {
        if (progressBar) progressBar.style.width = percent + '%';
        if (progressText) progressText.textContent = percent + '%';
    }

    function hide() {
        if (overlay) overlay.classList.remove('active');
    }

    return { show, setProgress, hide };
})();

// ========== Hash 路由 ==========
function getRoute() {
    const hash = window.location.hash.slice(1) || '/';
    const parts = hash.split('/');
    return { path: parts[0] || '/', params: parts.slice(1) };
}

function navigate(path, ...params) {
    if (params.length > 0) {
        window.location.hash = '#' + path + '/' + params.join('/');
    } else {
        window.location.hash = '#' + path;
    }
}

function handleRoute() {
    const { path, params } = getRoute();
    switch (path) {
        case 'detail':
            if (params[0]) showDetail(parseInt(params[0]));
            else showList();
            break;
        case 'user':
            if (params[0]) showUserView(decodeURIComponent(params[0]));
            else showList();
            break;
        case 'notices':
            showNoticeView();
            break;
        case 'create':
            showCreateView();
            break;
        default:
            showList();
    }
}

// 监听浏览器前进/后退
window.addEventListener('hashchange', handleRoute);

// ========== Token & 用户管理 ==========
function getToken() {
    return localStorage.getItem('minitieba_token') || '';
}
function setToken(token) {
    localStorage.setItem('minitieba_token', token);
}
function clearToken() {
    localStorage.removeItem('minitieba_token');
    localStorage.removeItem('minitieba_nickname');
    localStorage.removeItem('minitieba_username');
}
function isLoggedIn() {
    return !!getToken();
}
function getNickname() {
    return localStorage.getItem('minitieba_nickname') || '';
}
function getUsername() {
    return localStorage.getItem('minitieba_username') || '';
}
function getAvatar() {
    return localStorage.getItem('minitieba_avatar') || '';
}

function updateUserArea() {
    const area = document.getElementById('userArea');
    if (!area) return; // DOM 未准备好时直接返回
    const hint = document.getElementById('loginHint');
    const listFormArea = document.getElementById('postFormArea');

    if (isLoggedIn()) {
        area.innerHTML =
            '<div class="header-avatar" onclick="navigate(\'user\', getUsername())">' + getAvatarHtml(getAvatar(), getNickname(), '') + '</div>' +
            '<button class="btn-header" onclick="navigate(\'create\')"><span class="btn-icon">&#9997;</span><span class="btn-text">发帖</span></button>' +
            '<button class="btn-header" onclick="navigate(\'user\', getUsername())"><span class="btn-icon">&#128100;</span><span class="btn-text">个人中心</span></button>' +
            '<button class="btn-header" id="noticeBtn" onclick="navigate(\'notices\')"><span class="btn-icon">&#128172;</span><span class="btn-text">消息</span><span id="noticeBadge" class="notice-badge" style="display:none">0</span></button>' +
            '<button class="btn-header" onclick="doLogout()"><span class="btn-icon">&#128682;</span><span class="btn-text">退出</span></button>';
        if (hint) hint.style.display = 'none';
        if (listFormArea) listFormArea.style.display = 'none';
        loadUnreadNoticeCount();
    } else {
        area.innerHTML =
            '<button class="btn-header" onclick="showAuthModal()"><span class="btn-icon">&#128273;</span><span class="btn-text">登录</span></button>' +
            '<button class="btn-header" onclick="showAuthModal(\'register\')"><span class="btn-icon">&#128221;</span><span class="btn-text">注册</span></button>';
        if (hint) hint.style.display = '';
        if (listFormArea) listFormArea.style.display = 'none';
    }
}

// ========== 登录/注册弹窗 ==========
function showAuthModal(tab) {
    document.getElementById('authModal').classList.add('active');
    switchTab(tab || 'login');
}
function closeAuthModal() {
    document.getElementById('authModal').classList.remove('active');
}
function switchTab(tab) {
    document.getElementById('tabLogin').classList.toggle('active', tab === 'login');
    document.getElementById('tabRegister').classList.toggle('active', tab === 'register');
    document.getElementById('loginForm').style.display = tab === 'login' ? '' : 'none';
    document.getElementById('registerForm').style.display = tab === 'register' ? '' : 'none';
}

async function doLogin() {
    const username = document.getElementById('loginUsername').value.trim();
    const password = document.getElementById('loginPassword').value;
    if (!username || !password) { showToast('请输入用户名和密码'); return; }
    const res = await api('/api/user/login', 'POST', { username, password });
    if (res.code === 1) {
        setToken(res.token);
        localStorage.setItem('minitieba_username', res.username);
        localStorage.setItem('minitieba_nickname', res.nickname);
        if (res.avatar) localStorage.setItem('minitieba_avatar', res.avatar);
        closeAuthModal();
        updateUserArea();
        showToast('登录成功');
        loadPosts(currentPage);
    } else {
        showToast(res.msg || '登录失败');
    }
}

async function doRegister() {
    const username = document.getElementById('regUsername').value.trim();
    const password = document.getElementById('regPassword').value;
    const nickname = document.getElementById('regNickname').value.trim();
    if (!username || !password) { showToast('请输入用户名和密码'); return; }
    const res = await api('/api/user/register', 'POST', { username, password, nickname });
    if (res.code === 1) {
        showToast('注册成功，请登录');
        switchTab('login');
        document.getElementById('loginUsername').value = username;
    } else {
        showToast(res.msg || '注册失败');
    }
}

function doLogout() {
    clearToken();
    updateUserArea();
    showToast('已退出登录');
    navigate('/');
}

// ========== 工具方法 ==========
function showToast(msg) {
    const t = document.getElementById('toast');
    t.textContent = msg;
    t.classList.add('show');
    setTimeout(() => t.classList.remove('show'), 2000);
}

async function api(url, method, data) {
    const opts = { method: method || 'GET', headers: {} };
    const token = getToken();
    if (token) {
        opts.headers['Authorization'] = token;
    }
    if (data) {
        opts.headers['Content-Type'] = 'application/json';
        opts.body = JSON.stringify(data);
    }
    const res = await fetch(url, opts);
    if (res.status === 401) {
        clearToken();
        updateUserArea();
        showToast('登录已过期，请重新登录');
        throw new Error('unauthorized');
    }
    return res.json();
}

function formatTime(ts) {
    const d = new Date(ts);
    const now = new Date();
    const diff = now - d;
    if (diff < 60000) return '刚刚';
    if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前';
    if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前';
    if (diff < 604800000) return Math.floor(diff / 86400000) + '天前';
    return d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate() + ' ' +
           d.getHours().toString().padStart(2, '0') + ':' + d.getMinutes().toString().padStart(2, '0');
}

function escHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}

// 生成头像HTML
function getAvatarHtml(avatar, name, sizeClass) {
    if (!sizeClass) sizeClass = '';
    if (avatar && avatar.startsWith('/')) {
        return '<span class="user-avatar ' + sizeClass + '"><img src="' + escHtml(avatar) + '" alt="头像" onerror="this.parentElement.textContent=\'' + escHtml(name ? name.charAt(0) : '?') + '\'"></span>';
    } else if (avatar) {
        return '<span class="user-avatar ' + sizeClass + '"><img src="/api/file/get/' + escHtml(avatar) + '" alt="头像" onerror="this.parentElement.textContent=\'' + escHtml(name ? name.charAt(0) : '?') + '\'"></span>';
    } else {
        return '<span class="user-avatar ' + sizeClass + '">' + escHtml(name ? name.charAt(0).toUpperCase() : '?') + '</span>';
    }
}

// ========== 文件上传 ==========
// 获取文件URL（带缓存）
// 兼容格式:
// 1. 新格式: img_xxx 或 vid_xxx -> /api/file/get/img_xxx
// 2. 旧格式: /upload/xxx.png -> /upload/xxx.png (静态资源)
// 3. 旧格式: a766fb8299f141c39fc477b2fc42ad36.png -> /upload/xxx.png (静态资源)
function getFileUrl(fileKey) {
    if (!fileKey) return '';
    
    // 新格式: img_xxx 或 vid_xxx -> /api/file/get/img_xxx
    if (fileKey.startsWith('img_') || fileKey.startsWith('vid_')) {
        const url = '/api/file/get/' + fileKey;
        console.log('getFileUrl (new):', fileKey, '->', url);
        return url;
    }
    
    // 旧格式: /upload/xxx.png -> /upload/xxx.png (静态资源)
    if (fileKey.startsWith('/upload/')) {
        console.log('getFileUrl (old path):', fileKey);
        return fileKey;
    }
    
    // 旧格式: 直接是文件名 a766fb8299f141c39fc477b2fc42ad36.png -> /upload/xxx.png
    if (fileKey.includes('/') || fileKey.includes('\\')) {
        // 已经是完整路径
        console.log('getFileUrl (full path):', fileKey);
        return fileKey;
    }
    
    // 其他文件名格式，当作旧格式处理 -> /upload/xxx.png
    const url = '/upload/' + fileKey;
    console.log('getFileUrl (legacy):', fileKey, '->', url);
    return url;
}

// 获取文件信息
async function getFileInfo(fileKey) {
    try {
        const res = await api('/api/file/info/' + fileKey);
        if (res.code === 1) {
            return res.data;
        }
    } catch (e) {}
    return null;
}

// 选择图片（只保存到本地，不上传）
function selectImages(input) {
    if (!isLoggedIn()) { showToast('请先登录'); input.value = ''; return; }
    // 有视频时不能上传图片
    if (pendingVideo || uploadedVideo) {
        showToast('已有视频，不能再上传图片');
        input.value = '';
        return;
    }
    // 限制最多9张图片
    if (pendingImages.length >= 9) {
        showToast('最多只能选择9张图片');
        input.value = '';
        return;
    }
    const files = Array.from(input.files);
    let remaining = 9 - pendingImages.length;
    if (files.length > remaining) {
        showToast('最多还能选择' + remaining + '张图片');
        files.splice(remaining);
    }
    if (files.length === 0) {
        input.value = '';
        return;
    }
    for (const file of files) {
        pendingImages.push(file);
    }
    renderImagePreview();
    input.value = '';
    // 图片达到9张时禁用视频选择
    if (pendingImages.length >= 9) {
        updateVideoBtnState(true);
    }
}

// 选择视频（只保存到本地，不上传）
function selectVideo(input) {
    if (!isLoggedIn()) { showToast('请先登录'); input.value = ''; return; }
    // 有图片时不能上传视频
    if (pendingImages.length > 0 || uploadedImages.length > 0) {
        showToast('已有图片，不能再上传视频');
        input.value = '';
        return;
    }
    // 限制最多1个视频
    if (pendingVideo || uploadedVideo) {
        showToast('最多只能选择1个视频');
        input.value = '';
        return;
    }
    const file = input.files[0];
    if (!file) return;

    // 检查文件大小 (限制100MB)
    if (file.size > 100 * 1024 * 1024) {
        showToast('视频文件不能超过100MB');
        input.value = '';
        return;
    }

    pendingVideo = file;
    renderVideoPreview();
    updateImageBtnState(true); // 有视频时禁用图片选择
    input.value = '';
}

// 更新图片选择按钮状态
function updateImageBtnState(disabled) {
    const imageInput = document.getElementById('imageInput');
    const imageBtn = imageInput ? imageInput.parentElement : null;
    if (imageBtn) {
        imageBtn.style.opacity = disabled ? '0.5' : '1';
        imageBtn.style.pointerEvents = disabled ? 'none' : 'auto';
    }
}

// 更新视频选择按钮状态
function updateVideoBtnState(disabled) {
    const videoInput = document.getElementById('videoInput');
    const videoBtn = videoInput ? videoInput.parentElement : null;
    if (videoBtn) {
        videoBtn.style.opacity = disabled ? '0.5' : '1';
        videoBtn.style.pointerEvents = disabled ? 'none' : 'auto';
    }
}

function renderImagePreview() {
    const container = document.getElementById('imagePreview');
    if (!container) return;

    let html = '';
    // 待上传的图片（使用唯一标识）
    pendingImages.forEach((file, i) => {
        const src = URL.createObjectURL(file);
        html += '<div class="img-item">' +
            '<img src="' + src + '" onclick="viewImage(\'' + src.replace(/'/g, "\\'") + '\')" loading="lazy" />' +
            '<div class="remove-img" onclick="removePendingImage(' + i + ')">&#10005;</div>' +
        '</div>';
    });
    // 已上传的图片
    uploadedImages.forEach((fileKey, i) => {
        const src = getFileUrl(fileKey);
        html += '<div class="img-item">' +
            '<img src="' + src + '" onclick="viewImage(\'' + src.replace(/'/g, "\\'") + '\')" loading="lazy" />' +
            '<div class="remove-img" onclick="removeUploadedImage(' + i + ')">&#10005;</div>' +
        '</div>';
    });
    container.innerHTML = html;
}

function renderVideoPreview() {
    const container = document.getElementById('videoPreview');
    if (!container) return;
    // 检查待上传视频
    if (pendingVideo) {
        const src = URL.createObjectURL(pendingVideo);
        container.innerHTML =
            '<div class="img-item">' +
                '<video src="' + src + '" controls style="width:100%;height:100%;object-fit:cover;border-radius:6px"></video>' +
                '<div class="remove-img" onclick="removePendingVideo()">&#10005;</div>' +
            '</div>';
        return;
    }
    // 检查已上传视频
    if (uploadedVideo) {
        container.innerHTML =
            '<div class="img-item">' +
                '<video src="' + getFileUrl(uploadedVideo) + '" controls style="width:100%;height:100%;object-fit:cover;border-radius:6px"></video>' +
                '<div class="remove-img" onclick="removeUploadedVideo()">&#10005;</div>' +
            '</div>';
        return;
    }
    container.innerHTML = '';
}

function removePendingImage(idx) {
    pendingImages.splice(idx, 1);
    renderImagePreview();
    updateVideoBtnState(false);
}

function removeUploadedImage(idx) {
    uploadedImages.splice(idx, 1);
    renderImagePreview();
    updateVideoBtnState(false);
}

function removePendingVideo() {
    pendingVideo = null;
    renderVideoPreview();
    updateImageBtnState(false);
}

function removeUploadedVideo() {
    uploadedVideo = null;
    renderVideoPreview();
}

function removeVideo() {
    // 兼容旧代码，根据情况删除
    if (pendingVideo) {
        removePendingVideo();
    } else {
        removeUploadedVideo();
    }
}

// ========== 发帖 ==========
async function submitPost() {
    const contentInput = document.getElementById('contentInput');
    const content = contentInput.value.trim();
    const anonymous = document.getElementById('anonymousCheck').checked;
    const privated = document.getElementById('privateCheck').checked;
    if (!content) { showToast('请输入内容'); return; }
    if (content.length > 300) { showToast('内容不能超过300个字'); return; }

    const submitBtn = document.querySelector('.create-page .btn-primary');
    const originalText = submitBtn ? submitBtn.textContent : '';
    if (submitBtn) submitBtn.textContent = '发布中...';
    if (submitBtn) submitBtn.disabled = true;

    try {
        // 先上传待上传的文件
        // 上传图片
        for (let i = 0; i < pendingImages.length; i++) {
            const file = pendingImages[i];
            UploadProgress.show('正在上传图片', file.name + ' (' + Math.round(file.size / 1024) + 'KB)');
            try {
                const key = await uploadFileWithProgress(file, (percent) => {
                    UploadProgress.setProgress(percent);
                });
                uploadedImages.push(key);
            } catch (e) {
                UploadProgress.hide();
                showToast('图片上传失败');
                if (submitBtn) { submitBtn.textContent = originalText; submitBtn.disabled = false; }
                return;
            }
        }

        // 上传视频
        if (pendingVideo) {
            UploadProgress.show('正在上传视频', pendingVideo.name + ' (' + Math.round(pendingVideo.size / 1024 / 1024) + 'MB)');
            try {
                uploadedVideo = await uploadFileWithProgress(pendingVideo, (percent) => {
                    UploadProgress.setProgress(percent);
                });
            } catch (e) {
                UploadProgress.hide();
                showToast('视频上传失败');
                if (submitBtn) { submitBtn.textContent = originalText; submitBtn.disabled = false; }
                return;
            }
        }

        UploadProgress.hide();

        // 发帖
        const res = await api('/api/post/create', 'POST', {
            content,
            images: uploadedImages,
            video: uploadedVideo,
            anonymous,
            privated
        });
        if (res.code === 1) {
            showToast('发帖成功');
            clearForm();
            navigate('/');
        } else {
            showToast(res.msg || '发帖失败');
        }
    } catch(e) {
        showToast('发帖失败');
    } finally {
        if (submitBtn) { submitBtn.textContent = originalText; submitBtn.disabled = false; }
    }
}

// 带进度的文件上传
function uploadFileWithProgress(file, onProgress) {
    return new Promise((resolve, reject) => {
        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append('file', file);

        xhr.upload.addEventListener('progress', (e) => {
            if (e.lengthComputable) {
                const percent = Math.round((e.loaded / e.total) * 100);
                onProgress(percent);
            }
        });

        xhr.addEventListener('load', () => {
            try {
                const json = JSON.parse(xhr.responseText);
                if (json.code === 1) {
                    resolve(json.data);
                } else {
                    reject(new Error(json.msg || '上传失败'));
                }
            } catch (e) {
                reject(new Error('上传失败'));
            }
        });

        xhr.addEventListener('error', () => reject(new Error('网络错误')));
        xhr.addEventListener('abort', () => reject(new Error('上传取消')));

        const token = getToken();
        xhr.open('POST', '/api/file/upload');
        if (token) xhr.setRequestHeader('Authorization', token);
        xhr.send(formData);
    });
}

function clearForm() {
    document.getElementById('contentInput').value = '';
    document.getElementById('anonymousCheck').checked = false;
    document.getElementById('privateCheck').checked = false;
    pendingImages = [];
    pendingVideo = null;
    uploadedImages = [];
    uploadedVideo = null;
    renderImagePreview();
    renderVideoPreview();
    updateImageBtnState(false);
    updateVideoBtnState(false);
}

// ========== 帖子列表 ==========
async function loadPosts(page) {
    currentPage = page;
    try {
        const res = await api('/api/post/list?page=' + page + '&size=' + pageSize);
        if (res.code !== 1) return;
        const data = res.data;
        const posts = data.posts || [];
        const container = document.getElementById('postList');
        if (posts.length === 0) {
            container.innerHTML = '<div class="empty-state"><div class="emoji">&#128172;</div><p>还没有帖子，快来发第一个吧！</p></div>';
            document.getElementById('pagination').innerHTML = '';
            return;
        }
        container.innerHTML = posts.map(post => renderPostCard(post)).join('');
        renderPagination(data.page, data.totalPages);
    } catch(e) {}
}

function renderPostCard(post, showOwnStatus) {
    let imagesHtml = '';
    if (post.images && post.images.length > 0) {
        imagesHtml = '<div class="post-images">' +
            post.images.map(fileKey => '<img src="' + getFileUrl(fileKey) + '" onclick="event.stopPropagation();viewImage(\'' + getFileUrl(fileKey) + '\')" loading="lazy" />').join('') +
        '</div>';
    }
    let videoHtml = '';
    if (post.video) {
        videoHtml = '<div class="post-video"><video src="' + getFileUrl(post.video) + '" controls preload="metadata"></video></div>';
    }
    let contentPreview = post.content || '';
    if (contentPreview.length > 200) {
        contentPreview = contentPreview.substring(0, 200) + '...';
    }
    let authorHtml = escHtml(post.author) || '匿名用户';
    let authorClick = post.username ? 'onclick="event.stopPropagation();navigate(\'user\', \'' + encodeURIComponent(post.username) + '\')" style="cursor:pointer"' : '';

    const likeClass = post.likeStatus === 'like' ? 'active-like' : '';
    const dislikeClass = post.likeStatus === 'dislike' ? 'active-dislike' : '';
    const likeClick = isLoggedIn()
        ? 'onclick="event.stopPropagation();toggleLikeInList(' + post.id + ', \'like\')"'
        : 'onclick="event.stopPropagation();showAuthModal()"';
    const dislikeClick = isLoggedIn()
        ? 'onclick="event.stopPropagation();toggleLikeInList(' + post.id + ', \'dislike\')"'
        : 'onclick="event.stopPropagation();showAuthModal()"';

    let statusHtml = '';
    if (showOwnStatus) {
        if (post.privated) {
            statusHtml += '<span style="margin-left:6px;font-size:11px;color:#ff9800;background:#fff3e0;padding:2px 6px;border-radius:4px">&#128274; 私密</span>';
        }
        if (post.anonymous) {
            statusHtml += '<span style="margin-left:6px;font-size:11px;color:#9c27b0;background:#f3e5f5;padding:2px 6px;border-radius:4px">&#128119; 匿名</span>';
        }
    }

    // 判断是否是当前用户发的帖子，显示删除按钮
    let deleteBtn = '';
    if (isLoggedIn() && post.username === getUsername()) {
        deleteBtn = '<button class="btn-delete" onclick="event.stopPropagation();deletePost(' + post.id + ')">&#128465; 删除</button>';
    }

    return '<div class="post-card" onclick="navigate(\'detail\', ' + post.id + ')">' +
        '<div class="post-header"><span class="post-author"' + authorClick + '>' + getAvatarHtml(post.avatar, post.author, '') + authorHtml + '</span><span class="post-time">' + formatTime(post.createTime) + '</span>' + statusHtml + deleteBtn + '</div>' +
        '<div class="post-content">' + escHtml(contentPreview) + '</div>' +
        imagesHtml + videoHtml +
        '<div class="post-stats">' +
            '<span class="' + likeClass + '" ' + likeClick + '>&#128077; ' + post.likeCount + '</span>' +
            '<span class="' + dislikeClass + '" ' + dislikeClick + '>&#128078; ' + post.dislikeCount + '</span>' +
            '<span onclick="event.stopPropagation()">&#128172; ' + post.replyCount + '</span>' +
        '</div>' +
    '</div>';
}

// ========== 分页 ==========
function renderPagination(current, totalPages) {
    if (totalPages <= 1) {
        document.getElementById('pagination').innerHTML = '';
        return;
    }
    let html = '';
    html += '<button class="page-btn" ' + (current <= 1 ? 'disabled' : '') + ' onclick="loadPosts(' + (current - 1) + ')">上一页</button>';
    const start = Math.max(1, current - 2);
    const end = Math.min(totalPages, current + 2);
    for (let i = start; i <= end; i++) {
        html += '<button class="page-btn ' + (i === current ? 'active' : '') + '" onclick="loadPosts(' + i + ')">' + i + '</button>';
    }
    html += '<button class="page-btn" ' + (current >= totalPages ? 'disabled' : '') + ' onclick="loadPosts(' + (current + 1) + ')">下一页</button>';
    document.getElementById('pagination').innerHTML = html;
}

// ========== 发帖页面 ==========
function showCreateView() {
    if (!isLoggedIn()) {
        showAuthModal();
        navigate('/');
        return;
    }
    document.getElementById('listView').classList.add('hidden');
    document.getElementById('detailView').classList.remove('active');
    document.getElementById('userView').classList.remove('active');
    document.getElementById('noticeView').classList.remove('active');
    document.getElementById('createView').classList.add('active');
    clearForm();
}

// ========== 帖子详情 ==========
async function showDetail(postId) {
    try {
        const res = await api('/api/post/detail?id=' + postId);
        if (res.code !== 1) {
            showToast(res.msg || '加载失败');
            navigate('/');
            return;
        }

        const data = res.data;
        const post = data.post;
        const replies = data.replies || [];
        const likeStatus = data.likeStatus || 'none';

        let imagesHtml = '';
        if (post.images && post.images.length > 0) {
            imagesHtml = '<div class="post-images" style="margin:12px 0">' +
                post.images.map(fileKey => '<img src="' + getFileUrl(fileKey) + '" style="max-width:150px;max-height:150px;object-fit:cover;border-radius:6px;cursor:pointer;margin:4px" onclick="viewImage(\'' + getFileUrl(fileKey) + '\')" />').join('') +
            '</div>';
        }
        let videoHtml = '';
        if (post.video) {
            videoHtml = '<div class="post-video" style="margin:12px 0"><video src="' + getFileUrl(post.video) + '" controls preload="metadata" style="width:100%;max-height:400px;border-radius:6px"></video></div>';
        }

        const likeClass = likeStatus === 'like' ? 'active-like' : '';
        const dislikeClass = likeStatus === 'dislike' ? 'active-dislike' : '';

        let html = '<div class="detail-post">' +
            '<div class="post-header"><span class="post-author" style="font-size:16px;cursor:pointer" onclick="navigate(\'user\', \'' + encodeURIComponent(post.username || '') + '\')">' + getAvatarHtml(post.avatar, post.author, '') + escHtml(post.author) + '</span><span class="post-time">' + formatTime(post.createTime) + '</span></div>' +
            '<div class="post-content" style="margin:14px 0">' + escHtml(post.content) + '</div>' +
            imagesHtml + videoHtml +
            '<div class="post-stats" style="margin-top:12px;padding-top:12px;border-top:1px solid #f0f0f0">';

        if (isLoggedIn()) {
            html += '<span class="' + likeClass + '" onclick="toggleLike(' + postId + ', \'like\')">&#128077; ' + post.likeCount + '</span>' +
                '<span class="' + dislikeClass + '" onclick="toggleLike(' + postId + ', \'dislike\')">&#128078; ' + post.dislikeCount + '</span>';
        } else {
            html += '<span>&#128077; ' + post.likeCount + '</span>' +
                '<span>&#128078; ' + post.dislikeCount + '</span>';
        }
        html += '<span>&#128172; ' + post.replyCount + '</span></div></div>';

        html += '<div class="reply-section"><h3>&#128172; 回复 (' + replies.length + ')</h3>';

        if (replies.length === 0) {
            html += '<div class="empty-state" style="padding:20px"><p>暂无回复，来说点什么吧</p></div>';
        } else {
            html += replies.map(r => {
                // 判断是否是当前用户发的回复，显示删除按钮
                let replyDeleteBtn = '';
                if (isLoggedIn() && r.username === getUsername()) {
                    replyDeleteBtn = '<button class="btn-delete btn-delete-sm" onclick="deleteReply(' + r.id + ', ' + postId + ')">&#128465; 删除</button>';
                }
                return '<div class="reply-item">' +
                    '<div class="reply-header"><span class="reply-author" style="cursor:pointer" onclick="navigate(\'user\', \'' + encodeURIComponent(r.username || '') + '\')">' + getAvatarHtml(r.avatar, r.author, 'user-avatar-sm') + escHtml(r.author) + '</span>' +
                    '<span class="reply-time">' + formatTime(r.createTime) + '</span>' + replyDeleteBtn + '</div>' +
                    '<div class="reply-content">' + escHtml(r.content) + '</div>' +
                '</div>';
            }).join('');
        }

        if (isLoggedIn()) {
            html += '<div class="reply-form">' +
                '<input type="text" id="replyInput" placeholder="写下你的回复..." />' +
                '<label style="font-size:12px;color:#666"><input type="checkbox" id="replyAnonymous" /> 匿名回复</label>' +
                '<button class="btn btn-primary btn-sm" onclick="submitReply(' + postId + ')">回复</button>' +
            '</div>';
        } else {
            html += '<div style="text-align:center;padding:12px;color:#999;font-size:13px"><a onclick="showAuthModal()" style="color:#667eea;cursor:pointer">登录</a>后可回复</div>';
        }

        html += '</div>';

        document.getElementById('detailContent').innerHTML = html;
        document.getElementById('listView').classList.add('hidden');
        document.getElementById('detailView').classList.add('active');
        document.getElementById('userView').classList.remove('active');
        document.getElementById('noticeView').classList.remove('active');
        document.getElementById('createView').classList.remove('active');
    } catch(e) {}
}

// ========== 点赞/点踩 ==========
async function toggleLike(postId, type) {
    try {
        const res = await api('/api/post/like', 'POST', { postId, type });
        if (res.code === 1) {
            showDetail(postId);
        } else {
            showToast(res.msg || '操作失败');
        }
    } catch(e) {}
}

async function toggleLikeInList(postId, type) {
    if (!isLoggedIn()) {
        showAuthModal();
        return;
    }
    try {
        const res = await api('/api/post/like', 'POST', { postId, type });
        if (res.code === 1) {
            if (getRoute().path === '/') {
                loadPosts(currentPage);
            } else if (getRoute().path === 'user') {
                loadUserContent();
            }
        } else {
            showToast(res.msg || '操作失败');
        }
    } catch(e) {}
}

// ========== 提交回复 ==========
async function submitReply(postId) {
    const input = document.getElementById('replyInput');
    const content = input.value.trim();
    const anonymous = document.getElementById('replyAnonymous').checked;
    if (!content) { showToast('请输入回复内容'); return; }
    try {
        const res = await api('/api/post/reply', 'POST', { postId, content, anonymous });
        if (res.code === 1) {
            showDetail(postId);
        } else {
            showToast(res.msg || '回复失败');
        }
    } catch(e) {}
}

// ========== 删除帖子 ==========
async function deletePost(postId) {
    ConfirmDialog.show('删除帖子', '确定要删除这篇帖子吗？此操作不可恢复。', async () => {
        try {
            const res = await api('/api/post/delete?id=' + postId, 'POST');
            if (res.code === 1) {
                showToast('删除成功');
                navigate('/');
                loadPosts(currentPage);
            } else {
                showToast(res.msg || '删除失败');
            }
        } catch(e) {}
    });
}

// ========== 删除回复 ==========
async function deleteReply(replyId, postId) {
    ConfirmDialog.show('删除回复', '确定要删除这条回复吗？此操作不可恢复。', async () => {
        try {
            const res = await api('/api/post/reply/delete?id=' + replyId, 'POST');
            if (res.code === 1) {
                showToast('删除成功');
                showDetail(postId);
            } else {
                showToast(res.msg || '删除失败');
            }
        } catch(e) {}
    });
}

// ========== 图片查看 ==========
function viewImage(url) {
    document.getElementById('viewerImage').src = url;
    document.getElementById('imageViewer').classList.add('active');
}
function closeViewer() {
    document.getElementById('imageViewer').classList.remove('active');
}

// ========== 用户主页 ==========
async function showUserView(username) {
    currentUserName = username;
    userCurrentPage = 1;
    userCurrentTab = 'posts';

    document.getElementById('listView').classList.add('hidden');
    document.getElementById('detailView').classList.remove('active');
    document.getElementById('userView').classList.add('active');
    document.getElementById('noticeView').classList.remove('active');
    document.getElementById('createView').classList.remove('active');

    const displayNameEl = document.getElementById('userProfile');
    const isOwnProfile = isLoggedIn() && username === getUsername();
    displayNameEl.innerHTML =
        '<div class="avatar-wrapper" id="avatarWrapper">' +
        '<div class="avatar" id="userAvatarDisplay">&#128100;</div>' +
        (isOwnProfile ? '<div class="avatar-edit-btn" onclick="document.getElementById(\'avatarInput\').click()">&#128247;</div>' : '') +
        '</div>' +
        '<div class="username" id="userDisplayName">' + escHtml(username) + '</div>' +
        '<div class="user-signature" id="userSignature"></div>' +
        (isOwnProfile ? '<div class="user-profile-actions"><button class="btn btn-outline btn-sm" onclick="showNicknameEdit()">&#9998; 修改昵称</button> <button class="btn btn-outline btn-sm" onclick="showSignatureEdit()">&#128221; 修改签名</button></div>' : '');

    // 如果是自己的资料，添加头像上传input
    if (isOwnProfile && !document.getElementById('avatarInput')) {
        const input = document.createElement('input');
        input.type = 'file';
        input.id = 'avatarInput';
        input.accept = 'image/*';
        input.style.display = 'none';
        input.onchange = function() { uploadAvatar(this.files[0]); };
        document.getElementById('userProfile').appendChild(input);
    }

    try {
        const res = await api('/api/user/public/info?username=' + encodeURIComponent(username));
        if (res.code === 1) {
            const displayName = res.nickname || res.username || username;
            document.getElementById('userDisplayName').textContent = displayName;
            if (res.avatar) {
                updateUserAvatarDisplay(res.avatar);
            }
            // 显示签名
            const signatureEl = document.getElementById('userSignature');
            if (signatureEl) {
                if (res.signature && res.signature.trim()) {
                    signatureEl.textContent = '📋 ' + res.signature;
                } else {
                    signatureEl.textContent = '📋 该用户很懒什么都没留下';
                }
            }
        }
    } catch(e) {}

    switchUserTab('posts');
}

// 更新头像显示
function updateUserAvatarDisplay(avatar) {
    const avatarEl = document.getElementById('userAvatarDisplay');
    if (avatarEl && avatar) {
        avatarEl.innerHTML = '<img src="/api/file/get/' + escHtml(avatar) + '" style="width:100%;height:100%;border-radius:50%;object-fit:cover" onerror="this.parentElement.innerHTML=\'&#128100;\'" />';
    }
}

// 上传头像
async function uploadAvatar(file) {
    if (!file) return;
    if (file.size > 2 * 1024 * 1024) {
        showToast('头像不能超过2MB');
        return;
    }
    try {
        const formData = new FormData();
        formData.append('file', file);
        const token = getToken();
        const res = await fetch('/api/file/upload', {
            method: 'POST',
            headers: { 'Authorization': token },
            body: formData
        });
        const json = await res.json();
        if (json.code === 1) {
            const avatarKey = json.data;
            // 更新头像到服务器
            const updateRes = await api('/api/user/updateAvatar', 'POST', { avatar: avatarKey });
            if (updateRes.code === 1) {
                updateUserAvatarDisplay(avatarKey);
                showToast('头像更新成功');
            } else {
                showToast(updateRes.msg || '头像更新失败');
            }
        } else {
            showToast(json.msg || '上传失败');
        }
    } catch(e) {
        showToast('头像上传失败');
    }
}

// 显示昵称编辑
function showNicknameEdit() {
    const currentNickname = document.getElementById('userDisplayName').textContent;
    document.getElementById('nicknameInput').value = currentNickname;
    document.getElementById('nicknameModal').classList.add('active');
    document.getElementById('nicknameInput').focus();
}

// 关闭昵称编辑弹窗
function closeNicknameModal() {
    document.getElementById('nicknameModal').classList.remove('active');
}

// 提交昵称编辑
function submitNicknameEdit() {
    const input = document.getElementById('nicknameInput');
    const newNickname = input.value.trim();
    const currentNickname = document.getElementById('userDisplayName').textContent;
    if (newNickname && newNickname !== currentNickname) {
        closeNicknameModal();
        updateNickname(newNickname);
    } else {
        closeNicknameModal();
    }
}

// 显示签名编辑
function showSignatureEdit() {
    let currentSignature = document.getElementById('userSignature').textContent || '';
    // 移除 emoji 前缀
    currentSignature = currentSignature.replace(/^📋\s*/, '');
    // 移除"该用户很懒什么都没留下"
    if (currentSignature === '该用户很懒什么都没留下') {
        currentSignature = '';
    }
    const input = document.getElementById('signatureInput');
    input.value = currentSignature;
    updateSignatureCharCount();
    document.getElementById('signatureModal').classList.add('active');
    input.focus();
}

// 更新签名字数统计
function updateSignatureCharCount() {
    const input = document.getElementById('signatureInput');
    const counter = document.getElementById('signatureCharCount');
    const count = input.value.length;
    counter.textContent = count;
    counter.parentElement.classList.toggle('warn', count > 110);
}

// 关闭签名编辑弹窗
function closeSignatureModal() {
    document.getElementById('signatureModal').classList.remove('active');
}

// 提交签名编辑
function submitSignatureEdit() {
    const input = document.getElementById('signatureInput');
    const newSignature = input.value.trim();
    closeSignatureModal();
    updateSignature(newSignature);
}

// 更新签名
async function updateSignature(newSignature) {
    if (newSignature && newSignature.length > 128) {
        showToast('签名不能超过128个字');
        return;
    }
    try {
        const res = await api('/api/user/updateSignature', 'POST', { signature: newSignature });
        if (res.code === 1) {
            const signatureEl = document.getElementById('userSignature');
            if (signatureEl) {
                if (newSignature && newSignature.trim()) {
                    signatureEl.textContent = '📋 ' + newSignature;
                } else {
                    signatureEl.textContent = '📋 该用户很懒什么都没留下';
                }
            }
            showToast('签名修改成功');
        } else {
            showToast(res.msg || '修改失败');
        }
    } catch(e) {
        showToast('签名修改失败');
    }
}

// 更新昵称
async function updateNickname(newNickname) {
    try {
        const res = await api('/api/user/updateNickname', 'POST', { nickname: newNickname });
        if (res.code === 1) {
            document.getElementById('userDisplayName').textContent = newNickname;
            localStorage.setItem('nickname', newNickname);
            showToast('昵称修改成功');
        } else {
            showToast(res.msg || '修改失败');
        }
    } catch(e) {
        showToast('昵称修改失败');
    }
}

function switchUserTab(tab) {
    userCurrentTab = tab;
    userCurrentPage = 1;
    document.getElementById('tabPosts').classList.toggle('active', tab === 'posts');
    document.getElementById('tabReplies').classList.toggle('active', tab === 'replies');
    document.getElementById('userPostsList').style.display = tab === 'posts' ? '' : 'none';
    document.getElementById('userRepliesList').style.display = tab === 'replies' ? '' : 'none';
    document.getElementById('userPagination').innerHTML = '';
    loadUserContent();
}

async function loadUserContent() {
    const username = currentUserName;
    const isOwnProfile = isLoggedIn() && username === getUsername();
    try {
        const res = await api('/api/post/user?username=' + encodeURIComponent(username) + '&page=' + userCurrentPage + '&size=' + pageSize);
        if (res.code !== 1) return;

        if (userCurrentTab === 'posts') {
            renderUserPosts(res.data.posts || [], isOwnProfile);
        } else {
            renderUserReplies(res.data.replies || []);
        }
    } catch(e) {}
}

function renderUserPosts(posts, showOwnStatus) {
    const container = document.getElementById('userPostsList');
    if (posts.length === 0) {
        container.innerHTML = '<div class="empty-state"><div class="emoji">&#128221;</div><p>暂无发帖</p></div>';
        return;
    }
    container.innerHTML = posts.map(post => renderPostCard(post, showOwnStatus)).join('');
}

function renderUserReplies(replies) {
    const container = document.getElementById('userRepliesList');
    if (replies.length === 0) {
        container.innerHTML = '<div class="empty-state"><div class="emoji">&#128172;</div><p>暂无跟帖</p></div>';
        return;
    }
    container.innerHTML = replies.map(r =>
        '<div class="reply-card">' +
            '<div class="reply-to">回复了帖子 #' + r.postId + '</div>' +
            '<div class="reply-content">' + escHtml(r.content) + '</div>' +
            '<div class="reply-time">' + formatTime(r.createTime) + '</div>' +
            '<div class="reply-post-link" onclick="navigate(\'detail\', ' + r.postId + ')">查看原帖 &#8594;</div>' +
        '</div>'
    ).join('');
}

function showList() {
    document.getElementById('listView').classList.remove('hidden');
    document.getElementById('detailView').classList.remove('active');
    document.getElementById('userView').classList.remove('active');
    document.getElementById('noticeView').classList.remove('active');
    document.getElementById('createView').classList.remove('active');
    loadPosts(currentPage);
}

// ========== 消息通知 ==========
async function loadUnreadNoticeCount() {
    if (!isLoggedIn()) return;
    try {
        const res = await api('/api/post/notices?page=1&size=1');
        if (res.code === 1 && res.data && res.data.unreadCount > 0) {
            const badge = document.getElementById('noticeBadge');
            if (badge) {
                badge.textContent = res.data.unreadCount > 99 ? '99+' : res.data.unreadCount;
                badge.style.display = 'inline-flex';
            }
        }
    } catch(e) {
        console.error('加载未读通知数失败:', e);
    }
}

async function showNoticeView() {
    if (!isLoggedIn()) {
        showAuthModal();
        navigate('/');
        return;
    }
    document.getElementById('listView').classList.add('hidden');
    document.getElementById('detailView').classList.remove('active');
    document.getElementById('userView').classList.remove('active');
    document.getElementById('noticeView').classList.add('active');
    document.getElementById('createView').classList.remove('active');

    await loadNotices();
    try {
        await api('/api/post/notices/read', 'POST');
        const badge = document.getElementById('noticeBadge');
        if (badge) badge.style.display = 'none';
    } catch(e) {}
}

async function loadNotices() {
    try {
        const res = await api('/api/post/notices?page=1&size=50');
        if (res.code !== 1) {
            showToast(res.msg || '加载通知失败');
            return;
        }
        const notices = res.data.notices || [];
        const container = document.getElementById('noticeList');
        if (notices.length === 0) {
            container.innerHTML = '<div class="empty-state"><div class="emoji">&#128172;</div><p>暂无消息通知</p></div>';
            return;
        }
        container.innerHTML = notices.map(n => renderNoticeItem(n)).join('');
    } catch(e) {
        console.error('加载通知失败:', e);
        showToast('加载通知失败');
    }
}

function renderNoticeItem(notice) {
    const icon = notice.type === 'like' ? '&#128077;' : notice.type === 'dislike' ? '&#128078;' : '&#128172;';
    const actionText = notice.type === 'like' ? '赞了你的帖子' : notice.type === 'dislike' ? '踩了你的帖子' : '回复了你的帖子';
    const content = notice.type === 'reply' && notice.replyContent ? escHtml(notice.replyContent) : '';
    const postPreview = notice.postContent ? escHtml(notice.postContent) : '帖子内容';

    return '<div class="notice-item ' + (notice.readed ? '' : 'unread') + '" onclick="navigate(\'detail\', ' + notice.postId + ')">' +
        '<div class="notice-header">' +
            '<span class="notice-icon">' + icon + '</span>' +
            '<span class="notice-user">' + escHtml(notice.fromNickname) + '</span>' +
            '<span style="color:#666;font-size:13px">' + actionText + '</span>' +
            '<span class="notice-time">' + formatTime(notice.createTime) + '</span>' +
        '</div>' +
        (content ? '<div class="notice-content">' + content + '</div>' : '') +
        '<div class="notice-post">&#128203; ' + postPreview + '</div>' +
    '</div>';
}

// ========== 初始化 ==========
async function init() {
    if (getToken()) {
        try {
            const res = await api('/api/user/info');
            if (res.code === 1) {
                localStorage.setItem('minitieba_username', res.username);
                localStorage.setItem('minitieba_nickname', res.nickname);
            } else {
                clearToken();
            }
        } catch (e) {
            clearToken();
        }
    }
    updateUserArea();
    handleRoute();
}

// 确保 DOM 加载完成后再初始化
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
} else {
    init();
}
