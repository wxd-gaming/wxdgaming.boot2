function refreshContent(flag) {
    // 创建一个 URL 对象以便操作
    const urlObj = new URL(window.location.href);

    // 设置或覆盖 pagetype 参数
    urlObj.searchParams.set('pagetype', flag);
    // 使用 replaceState 替换当前历史记录，不刷新页面
    history.replaceState({}, '', urlObj.toString());
    loadTemplate(`${flag}.html`, '.content-container', '正在加载内容...')
    // 跳转到新的 URL
    // window.top.location.href = urlObj.toString();
}

/**
 * 加载模板并插入到指定容器
 * @param {string} templateUrl 模板文件路径
 * @param {string} containerSelector 容器选择器
 * @param {string} loadingText 加载提示文本
 * @param {function} successCall 加载成功后的回调函数
 */
function loadTemplate(templateUrl, containerSelector, loadingText, successCall) {
    return new Promise((resolve, reject) => {
        // 更新加载提示
        $(containerSelector).html(`<div class="loading">${loadingText}</div>`);
        // 使用jQuery的AJAX加载模板
        $.ajax({
            url: templateUrl,
            type: 'GET',
            dataType: 'html',
            success: function (html) {
                // 请求成功，插入模板内容
                $(containerSelector).html(html);
                if (successCall != null) {
                    successCall();
                }
                resolve(); // 成功时 resolve
            },
            error: function (xhr, status, error) {
                // 请求失败，显示错误信息
                $(containerSelector).html(`<div class="loading">加载失败: ${error} (状态: ${status})</div>`);
                console.error(`加载模板 ${templateUrl} 失败:`, error);
                reject(error); // 失败时 reject
            }
        });
    });
}