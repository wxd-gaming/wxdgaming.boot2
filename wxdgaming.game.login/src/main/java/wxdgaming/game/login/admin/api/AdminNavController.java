package wxdgaming.game.login.admin.api;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import wxdgaming.boot2.core.Const;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.SpringUtil;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.starter.net.httpclient5.HttpRequestPost;
import wxdgaming.boot2.starter.net.httpclient5.HttpResponse;
import wxdgaming.game.authority.AdminUserToken;
import wxdgaming.game.login.admin.AdminService;
import wxdgaming.logbus.LogBusProperties;

import java.util.List;

/**
 * 管理接口
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-13 15:36
 **/
@Slf4j
@Controller
@RequestMapping("/admin/")
public class AdminNavController implements InitPrint {

    final List<NavItem> navList = List.of(
            new NavItem() {{
                name = "管理员列表";
                routing = "/admin-table.html";
                lv = 10;
            }},
            new NavItem() {{
                name = "区服列表";
                routing = "/servertable.html";
                lv = 1;
            }},
            new NavItem() {{
                name = "玩家账号列表";
                routing = "/user-table.html";
                lv = 1;
            }},
            new NavItem() {{
                name = "礼包码";
                routing = "/gift-code-table.html";
                lv = 1;
            }},
            new NavItem() {{
                name = "公告";
                routing = "/notice-table.html";
                lv = 1;
            }}
    );
    final AdminService adminService;
    final LogBusProperties logBusProperties;

    public AdminNavController(AdminService adminService, LogBusProperties logBusProperties) {
        this.adminService = adminService;
        this.logBusProperties = logBusProperties;
    }

    @RequestMapping("/nav")
    public ModelAndView nav(HttpServletRequest request) {
        String author = SpringUtil.getAuthor(request);
        AdminUserToken adminUserToken = AdminUserToken.threadContext();
        List<NavItem> list = navList.stream().filter(navItem -> navItem.lv <= adminUserToken.getLv() || adminUserToken.isAdmin()).toList();
        ModelAndView modelAndView = new ModelAndView("nav"); // 使用 ModelAndView 返回视图名称和模型数据
        modelAndView.addObject("navList", list);
        HttpResponse execute = HttpRequestPost.of(logBusProperties.getPostUrl() + "/admin/log/nav")
                .addHeader(Const.authorization, author)
                .execute();
        RunResult runResult = execute.bodyRunResult();
        if (runResult.isOk()) {
            List<JSONObject> data = runResult.getObject("data", new TypeReference<List<JSONObject>>() {});
            for (JSONObject datum : data) {
                datum.put("routing", logBusProperties.getPostUrl() + datum.getString("routing"));
            }
            modelAndView.addObject("logNavList", data); // 添加模型数据到视图中
        } else {
            log.error("获取日志导航失败: {}", runResult);
        }
        return modelAndView;
    }

    @Getter
    @Setter
    public static class NavItem {
        public String name;
        public String routing;
        public int lv;
    }

}
