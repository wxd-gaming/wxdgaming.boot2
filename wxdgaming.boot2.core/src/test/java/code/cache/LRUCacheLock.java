package code.cache;

import lombok.Builder;
import lombok.Getter;
import wxdgaming.boot2.core.function.Consumer3;
import wxdgaming.boot2.core.function.Predicate3;
import wxdgaming.boot2.core.util.AssertUtil;

import java.time.Duration;
import java.util.function.Function;

/**
 * 加载缓存
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-07-03 10:57
 **/
@Getter
public class LRUCacheLock<K, V> {

    private LRUCacheHolderLock<K, V> cacheDriver;
    private LRUCacheHolderLock<K, V> heartDriver;
    private LRUCacheHolderLock<K, Hold> outerDriver;

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
    public LRUCacheLock(String cacheName, int blockSize,
                        Duration heartExpireAfterWrite,
                        Duration expireAfterAccess,
                        Duration expireAfterWrite,
                        Function<K, V> loader,
                        Consumer3<K, V, RemovalCause> heartListener,
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

        AssertUtil.isTrue(
                this.heartExpireAfterWrite.toMillis() >= CacheConst.minHeartDuration.toMillis(),
                "心跳时间不得低于%s秒", CacheConst.minHeartDuration.toSeconds()
        );

        cacheDriver = LRUCacheHolderLock.<K, V>builder()
                .loader(loader)
                .blockSize(blockSize)
                .removalListener(removalListener)
                .expireAfterAccess(expireAfterAccess)
                .expireAfterWrite(expireAfterWrite)
                .build();

        heartDriver = LRUCacheHolderLock.<K, V>builder()
                .blockSize(blockSize)
                .loader(key -> cacheDriver.get(key))
                .expireAfterWrite(this.heartExpireAfterWrite)
                .removalListener((k, v, removalCause) -> {
                    if (removalCause != RemovalCause.EXPIRE) return true;
                    if (heartListener != null)
                        heartListener.accept(k, v, removalCause);
                    return true;
                })
                .build();

        outerDriver = LRUCacheHolderLock.<K, Hold>builder()
                .blockSize(blockSize)
                .loader(key -> new Hold(heartDriver.get(key)))
                .expireAfterWrite(CacheConst.outerDuration)
                .removalListener((k, v, removalCause) -> {
                    if (removalCause != RemovalCause.EXPIRE) {
                        /*刷新一次*/
                        heartDriver.get(k);
                    }
                    return true;
                })
                .build();

    }

    /** 计算内存大小 注意特别耗时，并且可能死循环 */
    public long memorySize() {
        return 0;
    }

    private class Hold {
        final V v;

        public Hold(V v) {
            this.v = v;
        }
    }

    public long size() {
        return cacheDriver.size();
    }

    public V get(K k) {
        Hold hold = outerDriver.get(k);
        return hold.v;
    }

    public void put(K k, V v) {
        outerDriver.invalidate(k, RemovalCause.REPLACED);
        heartDriver.invalidate(k, RemovalCause.REPLACED);
        cacheDriver.put(k, v);
    }

    public void invalidate(K k) {
        outerDriver.invalidate(k, RemovalCause.EXPLICIT);
        heartDriver.invalidate(k, RemovalCause.EXPLICIT);
        cacheDriver.invalidate(k, RemovalCause.EXPLICIT);
    }

    public void invalidateAll() {
        invalidateAll(RemovalCause.EXPLICIT);
    }

    public void invalidateAll(RemovalCause cause) {
        outerDriver.invalidateAll();
        heartDriver.invalidateAll();
        cacheDriver.invalidateAll();
    }

    /** 强制刷新，定时清理过期数据可能出现延迟，所以也可以手动调用清理 */
    public void refresh() {
        outerDriver.cleanup();
        heartDriver.cleanup();
        cacheDriver.cleanup();
    }

}
