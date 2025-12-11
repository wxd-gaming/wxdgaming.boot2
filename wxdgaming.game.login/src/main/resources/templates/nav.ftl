<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script charset="utf-8" type="text/javascript" src="https://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js"></script>
    <script charset="utf-8" type="text/javascript" src="/js/com.wxd.js"></script>
    <link rel="stylesheet" type="text/css" href="/style/com.wxd.css"/>
    <title>系统导航</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            width: 200px;
            min-height: 100vh;
            background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
            color: #ecf0f1;
            font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
            padding: 0;
            overflow-x: hidden;
            overflow-y: auto;
        }

        /* 顶部区域 */
        .nav-header {
            padding: 20px 15px;
            text-align: center;
            border-bottom: 1px solid rgba(52, 152, 219, 0.3);
            background: rgba(26, 26, 46, 0.8);
            backdrop-filter: blur(10px);
        }

        .logo {
            font-size: 18px;
            font-weight: 700;
            color: #3498db;
            text-transform: uppercase;
            letter-spacing: 2px;
            margin-bottom: 10px;
            text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
        }

        .user-info {
            font-size: 12px;
            color: #bdc3c7;
            margin-bottom: 15px;
        }

        /* 退出按钮 */
        .logout-btn {
            background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
            color: white;
            border: none;
            padding: 10px 15px;
            border-radius: 8px;
            cursor: pointer;
            font-weight: 600;
            font-size: 13px;
            width: 100%;
            margin: 0;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.2);
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
        }

        .logout-btn:hover {
            background: linear-gradient(135deg, #c0392b 0%, #e74c3c 100%);
            transform: translateY(-2px);
            box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3);
        }

        .logout-btn:active {
            transform: translateY(0);
        }

        /* 导航菜单容器 */
        .nav-container {
            padding: 15px;
        }

        /* 菜单项样式 */
        .nav-menu {
            width: 100%;
            margin-bottom: 15px;
            background: rgba(44, 62, 80, 0.3);
            border-radius: 10px;
            border: 1px solid rgba(52, 152, 219, 0.2);
            overflow: hidden;
            transition: all 0.3s ease;
        }

        .nav-menu:hover {
            border-color: rgba(52, 152, 219, 0.4);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        }

        .menu-header {
            height: 45px;
            line-height: 45px;
            padding: 0 15px;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            display: flex;
            justify-content: space-between;
            align-items: center;
            background: rgba(52, 73, 94, 0.5);
            border-left: 4px solid transparent;
            transition: all 0.3s ease;
            color: #ecf0f1;
        }

        .menu-header:hover {
            background: rgba(52, 152, 219, 0.3);
            border-left-color: #3498db;
        }

        .menu-header.active {
            background: linear-gradient(90deg, rgba(52, 152, 219, 0.4) 0%, rgba(41, 128, 185, 0.4) 100%);
            border-left-color: #3498db;
            color: white;
        }

        .menu-header .arrow {
            font-size: 12px;
            transition: transform 0.3s ease;
            color: #bdc3c7;
        }

        .menu-header.active .arrow {
            transform: rotate(180deg);
            color: #3498db;
        }

        /* 子菜单样式 */
        .sub-menu {
            list-style: none;
            max-height: 0;
            overflow: hidden;
            transition: max-height 0.4s ease;
            background: rgba(30, 30, 46, 0.5);
        }

        .sub-menu.open {
            max-height: 400px;
            overflow-y: auto;
        }

        .sub-item {
            padding: 12px 20px;
            font-size: 13px;
            cursor: pointer;
            transition: all 0.3s ease;
            border-left: 3px solid transparent;
            color: #bdc3c7;
            position: relative;
        }

        .sub-item:hover {
            background: rgba(52, 152, 219, 0.2);
            color: #ecf0f1;
            padding-left: 25px;
            border-left-color: #2980b9;
        }

        .sub-item.active {
            background: rgba(46, 204, 113, 0.2);
            color: #27ae60;
            font-weight: 500;
            border-left-color: #27ae60;
        }

        .sub-item:not(:last-child) {
            border-bottom: 1px solid rgba(52, 73, 94, 0.3);
        }

        /* 滚动条样式 */
        .sub-menu.open::-webkit-scrollbar {
            width: 4px;
        }

        .sub-menu.open::-webkit-scrollbar-track {
            background: rgba(255, 255, 255, 0.05);
        }

        .sub-menu.open::-webkit-scrollbar-thumb {
            background: rgba(255, 255, 255, 0.2);
            border-radius: 2px;
        }

        .sub-menu.open::-webkit-scrollbar-thumb:hover {
            background: rgba(255, 255, 255, 0.3);
        }

        /* 动画效果 */
        @keyframes slideIn {
            from {
                opacity: 0;
                transform: translateX(-10px);
            }
            to {
                opacity: 1;
                transform: translateX(0);
            }
        }

        .nav-menu {
            animation: slideIn 0.5s ease-out;
        }

        /* 响应式调整 */
        @media (max-height: 600px) {
            .nav-header {
                padding: 15px 10px;
            }
            
            .menu-header {
                height: 40px;
                line-height: 40px;
                font-size: 13px;
            }
            
            .sub-item {
                padding: 10px 15px;
                font-size: 12px;
            }
        }
    </style>
    <script>
        function toggleMenu(self) {
            const $self = $(self);
            const $subMenu = $self.next('.sub-menu');
            
            // 切换当前菜单状态
            $self.toggleClass('active');
            
            // 切换子菜单显示
            $subMenu.toggleClass('open');
            
            // 关闭其他打开的菜单
            $('.menu-header').not($self).removeClass('active');
            $('.sub-menu').not($subMenu).removeClass('open');
        }

        function selectMenuItem(self, url) {
            // 移除所有激活状态
            $('.sub-item').removeClass('active');
            // 添加当前激活状态
            $(self).addClass('active');
            // 调用父窗口方法
            if (window.parent && window.parent.changeContent) {
                window.parent.changeContent(url);
            }
        }

        function logout() {
            sessionStorage.setItem("authorization", "");
            wxd.clearCookie();
            window.top.location.href = "/login.html";
        }

        $(document).ready(function() {
            // 默认展开第一个菜单
            $('.menu-header').first().addClass('active');
            $('.sub-menu').first().addClass('open');
            
            // 点击空白处关闭菜单
            $(document).on('click', function(e) {
                if (!$(e.target).closest('.menu-header').length && 
                    !$(e.target).closest('.sub-item').length &&
                    !$(e.target).closest('.logout-btn').length) {
                    $('.menu-header').removeClass('active');
                    $('.sub-menu').removeClass('open');
                }
            });
        });
    </script>
</head>
<body>
    <!-- 顶部区域 -->
    <div class="nav-header">
        <div class="logo">管理系统</div>
        <div class="user-info">欢迎使用</div>
        <button class="logout-btn" onclick="logout();">退出登录</button>
    </div>

    <!-- 导航菜单容器 -->
    <div class="nav-container">
        <!-- 导航菜单 -->
        <div class="nav-menu">
            <div class="menu-header active" onclick="toggleMenu(this)">
                系统导航
                <span class="arrow">▼</span>
            </div>
            <ul class="sub-menu open">
                <#list navList as nav>
                    <li class="sub-item" onclick="selectMenuItem(this, '${nav.routing}')">${nav.name}</li>
                </#list>
            </ul>
        </div>

        <!-- 日志菜单 -->
        <div class="nav-menu">
            <div class="menu-header" onclick="toggleMenu(this)">
                日志管理
                <span class="arrow">▼</span>
            </div>
            <ul class="sub-menu">
                <#list logNavList as nav>
                    <li class="sub-item" onclick="selectMenuItem(this, '${nav.routing}?tableName=${nav.name}')">${nav.comment}</li>
                </#list>
            </ul>
        </div>
    </div>
</body>
</html>