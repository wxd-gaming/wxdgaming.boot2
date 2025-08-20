package wxdgaming.boot2.core.cache2;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.executor.ExecutorEvent;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.format.data.Data2Size;
import wxdgaming.boot2.core.timer.MyClock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * lru 类型的缓存
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-03-20 16:14
 **/
@Slf4j
@SuperBuilder
public class LRULongCache<V> extends Cache<Long, V> {

    List<CacheLock> reentrantLocks;
    List<Long2ObjectOpenHashMap<CacheHolder<V>>> nodes;
    ScheduledFuture<?>[] timerJobs;

    /** 计算内存大小 注意特别耗时，并且可能死循环 */
    @Deprecated
    public long memorySize() {
        long size = 0;
        for (Long2ObjectOpenHashMap<CacheHolder<V>> node : nodes) {
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
                        ObjectIterator<Long2ObjectMap.Entry<CacheHolder<V>>> iterator = nodes.get(hashIndex).long2ObjectEntrySet().iterator();
                        long millis = MyClock.millis();
                        while (iterator.hasNext()) {
                            Long2ObjectMap.Entry<CacheHolder<V>> next = iterator.next();
                            CacheHolder<V> cacheHolder = next.getValue();
                            try {
                                if (millis > cacheHolder.getExpireEndTime()) {
                                    boolean remove = onRemove(next.getLongKey(), cacheHolder);
                                    if (remove)
                                        iterator.remove();
                                    else
                                        refresh(cacheHolder);/*移除缓存失败刷新一次*/
                                } else {
                                    if (cacheHolder.getValue() != null && LRULongCache.this.heartListener != null && millis > cacheHolder.getLastHeartTime()) {
                                        /*TODO 防止缓存穿透 holder.getValue() 可能为 null*/
                                        LRULongCache.this.heartListener.accept(next.getLongKey(), cacheHolder.getValue());
                                    }
                                }
                            } catch (Exception e) {
                                log.error("CASCache 心跳异常 {}", cacheHolder.getValue(), e);
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
        List<Long2ObjectOpenHashMap<CacheHolder<V>>> tmpNodes = new ArrayList<>(this.area);
        for (int i = 0; i < this.area; i++) {
            tmpLock.add(new CacheLock());
            tmpNodes.add(new Long2ObjectOpenHashMap<>());
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

    @Override public boolean has(Long k) {
        int hashIndex = hashIndex(k);
        CacheLock cacheLock = reentrantLocks.get(hashIndex);
        cacheLock.readLock.lock();
        try {
            return nodes.get(hashIndex).containsKey(k.longValue());
        } finally {
            cacheLock.readLock.unlock();
        }
    }

    @Override public V get(Long k) throws NullPointerException {
        V ifPresent = getIfPresent(k);
        if (ifPresent == null) {
            throw new NullPointerException(String.format("cache key=%s value is null", k));
        }
        return ifPresent;
    }

    @Override public V getIfPresent(Long k) {
        int hashIndex = hashIndex(k);
        CacheHolder<V> cacheHolder = null;
        CacheLock cacheLock = reentrantLocks.get(hashIndex);
        cacheLock.readLock.lock();
        try {
            cacheHolder = nodes.get(hashIndex).get(k.longValue());
        } finally {
            cacheLock.readLock.unlock();
        }
        if (cacheHolder == null) {
            if (LRULongCache.this.loader == null)
                return null;
            cacheLock.writeLock.lock();
            try {
                cacheHolder = nodes.get(hashIndex).get(k.longValue());
                if (cacheHolder == null) {
                    /*双重锁确保正确命中*/
                    V apply = LRULongCache.this.loader.apply(k);
                    /*TODO 即便是数据库 null 也要缓存, 防止缓存穿透*/
                    cacheHolder = newCacheHolder(apply);
                    nodes.get(hashIndex).put(k.longValue(), cacheHolder);
                }
            } finally {
                cacheLock.writeLock.unlock();
            }
        }
        if (cacheHolder == null) return null;
        refresh(cacheHolder);
        return cacheHolder.getValue();
    }

    @Override public V put(Long k, V v) {
        int hashIndex = hashIndex(k);
        CacheLock cacheLock = reentrantLocks.get(hashIndex);
        cacheLock.writeLock.lock();
        try {
            CacheHolder<V> cacheHolder = newCacheHolder(v);
            CacheHolder<V> old = nodes.get(hashIndex).put(k.longValue(), cacheHolder);
            return old == null ? null : old.getValue();
        } finally {
            cacheLock.writeLock.unlock();
        }
    }

    @Override public V putIfAbsent(Long k, V v) {
        int hashIndex = hashIndex(k);
        CacheLock cacheLock = reentrantLocks.get(hashIndex);
        cacheLock.writeLock.lock();
        try {
            CacheHolder<V> cacheHolder = newCacheHolder(v);
            CacheHolder<V> old = nodes.get(hashIndex).putIfAbsent(k, cacheHolder);
            if (old != null && old.getValue() == null) {
                nodes.get(hashIndex).put(k.longValue(), cacheHolder);
            }
            return old == null ? null : old.getValue();
        } finally {
            cacheLock.writeLock.unlock();
        }
    }

    @Override public V invalidate(Long longKey) {
        int hashIndex = hashIndex(longKey);
        CacheLock cacheLock = reentrantLocks.get(hashIndex);
        cacheLock.writeLock.lock();
        try {
            CacheHolder<V> cacheHolder = nodes.get(hashIndex).remove(longKey.longValue());
            if (cacheHolder == null) {
                return null;
            }
            cacheHolder.setExpireEndTime(0);
            onRemove(longKey, cacheHolder);
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
                Long2ObjectOpenHashMap<CacheHolder<V>> node = nodes.get(i);
                Long2ObjectMap.FastEntrySet<CacheHolder<V>> entries = node.long2ObjectEntrySet();
                for (Long2ObjectMap.Entry<CacheHolder<V>> entry : entries) {
                    long longKey = entry.getLongKey();
                    CacheHolder<V> holder = entry.getValue();
                    onRemove(longKey, holder);
                }
                node.clear();
            } finally {
                cacheLock.writeLock.unlock();
            }
        }
    }

    boolean onRemove(Long intKey, CacheHolder<V> cacheHolder) {
        if (LRULongCache.this.removalListener != null && cacheHolder.getValue() != null) {
            try {
                /*TODO 防止缓存穿透 holder.getValue() 可能为 null*/
                return LRULongCache.this.removalListener.apply(intKey, cacheHolder.getValue());
            } catch (Exception e) {
                log.error("removalListener 执行异常 {}", cacheHolder.getValue(), e);
            }
        }
        return true;
    }

    @Override public Collection<Long> keys() {
        List<Long> result = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            CacheLock cacheLock = reentrantLocks.get(i);
            cacheLock.readLock.lock();
            try {
                Long2ObjectOpenHashMap<CacheHolder<V>> node = nodes.get(i);
                List<Long> tmp = new ArrayList<>(node.size());
                for (Long2ObjectMap.Entry<CacheHolder<V>> holderLongEntry : node.long2ObjectEntrySet()) {
                    if (holderLongEntry.getValue().getValue() == null)
                        continue;
                    tmp.add(holderLongEntry.getLongKey());
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
                Long2ObjectOpenHashMap<CacheHolder<V>> node = nodes.get(i);
                List<V> tmp = new ArrayList<>(node.size());
                for (CacheHolder<V> holder : node.values()) {
                    if (holder.getValue() == null) continue;
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
                Long2ObjectOpenHashMap<CacheHolder<V>> node = nodes.get(i);
                size += node.size();
            } finally {
                cacheLock.readLock.unlock();
            }
        }
        return size;
    }

    @Deprecated
    @Override public void discard(Long k) {
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
