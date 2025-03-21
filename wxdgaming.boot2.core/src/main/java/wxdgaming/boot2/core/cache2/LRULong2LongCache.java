package wxdgaming.boot2.core.cache2;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.format.data.Data2Size;
import wxdgaming.boot2.core.threading.Event;
import wxdgaming.boot2.core.threading.ExecutorUtil;
import wxdgaming.boot2.core.threading.TimerJob;
import wxdgaming.boot2.core.timer.MyClock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * cas 类型的缓存
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-03-20 16:14
 **/
@Slf4j
@SuperBuilder
public class LRULong2LongCache extends Cache<Long, Long> {

    List<CacheLock> reentrantLocks;
    List<Long2ObjectOpenHashMap<CacheHolderLong>> nodes;
    TimerJob[] timerJobs;

    /** 计算内存大小 注意特别耗时，并且可能死循环 */
    @Deprecated
    public long memorySize() {
        long size = 0;
        for (Long2ObjectOpenHashMap<CacheHolderLong> node : nodes) {
            size += Data2Size.totalSize0(node);
        }
        return size;
    }

    CacheHolderLong newCacheHolder(Long value) {
        CacheHolderLong cacheHolder = new CacheHolderLong(value);
        cacheHolder.setLastHeartTime(MyClock.millis() + heartTimeMs);
        if (this.expireAfterWriteMs > 0) {
            cacheHolder.setExpireEndTime(MyClock.millis() + expireAfterWriteMs);
        }
        return cacheHolder;
    }

    void refresh(CacheHolderLong cacheHolder) {
        if (this.expireAfterReadMs > 0) {
            cacheHolder.setExpireEndTime(MyClock.millis() + expireAfterReadMs);
        }
    }

    public void start() {
        super.start();
        initNodes();
        this.timerJobs = new TimerJob[this.area];

        for (int i = 0; i < this.area; i++) {
            final int hashIndex = i;
            Event heartEvent = new Event(500, 1000) {
                @Override public void onEvent() throws Exception {
                    CacheLock cacheLock = reentrantLocks.get(hashIndex);
                    cacheLock.writeLock.lock();
                    try {
                        ObjectIterator<Long2ObjectMap.Entry<CacheHolderLong>> iterator = nodes.get(hashIndex).long2ObjectEntrySet().iterator();
                        long millis = MyClock.millis();
                        while (iterator.hasNext()) {
                            Long2ObjectMap.Entry<CacheHolderLong> next = iterator.next();
                            CacheHolderLong holder = next.getValue();
                            if (millis > holder.getExpireEndTime()) {
                                boolean remove = true;
                                if (LRULong2LongCache.this.removalListener != null) {
                                    remove = LRULong2LongCache.this.removalListener.apply(next.getLongKey(), holder.getValue());
                                }
                                if (remove)
                                    iterator.remove();
                                else
                                    refresh(holder);/*移除缓存失败刷新一次*/
                            } else {
                                if (LRULong2LongCache.this.heartListener != null && millis > holder.getLastHeartTime()) {
                                    LRULong2LongCache.this.heartListener.accept(next.getLongKey(), holder.getValue());
                                }
                            }
                        }
                    } finally {
                        cacheLock.writeLock.unlock();
                    }
                }
            };
            TimerJob timerJob = ExecutorUtil.getInstance()
                    .getDefaultExecutor()
                    .scheduleAtFixedDelay(
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
        List<Long2ObjectOpenHashMap<CacheHolderLong>> tmpNodes = new ArrayList<>(this.area);
        for (int i = 0; i < this.area; i++) {
            tmpLock.add(new CacheLock());
            tmpNodes.add(new Long2ObjectOpenHashMap<>());
        }

        this.reentrantLocks = Collections.unmodifiableList(tmpLock);
        this.nodes = Collections.unmodifiableList(tmpNodes);
    }

    @Override public void shutdown() {
        for (TimerJob timerJob : timerJobs) {
            timerJob.cancel();
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

    @Override public Long get(Long k) throws NullPointerException {
        Long ifPresent = getIfPresent(k);
        if (ifPresent == null) {
            throw new NullPointerException(String.format("cache key=%s value is null", k));
        }
        return ifPresent;
    }

    @Override public Long getIfPresent(Long k) {
        int hashIndex = hashIndex(k);
        CacheHolderLong cacheHolder = null;
        CacheLock cacheLock = reentrantLocks.get(hashIndex);
        cacheLock.readLock.lock();
        try {
            cacheHolder = nodes.get(hashIndex).get(k.longValue());
        } finally {
            cacheLock.readLock.unlock();
        }
        if (cacheHolder == null) {
            cacheLock.writeLock.lock();
            try {
                cacheHolder = nodes.get(hashIndex).get(k.longValue());
                if (cacheHolder == null) {
                    /*双重锁确保正确命中*/
                    if (LRULong2LongCache.this.loader == null)
                        return null;
                    Long apply = LRULong2LongCache.this.loader.apply(k);
                    if (apply == null)
                        return null;
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

    @Override public Long put(Long k, Long v) {
        return putLong(k, v);
    }

    public long putLong(long k, long v) {
        int hashIndex = hashIndex(k);
        CacheLock cacheLock = reentrantLocks.get(hashIndex);
        cacheLock.writeLock.lock();
        try {
            CacheHolderLong cacheHolder = newCacheHolder(v);
            CacheHolderLong old = nodes.get(hashIndex).put(k, cacheHolder);
            return old == null ? 0 : old.getValue();
        } finally {
            cacheLock.writeLock.unlock();
        }
    }

    @Override public Long putIfAbsent(Long k, Long v) {
        int hashIndex = hashIndex(k);
        CacheLock cacheLock = reentrantLocks.get(hashIndex);
        cacheLock.writeLock.lock();
        try {
            CacheHolderLong cacheHolder = newCacheHolder(v);
            CacheHolderLong old = nodes.get(hashIndex).putIfAbsent(k, cacheHolder);
            return old == null ? null : old.getValue();
        } finally {
            cacheLock.writeLock.unlock();
        }
    }

    @Override public Long invalidate(Long k) {
        int hashIndex = hashIndex(k);
        CacheLock cacheLock = reentrantLocks.get(hashIndex);
        cacheLock.writeLock.lock();
        try {
            CacheHolderLong cacheHolder = nodes.get(hashIndex).get(k.longValue());
            if (cacheHolder == null) {
                return null;
            }
            cacheHolder.setExpireEndTime(0);
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
                Long2ObjectOpenHashMap<CacheHolderLong> node = nodes.get(i);
                for (CacheHolderLong holder : node.values()) {
                    holder.setExpireEndTime(0);
                }
            } finally {
                cacheLock.writeLock.unlock();
            }
        }
    }

    /** 拷贝所有元素 */
    @Override public Collection<Long> values() {
        List<Long> result = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            CacheLock cacheLock = reentrantLocks.get(i);
            cacheLock.readLock.lock();
            try {
                Long2ObjectOpenHashMap<CacheHolderLong> node = nodes.get(i);
                List<Long> tmp = new ArrayList<>(node.size());
                for (CacheHolderLong holder : node.values()) {
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
                Long2ObjectOpenHashMap<CacheHolderLong> node = nodes.get(i);
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
