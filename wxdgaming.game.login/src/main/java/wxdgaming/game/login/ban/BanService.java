package wxdgaming.game.login.ban;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.event.StartEvent;
import wxdgaming.boot2.core.format.HexId;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.batis.DbDataTable;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.boot2.starter.scheduled.ann.Scheduled;
import wxdgaming.game.common.bean.ban.BanType;
import wxdgaming.game.login.entity.BanEntity;
import wxdgaming.game.login.inner.InnerService;

import java.util.Collection;
import java.util.HashSet;

/**
 * 封禁服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-29 15:38
 **/
@Slf4j
@Getter
@Service
public class BanService extends HoldApplicationContext {

    final InnerService innerService;
    final SqlDataHelper sqlDataHelper;
    final DbDataTable<BanEntity> banEntityDbDataTable;
    final HexId banHexId = new HexId(1);

    public BanService(InnerService innerService, PgsqlDataHelper sqlDataHelper) {
        this.innerService = innerService;
        this.sqlDataHelper = sqlDataHelper;
        this.banEntityDbDataTable = new DbDataTable<>(
                BanEntity.class,
                sqlDataHelper,
                entity -> entity.getUid(),
                entity -> {
                    if (entity.getBanType() == BanType.AccountLogin) {
                        return entity.getKey();
                    }
                    return null;
                }
        );
    }

    @EventListener
    public void startEvent(StartEvent event) {
        this.banEntityDbDataTable.loadAll();
    }

    /** 定时清理过期的 */
    @Scheduled("0 * * * ?")
    public void timerDelExpire() {
        HashSet<Long> longs = new HashSet<>();
        Collection<BanEntity> list = this.banEntityDbDataTable.getList();
        long millis = MyClock.millis();
        for (BanEntity banEntity : list) {
            if (millis > banEntity.getExpireTime()) {
                longs.add(banEntity.getUid());
            }
        }
        if (!longs.isEmpty()) {
            this.sqlDataHelper.batchDeleteByKey(BanEntity.class, longs);
        }
    }

    public void edit(BanEntity banEntity) {
        if (banEntity.getUid() == 0) {
            banEntity.setUid(banHexId.newId());
        }
        this.sqlDataHelper.save(banEntity);
        this.banEntityDbDataTable.loadAll();
        String jsonString = banEntity.toJSONString();
        JSONObject params = new JSONObject();
        params.put("data", jsonString);
        innerService.executeAllAsync("ban", "yunying/ban", params);
    }

    public void delete(long uid) {
        this.sqlDataHelper.deleteByKey(BanEntity.class, uid);
        JSONObject params = new JSONObject().fluentPut("uid", uid);
        innerService.executeAllAsync("ban", "yunying/banDel", params);
    }

}
