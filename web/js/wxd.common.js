/**
 * 判断值是否为空
 * @param value 需要判定的对象
 * @returns {boolean} true 表示为空，false 表示非空
 */
function isEmpty(value) {
    return value === null || value === undefined || value.trim() === "";
}

/**
 * 判断值是否为null
 * @param value
 * @returns {boolean}
 */
function isNull(value) {
    return value === null || value === undefined;
}

/**
 * 提示框
 */
class Notice {

    number = 0;
    container = null;

    constructor() {
        // 初始化提示容器
        this.container = document.createElement("div");
        this.container.style.cssText = `position: fixed;bottom: 20px;right: 20px;width: 300px;z-index: 10000;display: flex;flex-direction: column; /* 新提示在底部 */`;
        document.body.appendChild(this.container);
    }

    /**
     * 添加提示
     * @param message 提示内容
     * @param type info | error | success | warning
     * @param removeTime 移除时间
     * @returns {Notice}
     */
    append(message, type = "info", removeTime) {
        this.number++;
        const noticeItem = document.createElement("div");

        // 获取对应类型的样式
        const style = typeStyles[type] || typeStyles.info;

        // 设置基础样式
        noticeItem.style.cssText = `
background-color: ${style.backgroundColor};
border: 1px solid ${style.borderColor};
border-radius: 5px;padding: 10px;margin-top: 10px;box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
opacity: 0;transform: translateY(20px);transition: all 0.3s ease-in-out;display: flex;align-items: center;`;

        // 添加图标和消息内容
        noticeItem.innerHTML = `
        <span style="margin-right: 8px;">${style.icon}</span>
        <span>${message}</span>
    `;

        // 追加到容器
        this.container.appendChild(noticeItem);

        // 触发进入动画
        setTimeout(() => {
            noticeItem.style.opacity = "1";
            noticeItem.style.transform = "translateY(0)";
        }, 10);

        // 默认移除时间
        if (isNull(removeTime)) {
            removeTime = 3000;
        }

        // 自动移除
        setTimeout(() => {
            noticeItem.style.opacity = "0";
            noticeItem.style.transform = "translateY(-20px)";
            setTimeout(() => {
                if (noticeItem.parentNode) {
                    noticeItem.parentNode.removeChild(noticeItem);
                }
            }, 300);
        }, removeTime);

        return this;
    }


}

/**
 * 提示框
 */
class Alert {

    // 提示标题
    _title = "";
    // 提示内容
    _content = "";
    // 确认按钮的文字
    _confirmText = "确认";
    //确认按钮回调
    _confirmCall = null;
    _cancelText = "";
    _cancelCall = null;


    /**
     * 显示弹窗
     * @returns {Promise<unknown>}
     */
    show() {
        return new Promise((resolve, reject) => {
            // 创建遮罩层（背景透明全覆盖）
            const overlay = document.createElement("div");
            overlay.style.cssText = "position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0, 0, 0, 0.5); z-index: 9998; display: flex; justify-content: center; align-items: center;";

            // 创建弹窗容器
            const alertDiv = document.createElement("div");
            alertDiv.style.cssText = "position: relative; background-color: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); z-index: 9999; min-width: 400px; max-width: 80%; width: auto; text-align: center;";

            // 添加标题
            if (!isEmpty(this._title)) {
                const titleElement = document.createElement("h3");
                titleElement.textContent = this._title;
                titleElement.style.cssText = "margin-top: 0;";
                alertDiv.appendChild(titleElement);
            }

            // 添加内容
            if (!isEmpty(this._content)) {
                const contentWrapper = document.createElement("div");
                contentWrapper.innerHTML = this._content; // 直接插入自定义 HTML
                contentWrapper.style.cssText = "margin: 10px 0; text-align: left; word-wrap: break-word; overflow-wrap: break-word;";
                alertDiv.appendChild(contentWrapper);
            }

            // 添加关闭按钮
            const closeButton = document.createElement("button");
            closeButton.textContent = "×";
            closeButton.style.cssText = `position: absolute;top: 10px;right: 10px;background: none;border: none;fontSize: 20px;cursor: pointer;`;
            closeButton.onclick = () => {
                document.body.removeChild(overlay); // 关闭弹窗
                resolve(false); // 解析 Promise，表示用户点击了取消
            };
            alertDiv.appendChild(closeButton);

            // 创建按钮容器
            const buttonContainer = document.createElement("div");
            buttonContainer.style.display = "flex";
            buttonContainer.style.justifyContent = "flex-end"; // 按钮靠右对齐
            buttonContainer.style.gap = "10px"; // 按钮之间的间距

            // 添加确认按钮
            if (!isEmpty(this._confirmText)) {
                const confirmButton = document.createElement("button");
                confirmButton.textContent = this._confirmText;
                confirmButton.style.cssText = "margin: 10px 5px; padding: 8px 16px; background-color: #007bff; color: #fff; border: none; border-radius: 4px; cursor: pointer;";
                confirmButton.onclick = () => {
                    try {
                        if (this._confirmCall) this._confirmCall(); // 执行确认回调
                    } catch (e) {
                        console.log(e);
                    }
                    document.body.removeChild(overlay); // 关闭弹窗
                    resolve(true); // 解析 Promise，表示用户点击了确认
                };
                buttonContainer.appendChild(confirmButton);
            }


            // 添加取消按钮
            if (!isEmpty(this._cancelText)) {
                const cancelButton = document.createElement("button");
                cancelButton.textContent = this._cancelText;
                cancelButton.style.cssText = "margin: 10px 5px; padding: 8px 16px; background-color: #6c757d; color: #fff; border: none; border-radius: 4px; cursor: pointer;";
                cancelButton.onclick = () => {
                    try {
                        if (this._cancelCall) this._cancelCall(); // 执行取消回调
                    } catch (e) {
                        console.log(e);
                    }
                    document.body.removeChild(overlay); // 关闭弹窗
                    resolve(false); // 解析 Promise，表示用户点击了取消
                };
                buttonContainer.appendChild(cancelButton);
            }
            alertDiv.appendChild(buttonContainer);
            // 将弹窗添加到遮罩层
            overlay.appendChild(alertDiv);
            // 将遮罩层添加到页面
            document.body.appendChild(overlay);
        });
    }

    title(value) {
        this._title = value;
        return this;
    }

    content(value) {
        this._content = value;
        return this;
    }

    confirmText(value) {
        this._confirmText = value;
        return this;
    }

    confirmCall(value) {
        this._confirmCall = value;
        return this;
    }

    cancelText(value) {
        this._cancelText = value;
        return this;
    }

    cancelCall(value) {
        this._cancelCall = value;
        return this;
    }

}

const typeStyles = {
    info: {
        backgroundColor: "#e3f2fd", // 更浅的蓝色背景
        borderColor: "#90caf9",     // 更鲜明的边框色
        icon: "ℹ️"
    },
    error: {
        backgroundColor: "#f8d7da",
        borderColor: "#f5c6cb",
        icon: "❌"
    },
    success: {
        backgroundColor: "#d4edda",
        borderColor: "#c3e6cb",
        icon: "✅"
    },
    warning: {
        backgroundColor: "#fff3cd",
        borderColor: "#ffeaa7",
        icon: "⚠️"
    }
};

const wxd = {
    alertNumber: 0,
    _noticeInstance: null, // 缓存 Notice 实例

    // getter 实现延迟初始化
    get notice() {
        if (!this._noticeInstance) {
            this._noticeInstance = new Notice();
        }
        return this._noticeInstance;
    },

    newAlert: function () {
        return new Alert();
    }
}