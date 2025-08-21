package wxdgaming.game.login.inner;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.ann.Stop;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.game.basic.login.bean.info.InnerServerInfoBean;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 内网服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-10 20:52
 **/
@Slf4j
@Getter
@Service
public class InnerService {

    final SqlDataHelper sqlDataHelper;
    final ConcurrentHashMap<Integer, InnerServerInfoBean> innerGameServerInfoMap = new ConcurrentHashMap<>();
    final ConcurrentHashMap<Integer, InnerServerInfoBean> innerGatewayServerInfoMap = new ConcurrentHashMap<>();

    public InnerService(PgsqlDataHelper sqlDataHelper) {
        this.sqlDataHelper = sqlDataHelper;
        this.sqlDataHelper.checkTable(InnerServerInfoBean.class);

        sqlDataHelper.findList(InnerServerInfoBean.class).forEach(bean -> {
            log.info("InnerService: {}", bean);
            innerGameServerInfoMap.put(bean.getServerId(), bean);
        });

    }


    @Order(10)
    @Stop
    public void stop() {
        innerGameServerInfoMap.values().forEach(bean -> {
            sqlDataHelper.getDataBatch().save(bean);
        });
    }

    public InnerServerInfoBean idleGateway() {
        return innerGatewayServerInfoMap.values()
                .stream()
                .filter(bean -> MyClock.millis() - bean.getLastSyncTime() < TimeUnit.SECONDS.toMillis(15))
                .min((o1, o2) -> {
                    int free1 = o1.free();
                    int free2 = o2.free();
                    if (free1 != free2) {
                        return Integer.compare(free2, free1);
                    }
                    return Integer.compare(o1.getServerId(), o2.getServerId());
                })
                .orElse(null);
    }

}
