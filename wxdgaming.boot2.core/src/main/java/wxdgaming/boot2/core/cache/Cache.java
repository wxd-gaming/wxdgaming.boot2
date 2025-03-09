//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package wxdgaming.boot2.core.cache;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.function.Consumer2;
import wxdgaming.boot2.core.function.Function1;
import wxdgaming.boot2.core.function.Function2;
import wxdgaming.boot2.core.shutdown;

import java.util.concurrent.TimeUnit;

/**
 * 缓存
 *
 * @param <K>
 * @param <V>
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-05-10 20:19
 */
@Slf4j
@Getter
@Setter(value = AccessLevel.PROTECTED)
@Accessors(chain = true)
public final class Cache<K, V> {

    /** key: hash分区, value: {key: 缓存键, value: 缓存对象} */
    private ICacheArea<K, V>[] cacheAreaMap;
    final String cacheName;

    private int hashKey(K k) {
        int i = k.hashCode();
        int h = 0;
        if (cacheAreaMap.length > 1) {
            h = i % cacheAreaMap.length;
        }
        /*不需要负数*/
        return Math.abs(h);
    }

    /** 是否包含kay */
    public boolean containsKey(K k) {
        return cacheAreaMap[hashKey(k)].containsKey(k);
    }

    /** 如果获取缓存没有，可以根据加载,失败回抛出异常 */
    public V get(K k) {
        return cacheAreaMap[hashKey(k)].get(k);
    }

    /** 如果获取缓存没有，可以根据加载,失败回抛出异常 */
    public V get(K k, Function1<K, V> load) {
        V ifPresent = cacheAreaMap[hashKey(k)].getIfPresent(k, load);
        if (ifPresent == null) {
            throw new NullPointerException(String.valueOf(k) + " cache null");
        }
        return ifPresent;
    }

    /** 获取数据，如果没有数据返回null */
    public V getIfPresent(K k) {
        return cacheAreaMap[hashKey(k)].getIfPresent(k);
    }

    /** 获取数据，如果没有数据返回null */
    public V getIfPresent(K k, Function1<K, V> load) {
        return cacheAreaMap[hashKey(k)].getIfPresent(k, load);
    }

    /** 添加缓存 */
    public void put(K k, V v) {
        cacheAreaMap[hashKey(k)].put(k, v);
    }

    /** 添加缓存 */
    public void putIfAbsent(K k, V v) {
        cacheAreaMap[hashKey(k)].putIfAbsent(k, v);
    }

    /** 过期 */
    public V invalidate(K k) {
        return cacheAreaMap[hashKey(k)].invalidate(k);
    }

    /** 过期 */
    public void invalidateAll() {
        for (int i = 0; i < cacheAreaMap.length; i++) {
            ICacheArea<K, V> kviCacheArea = cacheAreaMap[i];
            kviCacheArea.invalidateAll();
        }
    }

    public long cacheSize() {
        long size = 0;
        for (int i = 0; i < cacheAreaMap.length; i++) {
            ICacheArea<K, V> kviCacheArea = cacheAreaMap[i];
            size += kviCacheArea.getSize();
        }
        return size;
    }

    @shutdown
    public void shutdown() {
        for (int i = 0; i < cacheAreaMap.length; i++) {
            ICacheArea<K, V> kviCacheArea = cacheAreaMap[i];
            kviCacheArea.shutdown();
        }
    }

    private Cache(String cacheName) {
        this.cacheName = cacheName;
    }

    public static <K, V> CacheBuilder<K, V> builder() {
        return new CacheBuilder<>();
    }

    public enum CacheType {
        /**
         * 基于线程安全的hashmap内部cas
         * <p>确实存在缓存过期零界点同时被访问；卸载和加载同时进行；如果不考虑零界点性能高于LRU、
         */
        CAS,
        /**
         * 基于读写锁
         * <p>线程更安全，但是性能比CAS低,
         */
        LRU;
    }

