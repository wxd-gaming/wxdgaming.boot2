package wxdgaming.boot2.starter.batis.sql.mysql;

import wxdgaming.boot2.starter.batis.sql.SqlDataCacheService;

/**
 * 缓存服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-11-18 13:10
 **/
public class MysqlDataCacheService extends SqlDataCacheService {

    public MysqlDataCacheService(MysqlDataHelper sqlDataHelper) {
        super(sqlDataHelper);
        sqlDataHelper.setCacheService(this);
    }

}
