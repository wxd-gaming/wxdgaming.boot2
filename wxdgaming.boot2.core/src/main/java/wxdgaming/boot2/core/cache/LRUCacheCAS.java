package wxdgaming.boot2.core.cache;

import lombok.Builder;
import lombok.Getter;
import wxdgaming.boot2.core.function.Consumer3;
import wxdgaming.boot2.core.function.Predicate3;
import wxdgaming.boot2.core.util.AssertUtil;

import java.time.Duration;
import java.util.Collection;
import java.util.function.Function;

/**
 * 缓存容器，通过3层架构实现，
 * <p>1. 核心缓存，用于存储缓存数据，真正的缓存数据容器，当缓存过期，触发删除监听
 * <p>2. 心跳缓存，用于存储缓存心跳数据,用户心跳处理，比如获取缓存后修改了数据需要存储数据，可以通过这个监听
 * <p>3. 外部缓存，用于存储缓存过期数据,固定2秒实现，也是防止缓存穿透关键一步
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-07-03 10:57
 **/
@Getter
public class LRUCacheCAS<K, V> implements Cache<K, V> {

    /** 核心驱动 */
    private LRUCacheHolderCAS<K, V> coreDriver;
    /** 心跳驱动 */
    private LRUCacheHolderCAS<K, V> heartDriver;
    /** 外置驱动 */
    private LRUCacheHolderCAS<K, Hold<V>> outerDriver;

    /** 缓存名称 */
    private final String cacheName;
    private final int blockSize;
    /** 心跳时间 */
    private final Duration heartExpireAfterWrite;
    /** 读取过期时间 */
    private final Duration expireAfterAccess;
    /** 写入过期时间 */
    private final Duration expireAfterWrite;
    private final Function<K, V> loader;
    private final Consumer3<K, V, RemovalCause> heartListener;
    private final Predicate3<K, V, RemovalCause> removalListener;

    @Builder
    public LRUCacheCAS(String cacheName, int blockSize,
                       Duration heartExpireAfterWrite,
                       Duration expireAfterAccess, Duration expireAfterWrite,
                       Function<K, V> loader, Consumer3<K, V, RemovalCause> heartListener,
                       Predicate3<K, V, RemovalCause> removalListener) {
        this.cacheName = cacheName;
        this.blockSize = blockSize;
        this.heartExpireAfterWrite = heartExpireAfterWrite == null ? CacheConst.heartDurationDefault : heartExpireAfterWrite;
        this.expireAfterAccess = expireAfterAccess;
        this.expireAfterWrite = expireAfterWrite;
        this.loader = loader;
        this.heartListener = heartListener;
        this.removalListener = removalListener;
        init();
    }

    private void init() {

        Duration removeDuration = this.expireAfterWrite == null ? this.expireAfterAccess : this.expireAfterWrite;
        if (removeDuration != null) {
            AssertUtil.isTrue(removeDuration.toMillis() >= this.heartExpireAfterWrite.toMillis() * 3, "过期时间不得低于心跳时间的3倍");
        }

        AssertUtil.isTrue(this.heartExpireAfterWrite.toMillis() >= CacheConst.minHeartDuration.toMillis(), "心跳时间不得低于%s秒", CacheConst.minHeartDuration.toSeconds());

        coreDriver = LRUCacheHolderCAS.<K, V>builder()
                .loader(loader)
                .blockSize(blockSize)
                .removalListener(removalListener)
                .expireAfterAccess(expireAfterAccess)
                .expireAfterWrite(expireAfterWrite)
                .build();

        heartDriver = LRUCacheHolderCAS.<K, V>builder()
                .blockSize(blockSize)
                .loader(key -> coreDriver.get(key))
                .expireAfterWrite(this.heartExpireAfterWrite)
                .removalListener((k, v, removalCause) -> {
                    if (heartListener != null)
                        heartListener.accept(k, v, removalCause);
                    return true;
                })
                .build();

        outerDriver = LRUCacheHolderCAS.<K, Hold<V>>builder()
                .blockSize(blockSize)
                .loader(key -> new Hold<V>(heartDriver.get(key)))
                .expireAfterWrite(CacheConst.outerDuration)
                .removalListener((k, v, removalCause) -> {
                    if (removalCause == RemovalCause.EXPIRE) {
                        /*刷新一次*/
                        heartDriver.get(k);
                    }
                    return true;
                })
                .build();

    }

    @Override public void close() {
        coreDriver.close();
        heartDriver.close();
        outerDriver.close();
    }

    /** 计算内存大小 注意特别耗时，并且可能死循环 */
    @Override public long memorySize() {
        return 0;
    }

    @Override public long size() {
        return coreDriver.size();
    }

    @Override public boolean has(K k) {
        return coreDriver.has(k);
    }

    @Override public Collection<V> values() {
        return coreDriver.values();
    }

    @Override public V get(K k) {
        Hold<V> hold = outerDriver.get(k);
        return hold.v();
    }

    @Override public void put(K k, V v) {
        outerDriver.invalidate(k, RemovalCause.REPLACED);
        heartDriver.invalidate(k, RemovalCause.REPLACED);
        coreDriver.put(k, v);
    }

    @Override public void invalidate(K k) {
        outerDriver.invalidate(k, RemovalCause.EXPLICIT);
        heartDriver.invalidate(k, RemovalCause.EXPLICIT);
        coreDriver.invalidate(k, RemovalCause.EXPLICIT);
    }

    @Override public void invalidateAll() {
        invalidateAll(RemovalCause.EXPLICIT);
    }

    @Override public void invalidateAll(RemovalCause cause) {
        outerDriver.invalidateAll(cause);
        heartDriver.invalidateAll(cause);
        coreDriver.invalidateAll(cause);
    }

    /** 强制刷新，定时清理过期数据可能出现延迟，所以也可以手动调用清理 */
    @Override public void refresh() {
        outerDriver.cleanup();
        heartDriver.cleanup();
        coreDriver.cleanup();
    }

}
