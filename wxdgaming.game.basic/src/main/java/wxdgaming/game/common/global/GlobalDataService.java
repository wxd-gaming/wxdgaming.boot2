package wxdgaming.game.common.global;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.BootstrapProperties;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.starter.batis.sql.SqlDataCache;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.game.common.bean.global.AbstractGlobalData;
import wxdgaming.game.common.bean.global.IGlobalDataConst;
import wxdgaming.game.common.entity.global.GlobalDataEntity;

/**
 * 全局数据服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-08 20:32
 **/
@Slf4j
@Service
public class GlobalDataService implements InitPrint {

    final BootstrapProperties bootstrapProperties;
    final SqlDataHelper sqlDataHelper;

    public GlobalDataService(BootstrapProperties bootstrapProperties, PgsqlDataHelper pgsqlDataHelper) {
        this.bootstrapProperties = bootstrapProperties;
        this.sqlDataHelper = pgsqlDataHelper;
    }

    public <R extends AbstractGlobalData> R get(IGlobalDataConst type) {
        return globalData(bootstrapProperties.getSid(), type, sqlDataHelper);
    }

    @SuppressWarnings("unchecked")
    public synchronized <R> R globalData(int sid, IGlobalDataConst globalDataConst, SqlDataHelper sqlDataHelper) {
        int uid = sid * 10000 + globalDataConst.getCode();
        SqlDataCache<GlobalDataEntity, Object> cache = sqlDataHelper.getCacheService().cache(GlobalDataEntity.class);
        GlobalDataEntity byKey = cache.getIfPresent(uid);
        if (byKey == null) {
            AbstractGlobalData abstractGlobalData = globalDataConst.getFactory().get();
            abstractGlobalData.setSid(sid);
            abstractGlobalData.setType(globalDataConst.getCode());
            abstractGlobalData.setComment(globalDataConst.getComment());
            abstractGlobalData.setMerger(false);
            GlobalDataEntity globalDataEntity = abstractGlobalData.globalEntity();
            cache.put(uid, globalDataEntity);
            return (R) abstractGlobalData;
        } else {
            return (R) byKey.getData();
        }
    }

}
