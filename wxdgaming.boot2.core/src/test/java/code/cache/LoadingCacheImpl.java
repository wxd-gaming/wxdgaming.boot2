package code.cache;

import lombok.Builder;
import lombok.Getter;
import wxdgaming.boot2.core.function.Consumer3;
import wxdgaming.boot2.core.function.Predicate3;
import wxdgaming.boot2.core.util.AssertUtil;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * 加载缓存
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-07-03 10:57
 **/
@Getter
public class LoadingCacheImpl<K, V> {

    static final Duration minHeartDuration = Duration.ofSeconds(5);
    static final Duration outerDuration = Duration.ofSeconds(2);
    static final Duration heartDurationDefault = Duration.ofSeconds(5);


    private final AtomicReference<CacheDriver<K, V>> coreCacheReference = new AtomicReference<>();
    private final AtomicReference<CacheDriver<K, V>> heartCacheReference = new AtomicReference<>();
    private final AtomicReference<CacheDriver<K, Hold>> outerCacheReference = new AtomicReference<>();

    private final String cacheName;
    private final int blockSize;
    /** 心跳时间 */
    private final Duration heartExpireAfterWrite;
    /** 读取过期时间 */
    private final Duration expireAfterAccess;
    /** 写入过期时间 */
    private final Duration expireAfterWrite;
    private final Function<K, V> loader;
    private final Consumer3<K, V, CacheDriver.RemovalCause> heartListener;
    private final Predicate3<K, V, CacheDriver.RemovalCause> removalListener;

    @Builder
    public LoadingCacheImpl(String cacheName, int blockSize, Duration heartExpireAfterWrite, Duration expireAfterAccess, Duration expireAfterWrite, Function<K, V> loader, Consumer3<K, V, CacheDriver.RemovalCause> heartListener, Predicate3<K, V, CacheDriver.RemovalCause> removalListener) {
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

        coreCacheReference.set(
                CacheDriver.<K, V>builder()
                        .loader(loader)
                        .blockSize(blockSize)
                        .removalListener(removalListener)
                        .expireAfterAccess(expireAfterAccess)
                        .expireAfterWrite(expireAfterWrite)
                        .build()
        );

        heartCacheReference.set(
                CacheDriver.<K, V>builder()
                        .blockSize(blockSize)
                        .loader(key -> coreCacheReference.get().get(key))
                        .expireAfterWrite(this.heartExpireAfterWrite)
                        .removalListener((k, v, removalCause) -> {
                            if (removalCause != CacheDriver.RemovalCause.EXPIRE) return true;
                            if (heartListener != null)
                                heartListener.accept(k, v, removalCause);
                            return true;
                        })
                        .build()
        );

        outerCacheReference.set(
                CacheDriver.<K, Hold>builder()
                        .blockSize(blockSize)
                        .loader(key -> new Hold(heartCacheReference.get().get(key)))
                        .expireAfterWrite(outerDuration)
                        .build()
        );

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
        return coreCacheReference.get().size();
    }

    public V get(K k) {
        Hold hold = outerCacheReference.get().get(k);
        return hold.v;
    }

    public void put(K k, V v) {
        outerCacheReference.get().invalidate(k, CacheDriver.RemovalCause.EXPLICIT);
        heartCacheReference.get().invalidate(k, CacheDriver.RemovalCause.EXPLICIT);
        coreCacheReference.get().put(k, v);
    }

    public void invalidate(K k) {
        outerCacheReference.get().invalidate(k, CacheDriver.RemovalCause.EXPLICIT);
        heartCacheReference.get().invalidate(k, CacheDriver.RemovalCause.EXPLICIT);
        coreCacheReference.get().invalidate(k, CacheDriver.RemovalCause.EXPLICIT);
    }

    public void invalidateAll() {
        outerCacheReference.get().invalidateAll();
        heartCacheReference.get().invalidateAll();
        coreCacheReference.get().invalidateAll();
    }

    /** 强制刷新，定时清理过期数据可能出现延迟，所以也可以手动调用清理 */
    public void refresh() {
        outerCacheReference.get().cleanup();
        heartCacheReference.get().cleanup();
        coreCacheReference.get().cleanup();
    }

}
