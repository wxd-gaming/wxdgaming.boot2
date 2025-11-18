package wxdgaming.boot2.starter.batis.sql.pgsql;

import wxdgaming.boot2.starter.batis.sql.SqlDataCacheService;

/**
 * 缓存服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-11-18 13:10
 **/
public class PgsqlDataCacheService extends SqlDataCacheService {

    public PgsqlDataCacheService(PgsqlDataHelper sqlDataHelper) {
        super(sqlDataHelper);
        sqlDataHelper.setCacheService(this);
    }

}
