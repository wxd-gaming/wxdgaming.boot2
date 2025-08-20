package wxdgaming.boot2.core.cache2;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.executor.ExecutorEvent;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.format.data.Data2Size;
import wxdgaming.boot2.core.timer.MyClock;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * lru 类型的缓存
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-03-20 16:14
 **/
@Slf4j
@Getter
@SuperBuilder
public class LRUCache<K, V> extends Cache<K, V> {

    List<CacheLock> reentrantLocks;
    List<HashMap<K, CacheHolder<V>>> nodes;

    /** 计算内存大小 注意特别耗时，并且可能死循环 */
    @Deprecated
    public long memorySize() {
        long size = 0;
        for (HashMap<K, CacheHolder<V>> node : nodes) {
            size += Data2Size.totalSize0(node);
        }
        return size;
    }

    CacheHolder<V> newCacheHolder(V value) {
        CacheHolder<V> cacheHolder = new CacheHolder<>(value);
        cacheHolder.setLastHeartTime(MyClock.millis() + heartTimeMs);
        if (value == null) {
            /*TODO 防止缓存穿透设置过期时间3秒钟*/
            cacheHolder.setExpireEndTime(MyClock.millis() + nullValueTimeMs);
        } else {
            if (this.expireAfterWriteMs > 0) {
                cacheHolder.setExpireEndTime(MyClock.millis() + expireAfterWriteMs);
            }
            refresh(cacheHolder);
        }
        return cacheHolder;
    }

    void refresh(CacheHolder<V> cacheHolder) {
        if (cacheHolder.getValue() != null && this.expireAfterReadMs > 0) {
            /*TODO 防止缓存穿透 holder.getValue() 可能为 null*/
            cacheHolder.setExpireEndTime(MyClock.millis() + expireAfterReadMs);
        }
    }

    public void start() {
        super.start();
        initNodes();
        this.timerJobs = new ScheduledFuture<?>[this.area];

        for (int i = 0; i < this.area; i++) {
            final int hashIndex = i;
            Runnable heartEvent = new ExecutorEvent() {

                @Override public String queueName() {
                    return "cache-heart-event";
                }

                @Override public void onEvent() throws Exception {
                    CacheLock cacheLock = reentrantLocks.get(hashIndex);
                    cacheLock.writeLock.lock();
                    try {
                        Iterator<Map.Entry<K, CacheHolder<V>>> iterator = nodes.get(hashIndex).entrySet().iterator();
                        long millis = MyClock.millis();
                        while (iterator.hasNext()) {
                            Map.Entry<K, CacheHolder<V>> next = iterator.next();
                            CacheHolder<V> holder = next.getValue();
                            try {
                                if (millis > holder.getExpireEndTime()) {
                                    boolean remove = onRemove(next.getKey(), holder);
                                    if (remove)
                                        iterator.remove();
                                    else
                                        refresh(holder);/*移除缓存失败刷新一次*/
                                } else {
                                    if (holder.getValue() != null && LRUCache.this.heartListener != null && millis > holder.getLastHeartTime()) {
                                        /*TODO 防止缓存穿透 holder.getValue() 可能为 null*/
                                        LRUCache.this.heartListener.accept(next.getKey(), holder.getValue());
                                    }
                                }
                            } catch (Exception e) {
                                log.error("CASCache 心跳异常 {}", holder.getValue(), e);
                            }
                        }
                    } finally {
                        cacheLock.writeLock.unlock();
                    }
                }
            };
            ScheduledFuture<?> timerJob = ExecutorFactory.getExecutorServiceBasic().scheduleAtFixedRate(
                    heartEvent,
                    this.heartTimeMs,
                    this.heartTimeMs,
                    TimeUnit.MILLISECONDS
            );
            this.timerJobs[i] = timerJob;
        }
    }

    private void initNodes() {
        List<CacheLock> tmpLock = new ArrayList<>(this.area);
        List<HashMap<K, CacheHolder<V>>> tmpNodes = new ArrayList<>(this.area);
        for (int i = 0; i < this.area; i++) {
            tmpLock.add(new CacheLock());
            tmpNodes.add(new HashMap<>());
        }

        this.reentrantLocks = Collections.unmodifiableList(tmpLock);
        this.nodes = Collections.unmodifiableList(tmpNodes);
    }

    @Override public void stop() {
        for (ScheduledFuture<?> timerJob : timerJobs) {
            timerJob.cancel(true);
        }
        invalidateAll();
    }

    @Override public boolean has(K k) {
        int hashIndex = hashIndex(k);
        CacheLock cacheLock = reentrantLocks.get(hashIndex);
        cacheLock.readLock.lock();
        try {
            return nodes.get(hashIndex).containsKey(k);
        } finally {
            cacheLock.readLock.unlock();
        }
    }

    @Override public V get(K k) throws NullPointerException {
        V ifPresent = getIfPresent(k);
        if (ifPresent == null) {
            throw new NullPointerException(String.format("cache key=%s value is null", k));
        }
        return ifPresent;
    }

