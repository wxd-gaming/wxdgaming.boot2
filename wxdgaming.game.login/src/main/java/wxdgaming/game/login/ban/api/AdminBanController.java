package wxdgaming.game.login.ban.api;

import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.CacheHttpServletRequest;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.starter.batis.sql.SqlQueryBuilder;
import wxdgaming.boot2.starter.batis.sql.WebSqlQueryCondition;
import wxdgaming.game.common.bean.ban.BanType;
import wxdgaming.game.login.ban.BanService;
import wxdgaming.game.login.entity.BanEntity;
import wxdgaming.game.util.Util;

import java.util.List;
import java.util.TreeMap;

/**
 * 封禁接口
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-29 15:39
 **/
@Slf4j
@RestController
@RequestMapping("/admin/ban")
public class AdminBanController implements InitPrint {

    final BanService banService;

    public AdminBanController(BanService banService) {
        this.banService = banService;
    }

    @RequestMapping("/edit")
    public RunResult edit(HttpServletRequest request,
                          @RequestParam(value = "uid", required = false) Long uid,
                          @RequestParam(value = "banType") String banType,
                          @RequestParam(value = "banKey") String banKey,
                          @RequestParam(value = "expireTime") String webExpireTime,
                          @RequestParam(value = "comment") String comment) {

        BanEntity banEntity = new BanEntity();
        if (uid != null) {
            banEntity.setUid(uid);
        }
        banEntity.setBanType(BanType.valueOf(banType));
        banEntity.setKey(banKey);
        banEntity.setComment(comment);
        banEntity.setExpireTime(Util.parseWebDate(webExpireTime));
        banService.edit(banEntity);
        return RunResult.ok().msg("成功");
    }


    @RequestMapping(value = "/del")
    public RunResult del(CacheHttpServletRequest context, @RequestParam(value = "uid") long uid) {
        this.banService.delete(uid);
        return RunResult.ok().msg("删除成功");
    }

    @RequestMapping(value = "/queryList")
    public RunResult queryGameServerList(HttpServletRequest context, @RequestBody WebSqlQueryCondition condition) {

        condition.setDefaultOrderBy("uid desc");
        SqlQueryBuilder queryBuilder = condition.build(banService.getSqlDataHelper(), BanEntity.class, "uid");

        long rowCount = queryBuilder.findCount();
        List<BanEntity> list2Entity = queryBuilder.findList2Entity(BanEntity.class);
        List<JSONObject> list = list2Entity.stream()
                .map(entity -> {
                    JSONObject jsonObject = entity.toJSONObject();
                    jsonObject.put("expireTime", Util.formatWebDate(entity.getExpireTime()));
                    return jsonObject;
                })
                .toList();
        return RunResult.ok().fluentPut("rowCount", rowCount).data(list);
    }

}
