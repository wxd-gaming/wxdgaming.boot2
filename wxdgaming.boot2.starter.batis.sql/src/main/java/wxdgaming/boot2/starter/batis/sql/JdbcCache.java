package wxdgaming.boot2.starter.batis.sql;

import lombok.Getter;
import wxdgaming.boot2.core.cache.Cache;
import wxdgaming.boot2.core.reflect.ReflectContext;
import wxdgaming.boot2.starter.batis.Entity;
import wxdgaming.boot2.starter.batis.TableMapping;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * jdbc cache
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-17 11:28
 **/
@Getter
public class JdbcCache<E extends Entity, Key> {

    protected final Class<E> cls;
    protected final TableMapping tableMapping;
    protected final SqlDataHelper<?> sqlDataHelper;
    protected final Cache<Key, E> cache;

    /**
     * 构建
     *
     * @param sqlDataHelper      数据库
     * @param expireAfterAccessM 滑动缓存过期时间
     */
    public JdbcCache(SqlDataHelper<?> sqlDataHelper, int hashArea, int expireAfterAccessM) {
        this.cls = ReflectContext.getTClass(this.getClass());
        this.sqlDataHelper = sqlDataHelper;
        this.tableMapping = this.sqlDataHelper.tableMapping(cls);
        cache = Cache.<Key, E>builder()
                .cacheName("cache-" + tableMapping.getTableName())
                .hashArea(hashArea)
                .expireAfterAccess(expireAfterAccessM, TimeUnit.MINUTES)
                .delay(1, TimeUnit.MINUTES)
                .heartTime(1, TimeUnit.MINUTES)
                .loader(this::loader)
                .heartListener(this::heart)
                .removalListener(this::removed)
                .build();
    }

    public void shutdown() {
        getCache().shutdown();
    }

    protected E loader(Key key) {
        E byId = (E) sqlDataHelper.findByKey(cls, key);
        if (byId != null) {
            byId.setNewEntity(false);
            byId.checkHashCode();
        }
        return byId;
    }

    protected void heart(Key key, E e) {
        boolean checkHashCode = e.checkHashCode();
        if (checkHashCode) {
            sqlDataHelper.getDataBatch().save(e);
        }
    }

    protected boolean removed(Key key, E e) {
        sqlDataHelper.update(e);
        return true;
    }

    /** 是否包含kay */
    public boolean containsKey(Key k) {
        return cache.containsKey(k);
    }

    /** 如果获取数据null 抛出异常 */
    public E get(Key key) {
        return cache.get(key);
    }

    /** 获取数据，如果没有数据返回null */
    public E getIfPresent(Key ID) {
        return cache.getIfPresent(ID);
    }

    /** 如果数据不存在，不会加载数据库，返回null */
    public E find(Key ID) {
        return cache.getIfPresent(ID, null);
    }

    public void put(Key key, E value) {
        sqlDataHelper.save(value);
        value.setNewEntity(false);
        cache.put(key, value);
    }

    /** 强制缓存过期 */
    public void invalidateAll() {
        cache.invalidateAll();
    }

    /** 强制缓存过期 */
    public void invalidate(Key key) {
        cache.invalidate(key);
    }

    public Collection<E> values() {
        return cache.values();
    }

    /** 丢弃所有缓存，操作非常危险 */
    @Deprecated
    public void discard() {
        cache.discard();
    }
}