    @Override public V getIfPresent(K k) {
        int hashIndex = hashIndex(k);
        CacheHolder<V> cacheHolder = null;
        CacheLock cacheLock = reentrantLocks.get(hashIndex);
        cacheLock.readLock.lock();
        try {
            cacheHolder = nodes.get(hashIndex).get(k);
        } finally {
            cacheLock.readLock.unlock();
        }
        if (cacheHolder == null) {
            /*双重锁确保正确命中*/
            if (LRUCache.this.loader == null)
                return null;
            cacheLock.writeLock.lock();
            try {
                cacheHolder = nodes.get(hashIndex).get(k);
                if (cacheHolder == null) {
                    V apply = LRUCache.this.loader.apply(k);
                    /*TODO 即便是数据库 null 也要缓存, 防止缓存穿透*/
                    cacheHolder = newCacheHolder(apply);
                    nodes.get(hashIndex).put(k, cacheHolder);
                }
            } finally {
                cacheLock.writeLock.unlock();
            }
        }
        if (cacheHolder == null) return null;
        refresh(cacheHolder);
        return cacheHolder.getValue();
    }

    @Override public V put(K k, V v) {
        int hashIndex = hashIndex(k);
        CacheLock cacheLock = reentrantLocks.get(hashIndex);
        cacheLock.writeLock.lock();
        try {
            CacheHolder<V> cacheHolder = newCacheHolder(v);
            CacheHolder<V> old = nodes.get(hashIndex).put(k, cacheHolder);
            return old == null ? null : old.getValue();
        } finally {
            cacheLock.writeLock.unlock();
        }
    }

    @Override public V putIfAbsent(K k, V v) {
        int hashIndex = hashIndex(k);
        CacheLock cacheLock = reentrantLocks.get(hashIndex);
        cacheLock.writeLock.lock();
        try {
            CacheHolder<V> cacheHolder = newCacheHolder(v);
            CacheHolder<V> old = nodes.get(hashIndex).putIfAbsent(k, cacheHolder);
            if (old != null && old.getValue() == null) {
                nodes.get(hashIndex).put(k, cacheHolder);
            }
            return old == null ? null : old.getValue();
        } finally {
            cacheLock.writeLock.unlock();
        }
    }

    @Override public V invalidate(K k) {
        int hashIndex = hashIndex(k);
        CacheLock cacheLock = reentrantLocks.get(hashIndex);
        cacheLock.writeLock.lock();
        try {
            CacheHolder<V> cacheHolder = nodes.get(hashIndex).remove(k);
            if (cacheHolder == null) {
                return null;
            }
            cacheHolder.setExpireEndTime(0);
            onRemove(k, cacheHolder);
            return cacheHolder.getValue();
        } finally {
            cacheLock.writeLock.unlock();
        }
    }

    @Override public void invalidateAll() {
        for (int i = 0; i < nodes.size(); i++) {
            CacheLock cacheLock = reentrantLocks.get(i);
            cacheLock.writeLock.lock();
            try {
                HashMap<K, CacheHolder<V>> node = nodes.get(i);
                for (Map.Entry<K, CacheHolder<V>> entry : node.entrySet()) {
                    K key = entry.getKey();
                    CacheHolder<V> holder = entry.getValue();
                    holder.setExpireEndTime(0);
                    onRemove(key, holder);
                }
                node.clear();
            } finally {
                cacheLock.writeLock.unlock();
            }
        }
    }

    boolean onRemove(K key, CacheHolder<V> cacheHolder) {
        if (LRUCache.this.removalListener != null && cacheHolder.getValue() != null) {
            try {
                /*TODO 防止缓存穿透 holder.getValue() 可能为 null*/
                return LRUCache.this.removalListener.apply(key, cacheHolder.getValue());
            } catch (Exception e) {
                log.error("removalListener 执行异常 {}", cacheHolder.getValue(), e);
            }
        }
        return true;
    }

    @Override public Collection<K> keys() {
        List<K> result = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            CacheLock cacheLock = reentrantLocks.get(i);
            cacheLock.readLock.lock();
            try {
                HashMap<K, CacheHolder<V>> node = nodes.get(i);
                List<K> tmp = new ArrayList<>(node.size());
                for (Map.Entry<K, CacheHolder<V>> holderLongEntry : node.entrySet()) {
                    if (holderLongEntry.getValue() == null) {
                        continue;
                    }
                    tmp.add(holderLongEntry.getKey());
                }
                result.addAll(tmp);
            } finally {
                cacheLock.readLock.unlock();
            }
        }
        return result;
    }

    /** 拷贝所有元素 */
    @Override public Collection<V> values() {
        List<V> result = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            CacheLock cacheLock = reentrantLocks.get(i);
            cacheLock.readLock.lock();
            try {
                HashMap<K, CacheHolder<V>> node = nodes.get(i);
                List<V> tmp = new ArrayList<>(node.size());
                for (CacheHolder<V> holder : node.values()) {
                    if (holder == null) {
                        continue;
                    }
                    tmp.add(holder.getValue());
                }
                result.addAll(tmp);
            } finally {
                cacheLock.readLock.unlock();
            }
        }
        return result;
    }

    @Override public long size() {
        long size = 0;
        for (int i = 0; i < nodes.size(); i++) {
            CacheLock cacheLock = reentrantLocks.get(i);
            cacheLock.readLock.lock();
            try {
                HashMap<K, CacheHolder<V>> node = nodes.get(i);
                size += node.size();
            } finally {
                cacheLock.readLock.unlock();
            }
        }
        return size;
    }

    @Deprecated
    @Override public void discard(K k) {
        int hashIndex = hashIndex(k);
        CacheLock cacheLock = reentrantLocks.get(hashIndex);
        cacheLock.writeLock.lock();
        try {
            nodes.get(hashIndex).remove(k);
        } finally {
            cacheLock.writeLock.unlock();
        }
    }

    @Deprecated
    @Override public void discardAll() {
        initNodes();
    }


}
