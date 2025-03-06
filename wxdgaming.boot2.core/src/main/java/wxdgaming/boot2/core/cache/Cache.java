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
import wxdgaming.boot2.core.collection.concurrent.ConcurrentTable;
import wxdgaming.boot2.core.function.Consumer2;
import wxdgaming.boot2.core.function.Function1;
import wxdgaming.boot2.core.function.Function2;
import wxdgaming.boot2.core.shutdown;
import wxdgaming.boot2.core.threading.Event;
import wxdgaming.boot2.core.threading.ExecutorUtil;
import wxdgaming.boot2.core.threading.TimerJob;
import wxdgaming.boot2.core.timer.MyClock;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    private final ConcurrentTable<Integer, K, CacheHolder> hkv = new ConcurrentTable<>();
    private final String cacheName;
    /** hash桶,通过hash分区 */
    private final int hashArea;
    /** 缓存容器check间隔时间 */
    private final long delay;
    /** 读取过期时间 */
    private final long expireAfterAccess;
    /** 写入过期时间 */
    private final long expireAfterWrite;
    /** 加载 */
    private Function1<K, V> loader;
    /** 移除监听, 如果返回 false 者不会删除 */
    private Function2<K, V, Boolean> removalListener;
    /** 心跳间隔时间 */
    private final long heartTime;
    /** 心跳监听 */
    private Consumer2<K, V> heartListener;
    private Map<Integer, TimerJob> timerJobs = Map.of();

    private CacheHolder buildValue(K k, V v) {
        CacheHolder tuple = new CacheHolder(k, v);
        refresh(tuple);
        return tuple;
    }

    private void refresh(CacheHolder holder) {
        long now = MyClock.millis();
        if (this.expireAfterWrite > 0L) {
            /*表示当前是写入过期*/
            if (holder.expireEndTime == 0) {
                holder.expireEndTime = (now + this.expireAfterWrite);
            }
        } else {
            /*表示读取过期*/
            holder.expireEndTime = (now + this.expireAfterAccess);
        }
    }

    private int hashKey(K k) {
        int i = k.hashCode();
        int h = 0;
        if (hashArea > 1) {
            h = i % hashArea;
        }
        /*不需要负数*/
        return Math.abs(h);
    }

    /** 是否包含kay */
    public boolean containsKey(K k) {
        return hkv.containsKey(hashKey(k), k);
    }

    /** 如果获取缓存没有，可以根据加载,失败回抛出异常 */
    public V get(K k) {
        return get(k, loader);
    }

    /** 如果获取缓存没有，可以根据加载,失败回抛出异常 */
    public V get(K k, Function1<K, V> load) {
        V ifPresent = getIfPresent(k, load);
        if (ifPresent == null) {
            throw new NullPointerException(String.valueOf(k) + " cache null");
        }
        return ifPresent;
    }

    /** 获取数据，如果没有数据返回null */
    public V getIfPresent(K k) {
        return getIfPresent(k, loader);
    }

    /** 获取数据，如果没有数据返回null */
    public V getIfPresent(K k, Function1<K, V> load) {
        int hk = hashKey(k);
        CacheHolder holder = this.hkv.get(hk).computeIfAbsent(k, l -> {
            V apply = null;
            if (load != null) {
                apply = load.apply(l);
            }
            if (apply == null) return null;
            return buildValue(k, apply);
        });
        if (holder != null) {
            if (this.expireAfterAccess > 0L) {
                refresh(holder);
            }
            return holder.value;
        } else {
            return null;
        }
    }

    /** 添加缓存 */
    public void put(K k, V v) {
        int hk = hashKey(k);
        this.hkv.put(hk, k, buildValue(k, v));
    }

    /** 添加缓存 */
    public void putIfAbsent(K k, V v) {
        int hk = hashKey(k);
        this.hkv.putIfAbsent(hk, k, buildValue(k, v));
    }

    /** 过期 */
    public V invalidate(K k) {
        int hk = hashKey(k);
        return Optional.ofNullable(this.hkv.remove(hk, k)).map(holder -> holder.value).orElse(null);
    }

    /** 过期 */
    public void invalidateAll() {
        this.hkv.clear();
    }

    public long cacheSize() {
        return hkv.size();
    }

    public Cache(String cacheName, int hashArea, long delay, long expireAfterAccess, long expireAfterWrite, long heartTime) {
        this.cacheName = cacheName;
        this.hashArea = hashArea;
        this.delay = delay;
        this.expireAfterAccess = expireAfterAccess;
        this.expireAfterWrite = expireAfterWrite;
        this.heartTime = heartTime;
    }

    @shutdown
    public void shutdown() {

        timerJobs.values().forEach(TimerJob::cancel);

        for (Map.Entry<Integer, ConcurrentHashMap<K, CacheHolder>> next : hkv.entrySet()) {
            ConcurrentHashMap<K, CacheHolder> nextValue = next.getValue();
            for (Map.Entry<K, CacheHolder> entryNext : nextValue.entrySet()) {
                K key = entryNext.getKey();
                CacheHolder holder = entryNext.getValue();
                if (removalListener != null) {
                    Boolean apply = removalListener.apply(key, holder.value);
                    if (!Boolean.TRUE.equals(apply)) {
                        log.debug("缓存 shutdown：{} 移除 {}", key, apply);
                    }
                }
            }
        }
    }

    private void init() {
        HashMap<Integer, TimerJob> tmpJobMap = new HashMap<>();
        for (int i = 0; i < hashArea; i++) {
            final int hkey = i;
            final Map<K, CacheHolder> hash = hkv.row(hkey);
            Event event = new Event(cacheName, 10_000, 100_000) {
                @Override public void onEvent() throws Exception {
                    long now = MyClock.millis();
                    Iterator<Map.Entry<K, CacheHolder>> entryIterator = hash.entrySet().iterator();
                    while (entryIterator.hasNext()) {
                        Map.Entry<K, CacheHolder> entryNext = entryIterator.next();
                        K key = entryNext.getKey();
                        CacheHolder holder = entryNext.getValue();

                        if (heartTime > 0 && heartListener != null && holder.lastHeartTime < now) {
                            heartListener.accept(key, holder.value);
                            holder.lastHeartTime = now + heartTime;
                        }

                        if (holder.expireEndTime > 0 && holder.expireEndTime < now) {
                            if (removalListener != null) {
                                Boolean apply = removalListener.apply(key, holder.value);
                                if (Boolean.TRUE.equals(apply)) {
                                    entryIterator.remove();
                                } else {
                                    refresh(holder);
                                    log.info("缓存过期：{} 移除失败", key);
                                }
                            } else {
                                entryIterator.remove();
                                log.info("缓存过期：{}", key);
                            }
                        }
                    }
                }
            };

            TimerJob timerJob = ExecutorUtil.getInstance().getDefaultExecutor().scheduleAtFixedDelay(
                    event,
                    10_000,
                    delay,
                    TimeUnit.MILLISECONDS
            );
            tmpJobMap.put(hkey, timerJob);
        }
        timerJobs = Map.copyOf(tmpJobMap);
    }

    private class CacheHolder implements Comparable<CacheHolder> {

        private final K key;
        private final V value;
        /** 最后执行心跳的时间 */
        private long lastHeartTime;
        /** 过期时间 */
        private long expireEndTime;

        public CacheHolder(K key, V value) {
            this.key = key;
            this.value = value;
            this.lastHeartTime = MyClock.millis() + Cache.this.heartTime;
        }

        public int compareTo(CacheHolder o) {
            if (this.expireEndTime != o.expireEndTime)
                return Long.compare(this.expireEndTime, o.expireEndTime);
            return Integer.compare(this.hashCode(), o.hashCode());
        }

        @Override public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;

            CacheHolder that = (CacheHolder) o;
            return Objects.equals(key, that.key);
        }

        @Override public int hashCode() {
            return Objects.hashCode(key);
        }

        @Override public String toString() {
            return "CacheHolder{" + "k=" + key + '}';
        }
    }

    public static <K, V> CacheBuilder<K, V> builder() {
        return new CacheBuilder<>();
    }

    public static class CacheBuilder<K, V> {
        private String cacheName;
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
            Cache<K, V> kvCache = new Cache<K, V>(cacheName, hashArea, delay, expireAfterAccess, expireAfterWrite, heartTime)
                    .setLoader(loader)
                    .setHeartListener(heartListener)
                    .setRemovalListener(removalListener);
            kvCache.init();
            return kvCache;
        }

    }

    @Override public String toString() {
        return "Cache{cacheName='%s'}".formatted(cacheName);
    }

}
