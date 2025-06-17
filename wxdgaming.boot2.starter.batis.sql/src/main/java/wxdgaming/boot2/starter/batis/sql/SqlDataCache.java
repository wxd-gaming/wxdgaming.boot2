package wxdgaming.boot2.starter.batis.sql;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.cache2.Cache;
import wxdgaming.boot2.core.cache2.LRUCache;
import wxdgaming.boot2.core.reflect.ReflectProvider;
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
@Slf4j
@Getter
public class SqlDataCache<E extends Entity, Key> {

    protected Class<E> cls;
    protected TableMapping tableMapping;
    protected SqlDataHelper<?> sqlDataHelper;
    protected Cache<Key, E> cache;

    /**
     * 构建
     *
     * @param sqlDataHelper      数据库操作
     * @param hashArea           分区数量
     * @param expireAfterAccessM 过期时间，单位分钟
     */
    public SqlDataCache(SqlDataHelper<?> sqlDataHelper, int hashArea, int expireAfterAccessM) {
        this.init(ReflectProvider.getTClass(this.getClass()), sqlDataHelper, hashArea, expireAfterAccessM);
    }

    /**
     * 构建
     *
     * @param cls                绑定表实体类
     * @param sqlDataHelper      数据库操作
     * @param hashArea           分区数量
     * @param expireAfterAccessM 过期时间，单位分钟
     */
    public SqlDataCache(Class<E> cls, SqlDataHelper<?> sqlDataHelper, int hashArea, int expireAfterAccessM) {
        this.init(cls, sqlDataHelper, hashArea, expireAfterAccessM);
    }

    protected void init(Class<E> cls, SqlDataHelper<?> sqlDataHelper, int hashArea, int expireAfterAccessM) {
        this.cls = cls;
        this.sqlDataHelper = sqlDataHelper;
        this.tableMapping = this.sqlDataHelper.tableMapping(cls);
        this.cache = LRUCache.<Key, E>builder()
                .cacheName("cache-" + tableMapping.getTableName())
                .area(hashArea)
                .expireAfterReadMs(TimeUnit.MINUTES.toMillis(expireAfterAccessM))
                .heartTimeMs(TimeUnit.SECONDS.toMillis(10))
                .loader(this::loader)
                .heartListener(this::heart)
                .removalListener(this::removed)
                .build();
        this.start();
    }

    public void start() {
        getCache().start();
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
        log.info("缓存移除：{}, {}, {}", cls, key, e);
        sqlDataHelper.getDataBatch().update(e);
        return true;
    }

    /** 是否包含kay */
    public boolean has(Key k) {
        return cache.has(k);
    }

    /** 如果获取数据null 抛出异常 */
    public E get(Key key) throws NullPointerException {
        return cache.get(key);
    }

    /** 获取数据，如果没有数据返回null */
    public E getIfPresent(Key ID) {
        return cache.getIfPresent(ID);
    }

    /** 如果数据不存在，不会加载数据库，返回null */
    public E find(Key ID) {
        if (!cache.has(ID)) return null;
        return cache.getIfPresent(ID);
    }

    public void put(Key key, E value) {
        sqlDataHelper.save(value);
        value.setNewEntity(false);
        cache.put(key, value);
    }

    public E putIfAbsent(Key key, E value) {
        sqlDataHelper.save(value);
        value.setNewEntity(false);
        return cache.putIfAbsent(key, value);
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
    public void discard(Key key) {
        cache.discard(key);
    }

    /** 丢弃所有缓存，操作非常危险 */
    @Deprecated
    public void discardAll() {
        cache.discardAll();
    }
}
