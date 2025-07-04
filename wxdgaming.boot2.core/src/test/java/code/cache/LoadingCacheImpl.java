package code.cache;

import lombok.Builder;
import wxdgaming.boot2.core.function.Consumer3;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * 加载缓存
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-07-03 10:57
 **/
@Builder
public class LoadingCacheImpl<K, V> {


    private final AtomicReference<CacheDriver<K, V>> innerCacheReference = new AtomicReference<>();

    private final AtomicReference<CacheDriver<K, Hold>> outerCacheReference = new AtomicReference<>();
    /** 心跳时间 */
    private Duration expireHeartAfterWrite;
    /** 读取过期时间 */
    private Duration expireAfterAccess;
    /** 写入过期时间 */
    private Duration expireAfterWrite;
    private Function<K, V> loader;
    private Consumer3<K, V, CacheDriver.RemovalCause> heartListener;
    private Consumer3<K, V, CacheDriver.RemovalCause> removalListener;

    public LoadingCacheImpl<K, V> start() {

        innerCacheReference.set(
                CacheDriver.<K, V>builder()
                        .loader(loader)
                        .removalListener(removalListener)
                        .expireAfterAccess(expireAfterAccess)
                        .expireAfterWrite(expireAfterWrite)
                        .build()
                        .start()
        );

        outerCacheReference.set(
                CacheDriver.<K, Hold>builder()
                        .loader(key -> new Hold(innerCacheReference.get().get(key)))
                        .removalListener((k, hold, removalCause) -> {
                            if (removalCause != CacheDriver.RemovalCause.EXPIRE) return;
                            if (heartListener != null)
                                heartListener.accept(k, hold.v, removalCause);
                        })
                        .expireAfterWrite(expireHeartAfterWrite)
                        .build()
                        .start()
        );

        return this;
    }

    private class Hold {
        final V v;

        public Hold(V v) {
            this.v = v;
        }
    }

    public V get(K k) {
        return outerCacheReference.get().get(k).v;
    }

    public void put(K k, V v) {
        outerCacheReference.get().remove(k, CacheDriver.RemovalCause.SPECIAL);
        innerCacheReference.get().put(k, v);
    }

    public void remove(K k) {
        outerCacheReference.get().remove(k, CacheDriver.RemovalCause.SPECIAL);
        innerCacheReference.get().remove(k, CacheDriver.RemovalCause.EXPLICIT);
    }

    /** 强制刷新，定时清理过期数据可能出现延迟，所以也可以手动调用清理 */
    public void refresh() {
        outerCacheReference.get().refresh();
        innerCacheReference.get().refresh();
    }

}