    public static class CacheBuilder<K, V> {
        private String cacheName;
        private CacheType cacheType = CacheType.CAS;
        /** 缓存容器check间隔时间 */
        private long delay = 100;
        /** hash桶,通过hash分区 */
        private int hashArea = 0;
        private Function1<K, V> loader;
        private Function2<K, V, Boolean> removalListener;
        private long expireAfterAccess;
        private long expireAfterWrite;
        private long heartTime;
        private Consumer2<K, V> heartListener;

        CacheBuilder() {
        }

        public CacheBuilder<K, V> cacheType(CacheType cacheType) {
            this.cacheType = cacheType;
            return this;
        }

        /** 缓存容器名字 */
        public CacheBuilder<K, V> cacheName(String cacheName) {
            this.cacheName = cacheName;
            return this;
        }

        /** 缓存容器check间隔时间 */
        public CacheBuilder<K, V> delay(long delay) {
            this.delay = delay;
            return this;
        }

        /** 缓存容器check间隔时间 */
        public CacheBuilder<K, V> delay(long duration, TimeUnit timeUnit) {
            return delay(timeUnit.toMillis(duration));
        }

        /** hash桶,通过hash分区 */
        public CacheBuilder<K, V> hashArea(int hashArea) {
            this.hashArea = hashArea;
            return this;
        }

        /** 加载 */
        public CacheBuilder<K, V> loader(Function1<K, V> loader) {
            this.loader = loader;
            return this;
        }

        /** 移除监听 */
        public CacheBuilder<K, V> removalListener(Function2<K, V, Boolean> removalListener) {
            this.removalListener = removalListener;
            return this;
        }

        /** 读取过期时间 */
        public CacheBuilder<K, V> expireAfterAccess(long duration, TimeUnit timeUnit) {
            return this.expireAfterAccess(timeUnit.toMillis(duration));
        }

        /** 读取过期时间 */
        public CacheBuilder<K, V> expireAfterAccess(long expireAfterAccess) {
            this.expireAfterAccess = expireAfterAccess;
            if (this.expireAfterWrite > 0) {
                throw new RuntimeException("写入过期和读取过期不允许同时设置");
            }
            return this;
        }

        /** 写入过期时间 */
        public CacheBuilder<K, V> expireAfterWrite(long duration, TimeUnit timeUnit) {
            return this.expireAfterWrite(timeUnit.toMillis(duration));
        }

        /** 写入过期时间 */
        public CacheBuilder<K, V> expireAfterWrite(long expireAfterWrite) {
            this.expireAfterWrite = expireAfterWrite;
            if (this.expireAfterAccess > 0) {
                throw new RuntimeException("写入过期和读取过期不允许同时设置");
            }
            return this;
        }

        /** 心跳间隔时间 */
        public CacheBuilder<K, V> heartTime(long heartTime) {
            this.heartTime = heartTime;
            return this;
        }

        /** 心跳间隔时间 */
        public CacheBuilder<K, V> heartTime(long duration, TimeUnit timeUnit) {
            this.heartTime = timeUnit.toMillis(duration);
            return this;
        }

        /** 心跳监听 */
        public CacheBuilder<K, V> heartListener(Consumer2<K, V> heartListener) {
            this.heartListener = heartListener;
            return this;
        }

        public Cache<K, V> build() {
            Cache<K, V> kvCache = new Cache<K, V>(cacheName);
            if (hashArea < 1) hashArea = 1;
            kvCache.cacheAreaMap = new ICacheArea[hashArea];
            for (int i = 0; i < hashArea; i++) {
                ICacheArea<K, V> cacheArea;
                if (cacheType == CacheType.CAS) {
                    cacheArea = new CASCacheArea<>(
                            cacheName + "-" + i,
                            delay, expireAfterAccess, expireAfterWrite,
                            loader, removalListener,
                            heartTime, heartListener
                    );
                } else {
                    cacheArea = new LRUCacheArea<>(
                            cacheName + "-" + i,
                            delay, expireAfterAccess, expireAfterWrite,
                            loader, removalListener,
                            heartTime, heartListener
                    );
                }
                kvCache.cacheAreaMap[i] = cacheArea;
            }
            return kvCache;
        }

    }

    @Override public String toString() {
        return "Cache{cacheName='%s'}".formatted(cacheName);
    }

}
