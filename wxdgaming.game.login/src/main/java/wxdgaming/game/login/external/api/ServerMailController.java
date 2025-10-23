package wxdgaming.game.login.external.api;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.boot2.starter.batis.sql.SqlQueryBuilder;
import wxdgaming.boot2.starter.batis.sql.WebSqlQueryCondition;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.game.login.LoginServerProperties;
import wxdgaming.game.login.entity.ServerMailEntity;
import wxdgaming.game.login.smail.ServerMailService;
import wxdgaming.game.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * 全服邮件接口
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-23 13:36
 **/
@Slf4j
@Controller
@RequestMapping("/admin/serverMail")
public class ServerMailController implements InitPrint {


    final LoginServerProperties loginServerProperties;
    final SqlDataHelper sqlDataHelper;
    final ServerMailService serverMailService;

    public ServerMailController(LoginServerProperties loginServerProperties, PgsqlDataHelper sqlDataHelper, ServerMailService serverMailService) {
        this.loginServerProperties = loginServerProperties;
        this.sqlDataHelper = sqlDataHelper;
        this.serverMailService = serverMailService;
    }


    @RequestMapping("/queryList")
    public RunResult queryList(@RequestBody WebSqlQueryCondition condition) {
        condition.setDefaultOrderBy("createtime desc");
        SqlQueryBuilder queryBuilder = condition.build(sqlDataHelper, ServerMailEntity.class, "createtime");

        long rowCount = queryBuilder.findCount();
        List<ServerMailEntity> list2Entity = queryBuilder.findList2Entity(ServerMailEntity.class);

        List<JSONObject> list = new ArrayList<>();
        for (ServerMailEntity serverMailEntity : list2Entity) {
            JSONObject jsonObject = serverMailEntity.toJSONObject();
            jsonObject.put("createTime", Util.formatWebDate(serverMailEntity.getCreateTime()));
            jsonObject.put("auditTime", Util.formatWebDate(serverMailEntity.getAuditTime()));
            list.add(jsonObject);
        }
        return RunResult.ok().fluentPut("rowCount", rowCount).data(list);
    }
}
