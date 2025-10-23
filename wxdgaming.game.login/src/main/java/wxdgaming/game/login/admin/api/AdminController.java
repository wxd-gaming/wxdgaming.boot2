package wxdgaming.game.login.admin.api;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.CacheHttpServletRequest;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.core.util.PatternUtil;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.boot2.starter.batis.sql.SqlQueryBuilder;
import wxdgaming.boot2.starter.batis.sql.WebSqlQueryCondition;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.game.authority.AdminUserToken;
import wxdgaming.game.login.admin.AdminService;
import wxdgaming.game.login.entity.AdminUserEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理接口
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-13 15:36
 **/
@Slf4j
@RestController
@RequestMapping("/admin/account")
public class AdminController implements InitPrint {

    final AdminService adminService;
    private final SqlDataHelper sqlDataHelper;

    public AdminController(AdminService adminService, PgsqlDataHelper sqlDataHelper) {
        this.adminService = adminService;
        this.sqlDataHelper = sqlDataHelper;
    }

    @RequestMapping("/add")
    public ResponseEntity<RunResult> add(CacheHttpServletRequest request,
                                         @RequestParam("userName") String userName,
                                         @RequestParam("admin") boolean admin,
                                         @RequestParam("password") String password,
                                         @RequestParam("phone") String phone) {

        AdminUserToken adminUserToken = AdminUserToken.threadContext();
        AssertUtil.isTrue(adminUserToken.isAdmin(), "没有权限");

        AssertUtil.isTrue(StringUtils.length(userName) > 4, "用户名长度不能小于5个字符");
        AssertUtil.isTrue(StringUtils.length(password) > 4, "密码长度不能小于5个字符");
        AssertUtil.isTrue(StringUtils.length(phone) >= 11, "手机号不正确");
        AssertUtil.isTrue(!PatternUtil.AdminSet.contains(userName.toUpperCase()), "用户名不合法");
        boolean b = PatternUtil.checkMatches(userName.toUpperCase(), PatternUtil.PATTERN_ACCOUNT);
        AssertUtil.isTrue(b, "用户名不合法,仅允许 数字，字母，汉字");

        AdminUserEntity adminUserEntity = adminService.findByName(userName);
        if (adminUserEntity != null) {
            return ResponseEntity.ok(RunResult.fail("用户名存在"));
        }

        adminService.add(admin, userName, password, phone);
        return ResponseEntity.ok(RunResult.ok().msg("成功"));
    }

    @RequestMapping("/edit")
    public ResponseEntity<RunResult> edit(CacheHttpServletRequest request,
                                          @RequestParam("userName") String userName,
                                          @RequestParam("admin") boolean admin,
                                          @RequestParam("password") String password,
                                          @RequestParam("phone") String phone) {
        AdminUserToken adminUserToken = AdminUserToken.threadContext();
        AssertUtil.isTrue(adminUserToken.isAdmin() || userName.equalsIgnoreCase(adminUserToken.getUserName()), "没有权限");
        AdminUserEntity adminUserEntity = adminService.findByName(userName);
        if (adminUserEntity == null) {
            return ResponseEntity.ok(RunResult.fail("用户名存在"));
        }

        if (!adminUserToken.isAdmin()) {
            admin = false;
        }

        if (userName.equalsIgnoreCase(adminUserToken.getUserName())) {
            admin = adminUserEntity.isAdmin();
        }

        AssertUtil.isTrue(StringUtils.length(password) > 4, "密码长度不能小于5个字符");
        AssertUtil.isTrue(StringUtils.length(phone) >= 11, "手机号不正确");

        adminService.save(adminUserEntity, admin, password, phone);
        return ResponseEntity.ok(RunResult.ok().msg("修改成功"));
    }

    @RequestMapping("/delete")
    public ResponseEntity<RunResult> delete(CacheHttpServletRequest request, @RequestParam("userName") String userName) {
        AdminUserToken adminUserToken = AdminUserToken.threadContext();
        AssertUtil.isTrue(adminUserToken.isAdmin(), "没有权限");
        AssertUtil.isTrue(!userName.equalsIgnoreCase(adminUserToken.getUserName()), "不能删除自己");
        adminService.delete(userName);
        return ResponseEntity.ok(RunResult.ok().msg("修改成功"));
    }

    @RequestMapping("/query")
    public ResponseEntity<RunResult> query(CacheHttpServletRequest request, @RequestParam("userName") String userName) {
        AdminUserToken adminUserToken = AdminUserToken.threadContext();
        AssertUtil.isTrue(adminUserToken.isAdmin() || userName.equalsIgnoreCase(adminUserToken.getUserName()), "没有权限");
        AdminUserEntity adminUserEntity = adminService.findByName(userName);

        JSONObject jsonObject = MapOf.newJSONObject();
        jsonObject.put("userName", adminUserEntity.getUserName());
        jsonObject.put("admin", adminUserEntity.isAdmin() ? 1 : 0);
        jsonObject.put("phone", adminUserEntity.getPhone());
        jsonObject.put("routes", adminUserEntity.getRoutes());

        return ResponseEntity.ok(RunResult.ok().data(jsonObject));
    }

    @RequestMapping("/queryList")
    public ResponseEntity<RunResult> queryList(CacheHttpServletRequest request, @RequestBody WebSqlQueryCondition condition) {
        AdminUserToken adminUserToken = AdminUserToken.threadContext();
        AssertUtil.isTrue(adminUserToken.isAdmin(), "没有权限");

        Class<AdminUserEntity> adminUserEntityClass = AdminUserEntity.class;
        SqlQueryBuilder sqlQueryBuilder = condition.build(sqlDataHelper, adminUserEntityClass, null);

        long rowCount = sqlQueryBuilder.findCount();
        List<AdminUserEntity> list2Entity = sqlQueryBuilder.findList2Entity(adminUserEntityClass);
        List<JSONObject> jsonList = new ArrayList<>();
        for (AdminUserEntity adminUserEntity : list2Entity) {
            JSONObject jsonObject = MapOf.newJSONObject();
            jsonObject.put("userName", adminUserEntity.getUserName());
            jsonObject.put("admin", adminUserEntity.isAdmin());
            jsonObject.put("phone", adminUserEntity.getPhone());
            jsonObject.put("routes", adminUserEntity.getRoutes());
            jsonList.add(jsonObject);
        }

        return ResponseEntity.ok(RunResult.ok().fluentPut("rowCount", rowCount).data(jsonList));
    }

}
