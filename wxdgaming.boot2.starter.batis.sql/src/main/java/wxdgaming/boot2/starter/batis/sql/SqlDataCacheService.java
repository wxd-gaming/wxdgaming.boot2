package wxdgaming.boot2.starter.batis.sql;

import lombok.Getter;
import wxdgaming.boot2.core.ann.Stop;
import wxdgaming.boot2.starter.batis.Entity;

import java.util.concurrent.ConcurrentHashMap;

/**
 * cache服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-22 09:13
 **/
@Getter
public class SqlDataCacheService {

    protected SqlDataHelper sqlDataHelper;
    protected final ConcurrentHashMap<Class<?>, SqlDataCache<?, ?>> jdbcCacheMap = new ConcurrentHashMap<>();

    public SqlDataCacheService(SqlDataHelper sqlDataHelper) {
        this.sqlDataHelper = sqlDataHelper;
    }

    @Stop
    public void stop() {
        jdbcCacheMap.values().forEach(SqlDataCache::stop);
    }

    /**
     * 通过cache获取对象
     *
     * @param cls 返回的数据实体类
     * @param <E> 实体模型
     * @param <K> 主键类型
     * @return 缓存集合
     */
    public <E extends Entity, K> SqlDataCache<E, K> cache(Class<E> cls) {
        SqlDataCache sqlDataCache = jdbcCacheMap.computeIfAbsent(
                cls,
                l -> new SqlDataCache<>(
                        cls,
                        this.sqlDataHelper,
                        this.sqlDataHelper.getSqlConfig().getCacheArea(),
                        this.sqlDataHelper.getSqlConfig().getCacheExpireAfterAccessM()
                )
        );
        return sqlDataCache;
    }

    /**
     * 通过cache获取对象
     *
     * @param cls 返回的数据实体类
     * @param k   主键值
     * @param <E> 实体模型
     * @param <K> 主键类型
     * @return 实体对象
     */
    public <E extends Entity, K> E cache(Class<E> cls, K k) throws NullPointerException {
        return cache(cls).get(k);
    }

    /**
     * 通过cache获取对象
     *
     * @param cls 返回的数据实体类
     * @param k   主键值
     * @param <E> 实体模型
     * @param <K> 主键类型
     * @return 实体对象
     */
    public <E extends Entity, K> E cacheIfPresent(Class<E> cls, K k) {
        return cache(cls).getIfPresent(k);
    }

}
