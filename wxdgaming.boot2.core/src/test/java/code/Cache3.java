//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package code;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.function.Consumer2;
import wxdgaming.boot2.core.function.Function1;
import wxdgaming.boot2.core.function.Function2;
import wxdgaming.boot2.core.lang.DiffTime;
import wxdgaming.boot2.core.shutdown;
import wxdgaming.boot2.core.threading.Event;
import wxdgaming.boot2.core.threading.ExecutorUtil;
import wxdgaming.boot2.core.threading.TimerJob;
import wxdgaming.boot2.core.timer.MyClock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
public final class Cache3<K, V> {

    /** key: hash分区, value: {key: 缓存键, value: 缓存对象} */
    private final HashMap<Integer, CacheHolderManager> hkv = new HashMap<>();
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
        int hashKey = hashKey(k);
        return hkv.get(hashKey).containsKey(k);
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
        return this.hkv.get(hk).getIfPresent(k, load);
    }

    /** 添加缓存 */
    public void put(K k, V v) {
        int hk = hashKey(k);
        this.hkv.get(hk).put(k, v);
    }

    /** 添加缓存 */
    public void putIfAbsent(K k, V v) {
        int hk = hashKey(k);
        this.hkv.get(hk).putIfAbsent(k, v);
    }

    /** 过期 */
    public V invalidate(K k) {
        int hk = hashKey(k);
        return this.hkv.get(hk).remove(k);
    }

    /** 过期 */
    public void invalidateAll() {
        this.hkv.clear();
    }

    public long cacheSize() {
        return hkv.values().stream().mapToLong(v -> v.nodes.size()).sum();
    }

    public Cache3(String cacheName, int hashArea, long delay, long expireAfterAccess, long expireAfterWrite, long heartTime) {
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

        if (removalListener != null) {
            for (Map.Entry<Integer, CacheHolderManager> next : hkv.entrySet()) {
                CacheHolderManager holderManager = next.getValue();
                holderManager.writeLock.lock();
                try {
                    for (CacheHolder holder : holderManager.nodes.values()) {
                        Boolean apply = removalListener.apply(holder.key, holder.value);
                        if (!Boolean.TRUE.equals(apply)) {
                            log.debug("缓存 shutdown：{} 移除 {}", holder.key, apply);
                        }
                    }
                } finally {
                    holderManager.writeLock.unlock();
                }
            }
        }
    }

    private void init() {
        HashMap<Integer, TimerJob> tmpJobMap = new HashMap<>();
        for (int i = 0; i < hashArea; i++) {
            final int hkey = i;
            CacheHolderManager holderManager = new CacheHolderManager();
            hkv.put(hkey, holderManager);

            final DiffTime diffTime = new DiffTime();

            Event event = new Event(cacheName, 10_000, 100_000) {
                @Override public void onEvent() throws Exception {
                    long now = MyClock.millis();
                    // if (!holderManager.writeLock.tryLock()) return;
                    diffTime.reset();
                    holderManager.writeLock.lock();
                    try {
                        if (heartListener != null) {
                            for (CacheHolder holder : holderManager.nodes.values()) {
                                if (holder.lastHeartTime < now) {
                                    heartListener.accept(holder.key, holder.value);
                                    holder.lastHeartTime = now + heartTime;
                                } else {
                                    break;
                                }
                            }
                        }
                        ArrayList<CacheHolder> changes = new ArrayList<>();
                        for (CacheHolder holder : holderManager.nodes.values()) {
                            if (holder.expireEndTime < now) {
                                changes.add(holder);
                            } else {
                                break;
                            }
                        }
                        for (CacheHolder change : changes) {
                            holderManager.remove(change);
                        }
                    } finally {
                        holderManager.writeLock.unlock();
                    }

                    float diff = diffTime.diff();
                    if (diff > 0) {
                        log.info("缓存 {}-{} 刷新 {}", cacheName, hkey, diff);
                    }
                }
            };

            TimerJob timerJob = ExecutorUtil.getInstance().getDefaultExecutor().scheduleAtFixedDelay(
                    event,
                    delay,
                    delay,
                    TimeUnit.MILLISECONDS
            );
            tmpJobMap.put(hkey, timerJob);
        }
        timerJobs = Map.copyOf(tmpJobMap);
    }

    private class CacheHolderManager {

        private final ReentrantReadWriteLock lock;
        private final ReentrantReadWriteLock.WriteLock writeLock;
        private final ReentrantReadWriteLock.ReadLock readLock;
        private HashMap<K, CacheHolder> nodes = new HashMap<>();

        public CacheHolderManager() {
            lock = new ReentrantReadWriteLock();
            writeLock = lock.writeLock();
            readLock = lock.readLock();
        }

        public void clear() {
            writeLock.lock();
            try {
                nodes = new HashMap<>();
            } finally {
                writeLock.unlock();
            }
        }

        public boolean containsKey(K k) {
            readLock.lock();
            try {
                return nodes.containsKey(k);
            } finally {
                readLock.unlock();
            }
        }

        /** 添加缓存 */
        public void put(K k, V v) {
            writeLock.lock();
            try {
                this.nodes.put(k, buildValue(k, v));
            } finally {
                writeLock.unlock();
            }
        }

        /** 添加缓存 */
        public void putIfAbsent(K k, V v) {
            writeLock.lock();
            try {
                this.nodes.putIfAbsent(k, buildValue(k, v));
            } finally {
                writeLock.unlock();
            }
        }

        /** 获取数据，如果没有数据返回null */
        public V getIfPresent(K k, Function1<K, V> load) {
            CacheHolder holder;
            readLock.lock();
            try {
                holder = this.nodes.get(k);
            } finally {
                readLock.unlock();
            }
            if (holder == null) {
                writeLock.lock();
                try {
                    holder = this.nodes.get(k);
                    if (holder == null) {
                        V apply = null;
                        if (load != null) {
                            apply = load.apply(k);
                        }
                        if (apply == null) return null;
                        holder = buildValue(k, apply);
                        this.nodes.putIfAbsent(k, holder);
                    }
                } finally {
                    writeLock.unlock();
                }
            }
            if (Cache3.this.expireAfterAccess > 0L) {
                refresh(holder);
            }
            return holder.value;
        }

        public V remove(K key) {
            CacheHolder holder;
            readLock.lock();
            try {
                holder = this.nodes.get(key);
            } finally {
                readLock.unlock();
            }
            return remove(holder);
        }

        public V remove(CacheHolder holder) {
            writeLock.lock();
            try {
                if (holder != null) {
                    if (Cache3.this.removalListener != null) {
                        Cache3.this.removalListener.apply(holder.key, holder.value);
                    }
                    this.nodes.remove(holder.key);
                    return holder.value;
                }
                return null;
            } finally {
                writeLock.unlock();
            }
        }

        private CacheHolder buildValue(K k, V v) {
            CacheHolder holder = new CacheHolder(k, v);
            refresh(holder);
            return holder;
        }

        private void refresh(CacheHolder holder) {
            writeLock.lock();
            try {
                long now = MyClock.millis();
                if (Cache3.this.expireAfterWrite > 0L) {
                    /*表示当前是写入过期*/
                    if (holder.expireEndTime == 0) {
                        holder.expireEndTime = (now + Cache3.this.expireAfterWrite);
                    }
                } else if (Cache3.this.expireAfterAccess > 0L) {
                    /*表示读取过期*/
                    holder.expireEndTime = (now + Cache3.this.expireAfterAccess);
                }
            } finally {
                writeLock.unlock();
            }
        }
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
            this.lastHeartTime = MyClock.millis() + Cache3.this.heartTime;
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

        public Cache3<K, V> build() {
            Cache3<K, V> kvCache = new Cache3<K, V>(cacheName, hashArea, delay, expireAfterAccess, expireAfterWrite, heartTime)
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
