package wxdgaming.game.login.inner;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.game.bean.info.InnerServerInfoBean;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 内网服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-10 20:52
 **/
@Slf4j
@Getter
@Singleton
public class InnerService {

    final SqlDataHelper<?> sqlDataHelper;
    final ConcurrentHashMap<Integer, InnerServerInfoBean> innerGameServerInfoMap = new ConcurrentHashMap<>();
    final ConcurrentHashMap<Integer, InnerServerInfoBean> innerGatewayServerInfoMap = new ConcurrentHashMap<>();

    @Inject
    @SuppressWarnings({"rawtypes"})
    public InnerService(SqlDataHelper sqlDataHelper) {
        this.sqlDataHelper = sqlDataHelper;
        this.sqlDataHelper.checkTable(InnerServerInfoBean.class);
    }

    @Start
    public void start() {
        sqlDataHelper.findList(InnerServerInfoBean.class).forEach(bean -> {
            log.info("InnerService: {}", bean);
            innerGameServerInfoMap.put(bean.getServerId(), bean);
        });
    }

}
