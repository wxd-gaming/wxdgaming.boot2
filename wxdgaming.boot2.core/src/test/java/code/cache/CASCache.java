package code.cache;

import lombok.Builder;
import lombok.Getter;
import wxdgaming.boot2.core.function.Consumer3;
import wxdgaming.boot2.core.function.Predicate3;
import wxdgaming.boot2.core.util.AssertUtil;

import java.time.Duration;
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
public class CASCache<K, V> {

    static final Duration minHeartDuration = Duration.ofSeconds(5);
    static final Duration outerDuration = Duration.ofSeconds(2);
    static final Duration heartDurationDefault = Duration.ofSeconds(5);

    public enum RemovalCause {
        /** 替换 */
        REPLACED,
        /** 过期删除 */
        EXPIRE,
        /** 手动删除 */
        EXPLICIT,
        ;
    }


    private CASCacheHolder<K, V> coreCacheReference;
    private CASCacheHolder<K, V> heartCacheReference;
    private CASCacheHolder<K, Hold> outerCacheReference;

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
    public CASCache(String cacheName, int blockSize,
                    Duration heartExpireAfterWrite,
                    Duration expireAfterAccess, Duration expireAfterWrite,
                    Function<K, V> loader, Consumer3<K, V, RemovalCause> heartListener,
                    Predicate3<K, V, RemovalCause> removalListener) {
        this.cacheName = cacheName;
        this.blockSize = blockSize;
        this.heartExpireAfterWrite = heartExpireAfterWrite == null ? heartDurationDefault : heartExpireAfterWrite;
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

        AssertUtil.isTrue(this.heartExpireAfterWrite.toMillis() >= minHeartDuration.toMillis(), "心跳时间不得低于%s秒", minHeartDuration.toSeconds());

        coreCacheReference = CASCacheHolder.<K, V>builder()
                .loader(loader)
                .blockSize(blockSize)
                .removalListener(removalListener)
                .expireAfterAccess(expireAfterAccess)
                .expireAfterWrite(expireAfterWrite)
                .build();

        heartCacheReference = CASCacheHolder.<K, V>builder()
                .blockSize(blockSize)
                .loader(key -> coreCacheReference.get(key))
                .expireAfterWrite(this.heartExpireAfterWrite)
                .removalListener((k, v, removalCause) -> {
                    if (heartListener != null)
                        heartListener.accept(k, v, removalCause);
                    return true;
                })
                .build();

        outerCacheReference = CASCacheHolder.<K, Hold>builder()
                .blockSize(blockSize)
                .loader(key -> new Hold(heartCacheReference.get(key)))
                .expireAfterWrite(outerDuration)
                .build();

    }

    public void close() {
        coreCacheReference.close();
        heartCacheReference.close();
        outerCacheReference.close();
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
        return coreCacheReference.size();
    }

    public V get(K k) {
        Hold hold = outerCacheReference.get(k);
        return hold.v;
    }

    public void put(K k, V v) {
        outerCacheReference.invalidate(k, RemovalCause.REPLACED);
        heartCacheReference.invalidate(k, RemovalCause.REPLACED);
        coreCacheReference.put(k, v);
    }

    public void invalidate(K k) {
        outerCacheReference.invalidate(k, RemovalCause.EXPLICIT);
        heartCacheReference.invalidate(k, RemovalCause.EXPLICIT);
        coreCacheReference.invalidate(k, RemovalCause.EXPLICIT);
    }

    public void invalidateAll() {
        invalidateAll(RemovalCause.EXPLICIT);
    }

    public void invalidateAll(RemovalCause cause) {
        outerCacheReference.invalidateAll(cause);
        heartCacheReference.invalidateAll(cause);
        coreCacheReference.invalidateAll(cause);
    }

    /** 强制刷新，定时清理过期数据可能出现延迟，所以也可以手动调用清理 */
    public void refresh() {
        outerCacheReference.cleanup();
        heartCacheReference.cleanup();
        coreCacheReference.cleanup();
    }

}
