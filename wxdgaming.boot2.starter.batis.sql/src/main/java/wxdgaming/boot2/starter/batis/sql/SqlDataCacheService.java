package wxdgaming.boot2.starter.batis.sql;

import lombok.Getter;
import wxdgaming.boot2.starter.batis.Entity;

import java.util.concurrent.ConcurrentHashMap;

/**
 * cache服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-22 09:13
 **/
@Getter
public class SqlDataCacheService {

    protected SqlDataHelper<?> sqlDataHelper;
    protected final ConcurrentHashMap<Class<?>, SqlDataCache<?, ?>> jdbcCacheMap = new ConcurrentHashMap<>();

    public SqlDataCacheService(SqlDataHelper<?> sqlDataHelper) {
        this.sqlDataHelper = sqlDataHelper;
    }

    /**
     * 通过cache获取对象
     *
     * @param cls 返回的数据实体类
     * @param k   主键值
     * @param <R> 实体模型
     * @param <K> 主键类型
     * @return 实体对象
     */
    public <R extends Entity, K> R cache(Class<R> cls, K k) {
        SqlDataCache<?, ?> sqlDataCache = jdbcCacheMap.computeIfAbsent(
                cls,
                l -> new SqlDataCache<>(cls, this.sqlDataHelper, 1, 120)
        );
        return ((SqlDataCache<R, K>) sqlDataCache).get(k);
    }

}
