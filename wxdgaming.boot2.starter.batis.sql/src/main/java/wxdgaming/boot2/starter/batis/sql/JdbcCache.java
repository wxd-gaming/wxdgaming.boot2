package wxdgaming.boot2.starter.batis.sql;

import lombok.Getter;
import wxdgaming.boot2.core.cache.Cache;
import wxdgaming.boot2.core.reflect.ReflectContext;
import wxdgaming.boot2.starter.batis.Entity;
import wxdgaming.boot2.starter.batis.TableMapping;

import java.util.concurrent.TimeUnit;

/**
 * jdbc cahce
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-17 11:28
 **/
@Getter
public class JdbcCache<E extends Entity, UID> {

    protected final Class<E> cls;
    protected final TableMapping tableMapping;
    protected final SqlDataHelper<?> sqlDataHelper;
    protected final Cache<UID, E> cache;

    /**
     * 构建
     *
     * @param sqlDataHelper      数据库
     * @param expireAfterAccessM 滑动缓存过期时间
     */
    public JdbcCache(SqlDataHelper<?> sqlDataHelper, int expireAfterAccessM) {
        this.cls = ReflectContext.getTClass(this.getClass());
        this.sqlDataHelper = sqlDataHelper;
        this.tableMapping = this.sqlDataHelper.tableMapping(cls);
        cache = Cache.<UID, E>builder()
                .cacheName("cache-" + tableMapping.getTableName())
                .expireAfterAccess(expireAfterAccessM, TimeUnit.MINUTES)
                .heartTime(1, TimeUnit.MINUTES)
                .loader(this::loader)
                .heartListener(this::heart)
                .removalListener(this::removed)
                .build();
    }

    protected E loader(UID uid) {
        E byId = (E) sqlDataHelper.findByKey(cls, uid);
        if (byId != null) {
            byId.setNewEntity(false);
            byId.checkHashCode();
        }
        return byId;
    }

    protected void heart(UID uid, E e) {
        boolean checkHashCode = e.checkHashCode();
        if (checkHashCode) {
            sqlDataHelper.getDataBatch().save(e);
        }
    }

    protected boolean removed(UID uid, E e) {
        sqlDataHelper.update(e);
        return true;
    }


    /** 如果获取数据null 抛出异常 */
    public E get(UID key) {
        return cache.get(key);
    }

    /** 获取数据，如果没有数据返回null */
    public E getIfPresent(UID ID) {
        return cache.getIfPresent(ID);
    }

    public void put(UID key, E value) {
        sqlDataHelper.save(value);
        value.setNewEntity(false);
        cache.put(key, value);
    }

    /** 强制缓存过期 */
    public void invalidate(UID key) {
        cache.invalidate(key);
    }

}
