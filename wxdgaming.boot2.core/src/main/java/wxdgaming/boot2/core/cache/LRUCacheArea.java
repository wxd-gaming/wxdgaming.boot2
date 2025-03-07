package wxdgaming.boot2.core.cache;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.function.Consumer2;
import wxdgaming.boot2.core.function.Function1;
import wxdgaming.boot2.core.function.Function2;
import wxdgaming.boot2.core.threading.Event;
import wxdgaming.boot2.core.threading.ExecutorUtil;
import wxdgaming.boot2.core.threading.TimerJob;
import wxdgaming.boot2.core.timer.MyClock;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 基于map cas实现缓存并非绝对安全
 * 采用读写锁，线程更安全
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-03-07 17:49
 **/
@Slf4j
@Getter
class LRUCacheArea<K, V> implements ICacheArea<K, V> {

    private final String cacheName;
    /** 缓存容器check间隔时间 */
    private final long delay;
    /** 读取过期时间 */
    private final long expireAfterAccess;
    /** 写入过期时间 */
    private final long expireAfterWrite;
    /** 加载 */
    private final Function1<K, V> loader;
    /** 移除监听, 如果返回 false 者不会删除 */
    private final Function2<K, V, Boolean> removalListener;
    /** 心跳间隔时间 */
    private final long heartTime;
    /** 心跳监听 */
    private final Consumer2<K, V> heartListener;
    private final TimerJob timerJob;

    private final ReentrantReadWriteLock lock;
    private final ReentrantReadWriteLock.WriteLock writeLock;
    private final ReentrantReadWriteLock.ReadLock readLock;

    private HashMap<K, CacheHolder<K, V>> nodes = new HashMap<>();

    public LRUCacheArea(String cacheName, long delay,
                        long expireAfterAccess, long expireAfterWrite,
                        Function1<K, V> loader, Function2<K, V, Boolean> removalListener,
                        long heartTime, Consumer2<K, V> heartListener) {
        this.cacheName = cacheName;
        this.delay = delay;
        this.expireAfterAccess = expireAfterAccess;
        this.expireAfterWrite = expireAfterWrite;
        this.loader = loader;
        this.removalListener = removalListener;
        this.heartTime = heartTime;
        this.heartListener = heartListener;

        this.lock = new ReentrantReadWriteLock();
        this.writeLock = lock.writeLock();
        this.readLock = lock.readLock();

        Event event = new Event(this.cacheName, 10_000, 100_000) {
            @Override public void onEvent() throws Exception {
                long now = MyClock.millis();
                writeLock.lock();
                try {
                    Iterator<Map.Entry<K, CacheHolder<K, V>>> entryIterator = nodes.entrySet().iterator();
                    while (entryIterator.hasNext()) {
                        Map.Entry<K, CacheHolder<K, V>> entryNext = entryIterator.next();
                        K key = entryNext.getKey();
                        CacheHolder<K, V> holder = entryNext.getValue();

                        if (heartTime > 0 && heartListener != null && holder.getLastHeartTime() < now) {
                            heartListener.accept(key, holder.getValue());
                            holder.setLastHeartTime(now + heartTime);
                        }

                        if (holder.getExpireEndTime() > 0 && holder.getExpireEndTime() < now) {
                            if (removalListener != null) {
                                Boolean apply = removalListener.apply(key, holder.getValue());
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
                } finally {
                    writeLock.unlock();
                }
            }
        };

        timerJob = ExecutorUtil.getInstance().getDefaultExecutor().scheduleAtFixedDelay(
                event,
                delay,
                delay,
                TimeUnit.MILLISECONDS
        );

    }

    @Override public void refresh(CacheHolder<K, V> holder) {
        writeLock.lock();
        try {
            long now = MyClock.millis();
            if (this.expireAfterWrite > 0L) {
                /*表示当前是写入过期*/
                if (holder.getExpireEndTime() == 0) {
                    holder.setExpireEndTime(now + this.expireAfterWrite);
                }
            } else {
                /*表示读取过期*/
                holder.setExpireEndTime(now + this.expireAfterAccess);
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override public long getSize() {
        readLock.lock();
        try {
            return nodes.size();
        } finally {
            readLock.unlock();
        }
    }

    /** 是否包含kay */
    @Override public boolean containsKey(K k) {
        readLock.lock();
        try {
            return nodes.containsKey(k);
        } finally {
            readLock.unlock();
        }
    }

    /** 如果获取缓存没有，可以根据加载,失败回抛出异常 */
    @Override public V get(K k) {
        return get(k, loader);
    }

    /** 如果获取缓存没有，可以根据加载,失败回抛出异常 */
    @Override public V get(K k, Function1<K, V> load) {
        V ifPresent = getIfPresent(k, load);
        if (ifPresent == null) {
            throw new NullPointerException(String.valueOf(k) + " cache null");
        }
        return ifPresent;
    }

    /** 获取数据，如果没有数据返回null */
    @Override public V getIfPresent(K k) {
        return getIfPresent(k, loader);
    }

    /** 获取数据，如果没有数据返回null */
    @Override public V getIfPresent(K k, Function1<K, V> load) {
        CacheHolder<K, V> holder;
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
                    if (apply != null) {
                        holder = buildValue(k, apply, heartTime);
                        this.nodes.put(k, holder);
                    }
                }
            } finally {
                writeLock.unlock();
            }
        }
        if (holder != null) {
            if (this.expireAfterAccess > 0L) {
                refresh(holder);
            }
            return holder.getValue();
        } else {
            return null;
        }
    }

    /** 添加缓存 */
    @Override public void put(K k, V v) {
        writeLock.lock();
        try {
            this.nodes.put(k, buildValue(k, v, heartTime));
        } finally {
            writeLock.unlock();
        }
    }

    /** 添加缓存 */
    @Override public void putIfAbsent(K k, V v) {
        writeLock.lock();
        try {
            this.nodes.putIfAbsent(k, buildValue(k, v, heartTime));
        } finally {
            writeLock.unlock();
        }
    }

    /** 过期 */
    @Override public V invalidate(K k) {
        writeLock.lock();
        try {
            CacheHolder<K, V> remove = this.nodes.remove(k);
            if (remove == null) return null;
            if (removalListener != null) removalListener.apply(k, remove.getValue());
            return remove.getValue();
        } finally {
            writeLock.unlock();
        }
    }

    /** 过期 */
    @Override public void invalidateAll() {
        writeLock.lock();
        try {
            Map<K, CacheHolder<K, V>> tmp = nodes;
            nodes = new HashMap<>();
            if (removalListener != null) {
                tmp.values().forEach((cacheHolder) -> removalListener.apply(cacheHolder.getKey(), cacheHolder.getValue()));
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override public void shutdown() {
        if (timerJob != null) {
            timerJob.cancel();
        }
        invalidateAll();
    }

}
