// ========== 全局变量 ==========
let currentPage = 1;
const pageSize = 10;
let uploadedImages = [];
let uploadedVideo = null;  // 视频文件key
let userCurrentTab = 'posts';
let userCurrentPage = 1;
let currentUserName = '';

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

function updateUserArea() {
    const area = document.getElementById('userArea');
    const hint = document.getElementById('loginHint');
    const listFormArea = document.getElementById('postFormArea');

    if (isLoggedIn()) {
        area.innerHTML =
            '<span class="username">' + escHtml(getNickname()) + '</span>' +
            '<button class="btn-header" onclick="navigate(\'create\')">&#9997; 发帖</button>' +
            '<button class="btn-header" id="noticeBtn" onclick="navigate(\'notices\')">&#128172; 消息<span id="noticeBadge" class="notice-badge" style="display:none">0</span></button>' +
            '<button class="btn-header" onclick="doLogout()">退出</button>';
        if (hint) hint.style.display = 'none';
        if (listFormArea) listFormArea.style.display = 'none';
        loadUnreadNoticeCount();
    } else {
        area.innerHTML =
            '<button class="btn-header" onclick="showAuthModal()">登录</button>' +
            '<button class="btn-header" onclick="showAuthModal(\'register\')">注册</button>';
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

async function uploadImages(input) {
    if (!isLoggedIn()) { showToast('请先登录'); input.value = ''; return; }
    const files = Array.from(input.files);
    for (const file of files) {
        const formData = new FormData();
        formData.append('file', file);
        try {
            const token = getToken();
            const res = await fetch('/api/file/upload', {
                method: 'POST',
                headers: { 'Authorization': token },
                body: formData
            });
            const json = await res.json();
            if (json.code === 1) {
                uploadedImages.push(json.data);
                renderImagePreview();
            } else {
                showToast('图片上传失败: ' + json.msg);
            }
        } catch (e) {
            showToast('图片上传失败');
        }
    }
    input.value = '';
}

async function uploadVideo(input) {
    if (!isLoggedIn()) { showToast('请先登录'); input.value = ''; return; }
    const file = input.files[0];
    if (!file) return;

    // 检查文件大小 (限制100MB)
    if (file.size > 100 * 1024 * 1024) {
        showToast('视频文件不能超过100MB');
        input.value = '';
        return;
    }

    showToast('视频上传中...');
    const formData = new FormData();
    formData.append('file', file);
    try {
        const token = getToken();
        const res = await fetch('/api/file/upload', {
            method: 'POST',
            headers: { 'Authorization': token },
            body: formData
        });
        const json = await res.json();
        if (json.code === 1) {
            uploadedVideo = json.data;
            renderVideoPreview();
            showToast('视频上传成功');
        } else {
            showToast('视频上传失败: ' + json.msg);
        }
    } catch (e) {
        showToast('视频上传失败');
    }
    input.value = '';
}

function renderImagePreview() {
    const container = document.getElementById('imagePreview');
    if (!container) return;
    container.innerHTML = uploadedImages.map((fileKey, i) =>
        '<div class="img-item">' +
            '<img src="' + getFileUrl(fileKey) + '" onclick="viewImage(\'' + getFileUrl(fileKey) + '\')" loading="lazy" />' +
            '<div class="remove-img" onclick="removeImage(' + i + ')">&#10005;</div>' +
        '</div>'
    ).join('');
}

function renderVideoPreview() {
    const container = document.getElementById('videoPreview');
    if (!container) return;
    if (!uploadedVideo) {
        container.innerHTML = '';
        return;
    }
    container.innerHTML =
        '<div class="img-item">' +
            '<video src="' + getFileUrl(uploadedVideo) + '" controls style="width:100%;height:100%;object-fit:cover;border-radius:6px"></video>' +
            '<div class="remove-img" onclick="removeVideo()">&#10005;</div>' +
        '</div>';
}

function removeImage(idx) {
    uploadedImages.splice(idx, 1);
    renderImagePreview();
}

function removeVideo() {
    uploadedVideo = null;
    renderVideoPreview();
}

// ========== 发帖 ==========
async function submitPost() {
    const content = document.getElementById('contentInput').value.trim();
    const anonymous = document.getElementById('anonymousCheck').checked;
    const privated = document.getElementById('privateCheck').checked;
    if (!content) { showToast('请输入内容'); return; }
    try {
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
    } catch(e) {}
}

function clearForm() {
    document.getElementById('contentInput').value = '';
    document.getElementById('anonymousCheck').checked = false;
    document.getElementById('privateCheck').checked = false;
    uploadedImages = [];
    uploadedVideo = null;
    renderImagePreview();
    renderVideoPreview();
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

    return '<div class="post-card" onclick="navigate(\'detail\', ' + post.id + ')">' +
        '<div><span class="post-author"' + authorClick + '>' + authorHtml + '</span><span class="post-time">' + formatTime(post.createTime) + '</span>' + statusHtml + '</div>' +
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
            '<div><span class="post-author" style="font-size:16px;cursor:pointer" onclick="navigate(\'user\', \'' + encodeURIComponent(post.username || '') + '\')">' + escHtml(post.author) + '</span><span class="post-time">' + formatTime(post.createTime) + '</span></div>' +
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
            html += replies.map(r =>
                '<div class="reply-item">' +
                    '<span class="reply-author" style="cursor:pointer" onclick="navigate(\'user\', \'' + encodeURIComponent(r.username || '') + '\')">' + escHtml(r.author) + '</span>' +
                    '<span class="reply-time">' + formatTime(r.createTime) + '</span>' +
                    '<div class="reply-content">' + escHtml(r.content) + '</div>' +
                '</div>'
            ).join('');
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
    displayNameEl.innerHTML =
        '<div class="avatar">&#128100;</div>' +
        '<div class="username" id="userDisplayName">' + escHtml(username) + '</div>';

    try {
        const res = await api('/api/user/public/info?username=' + encodeURIComponent(username));
        if (res.code === 1 && res.data) {
            const displayName = res.data.nickname || res.data.username || username;
            document.getElementById('userDisplayName').textContent = displayName;
        }
    } catch(e) {}

    switchUserTab('posts');
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
init();
