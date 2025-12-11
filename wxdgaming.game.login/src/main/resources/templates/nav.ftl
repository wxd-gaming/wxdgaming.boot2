<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script charset="utf-8" type="text/javascript" src="https://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js"></script>
    <script charset="utf-8" type="text/javascript" src="/js/com.wxd.js"></script>
    <link rel="stylesheet" type="text/css" href="/style/com.wxd.css"/>
    <title>nav</title>
    <style>
        body {
            overflow-x: hidden;
            overflow-y: auto;
            background: linear-gradient(135deg, #2c3e50 0%, #1a1a2e 100%);
            color: #ecf0f1;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            padding: 20px 0;
        }

        ul, li {
            width: 200px;
            box-sizing: border-box;
            margin: 0;
            padding: 0;
            list-style: none;
        }

        .title_box {
            border: 1px solid #34495e;
            border-radius: 8px;
            margin-bottom: 15px;
            background: rgba(30, 30, 46, 0.7);
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            transition: all 0.3s ease;
        }

        .title_box:hover {
            box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2);
            transform: translateY(-2px);
        }

        .title_box li {
            cursor: pointer;
        }

        .title_box.ul_left {
            border-left: 4px solid #3498db;
        }

        .title_one {
            height: 45px;
            line-height: 45px;
            padding: 0 15px;
            font-size: 15px;
            font-weight: 600;
            position: relative;
            border-bottom: 1px solid #34495e;
            background: rgba(44, 62, 80, 0.5);
            color: #ecf0f1;
            border-radius: 8px 8px 0 0;
            transition: all 0.3s ease;
        }

        .title_one:last-child {
            border-bottom: none;
            border-radius: 8px;
        }

        .title_one strong {
            position: absolute;
            right: 15px;
            font-weight: bold;
            font-size: 18px;
            transition: transform 0.3s ease;
        }

        .title_one.check {
            background: linear-gradient(90deg, #3498db 0%, #2980b9 100%);
            color: white;
        }

        .title_one.check strong {
            transform: rotate(180deg);
        }

        .title_one:hover {
            background: linear-gradient(90deg, #3498db 0%, #2980b9 100%);
            color: white;
        }

        .title_sub_box {
            overflow: hidden;
        }

        .title_sub {
            font-size: 14px;
            background: rgba(44, 62, 80, 0.3);
        }

        .title_sub li {
            padding: 12px 20px;
            height: auto;
            line-height: 1.4;
            border-bottom: 1px solid #34495e;
            transition: all 0.2s ease;
            color: #bdc3c7;
        }

        .title_sub li:last-child {
            border-bottom: none;
        }

        .title_sub li.check {
            background: rgba(52, 152, 219, 0.3);
            color: #3498db;
            font-weight: 500;
            padding-left: 25px;
            border-left: 3px solid #3498db;
        }

        .title_sub li:hover {
            background: rgba(52, 152, 219, 0.2);
            color: #ecf0f1;
            padding-left: 25px;
        }

        .logout-btn {
            background: linear-gradient(90deg, #e74c3c 0%, #c0392b 100%);
            color: white;
            border: none;
            padding: 12px 20px;
            border-radius: 6px;
            cursor: pointer;
            font-weight: 600;
            width: 200px;
            margin: 0 0 20px 0;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
            transition: all 0.3s ease;
        }

        .logout-btn:hover {
            background: linear-gradient(90deg, #c0392b 0%, #e74c3c 100%);
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
        }

        .logo {
            text-align: center;
            padding: 20px 0;
            font-size: 20px;
            font-weight: bold;
            color: #3498db;
            text-transform: uppercase;
            letter-spacing: 1px;
            border-bottom: 1px solid #34495e;
            margin-bottom: 20px;
        }
    </style>
    <script>
        function selectNav(self) {
            // 移除所有 title_one 的 check 类，然后给当前元素添加 check 类
            $(".title_one").removeClass("check");
            $(".title_one").parent("ul").removeClass("check");
            $(self).addClass("check");
            $(self).parent("ul").addClass("ul_left");
            let next = $(self).next("li");
            $(".title_sub_box").each(function () {
                if (this === next[0]) {
                    $(this).slideDown(500);
                    return;
                }
                $(this).slideUp(200);
            });
        }

        function selectNav2(self, url) {
            $(".title_sub li").removeClass("check");
            $(self).addClass("check");
            window.parent.changeContent(url);
        }

        function logout() {
            sessionStorage.setItem("authorization", "");
            wxd.clearCookie();
            window.top.location.href = "/login.html";
        }

        $(() => {
        });

    </script>
</head>
<body>
<div class="logo">管理系统</div>
<button class="logout-btn" onclick="logout();">退出登录</button>

<ul class="title_box ul_left">
    <li class="title_one check" onclick="selectNav(this)">导航<strong>∨</strong></li>
    <li class="title_sub_box">
        <ul class="title_sub">
            <#list navList as nav>
                <li onclick="selectNav2(this, '${nav.routing}')">${nav.name}</li>
            </#list>
        </ul>
    </li>
</ul>
<ul class="title_box">
    <li class="title_one" onclick="selectNav(this)">日志<strong>∨</strong></li>
    <li class="title_sub_box" style="display: none;">
        <ul class="title_sub">
            <#list logNavList as nav>
                <li onclick="selectNav2(this, '${nav.routing}?tableName=${nav.name}')">${nav.comment}</li>
            </#list>
        </ul>
    </li>
</ul>
</body>
</html>
