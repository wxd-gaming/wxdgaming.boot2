package wxdgaming.boot2.core.cache2;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.format.data.Data2Size;
import wxdgaming.boot2.core.threading.Event;
import wxdgaming.boot2.core.threading.ExecutorUtil;
import wxdgaming.boot2.core.threading.ExecutorUtilImpl;
import wxdgaming.boot2.core.threading.TimerJob;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.core.util.AssertUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * lru 类型的缓存
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-03-20 16:14
 **/
@Slf4j
@SuperBuilder
public class LRUInt2IntCache extends Cache<Integer, Integer> {

    List<CacheLock> reentrantLocks;
    List<Int2ObjectOpenHashMap<CacheHolderInt>> nodes;

    /** 计算内存大小 注意特别耗时，并且可能死循环 */
    @Deprecated
    public long memorySize() {
        long size = 0;
        for (Int2ObjectOpenHashMap<CacheHolderInt> node : nodes) {
            size += Data2Size.totalSize0(node);
        }
        return size;
    }

    CacheHolderInt newCacheHolder(Integer value) {
        CacheHolderInt cacheHolder = new CacheHolderInt(value);
        cacheHolder.setLastHeartTime(MyClock.millis() + heartTimeMs);
        if (this.expireAfterWriteMs > 0) {
            cacheHolder.setExpireEndTime(MyClock.millis() + expireAfterWriteMs);
        }
        return cacheHolder;
    }

    void refresh(CacheHolderInt cacheHolder) {
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
                        ObjectIterator<Int2ObjectMap.Entry<CacheHolderInt>> iterator = nodes.get(hashIndex).int2ObjectEntrySet().iterator();
                        long millis = MyClock.millis();
                        while (iterator.hasNext()) {
                            Int2ObjectMap.Entry<CacheHolderInt> next = iterator.next();
                            CacheHolderInt holder = next.getValue();
                            if (millis > holder.getExpireEndTime()) {
                                boolean remove = true;
                                if (LRUInt2IntCache.this.removalListener != null) {
                                    remove = LRUInt2IntCache.this.removalListener.apply(next.getIntKey(), holder.getValue());
                                }
                                if (remove)
                                    iterator.remove();
                                else
                                    refresh(holder);/*移除缓存失败刷新一次*/
                            } else {
                                if (LRUInt2IntCache.this.heartListener != null && millis > holder.getLastHeartTime()) {
                                    LRUInt2IntCache.this.heartListener.accept(next.getIntKey(), holder.getValue());
                                }
                            }
                        }
                    } finally {
                        cacheLock.writeLock.unlock();
                    }
                }
            };
            TimerJob timerJob = ExecutorUtilImpl.getInstance()
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
        List<Int2ObjectOpenHashMap<CacheHolderInt>> tmpNodes = new ArrayList<>(this.area);
        for (int i = 0; i < this.area; i++) {
            tmpLock.add(new CacheLock());
            tmpNodes.add(new Int2ObjectOpenHashMap<>());
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

    @Override public boolean has(Integer k) {
        int hashIndex = hashIndex(k);
        CacheLock cacheLock = reentrantLocks.get(hashIndex);
        cacheLock.readLock.lock();
        try {
            return nodes.get(hashIndex).containsKey(k.intValue());
        } finally {
            cacheLock.readLock.unlock();
        }
    }

    @Override public Integer get(Integer k) throws NullPointerException {
        Integer ifPresent = getIfPresent(k);
        if (ifPresent == null) {
            throw new NullPointerException(String.format("cache key=%s value is null", k));
        }
        return ifPresent;
    }

    @Override public Integer getIfPresent(Integer k) {
        int hashIndex = hashIndex(k);
        CacheHolderInt cacheHolder = null;
        CacheLock cacheLock = reentrantLocks.get(hashIndex);
        cacheLock.readLock.lock();
        try {
            cacheHolder = nodes.get(hashIndex).get(k.intValue());
        } finally {
            cacheLock.readLock.unlock();
        }
        if (cacheHolder == null) {
            cacheLock.writeLock.lock();
            try {
                cacheHolder = nodes.get(hashIndex).get(k.intValue());
                if (cacheHolder == null) {
                    /*双重锁确保正确命中*/
                    if (LRUInt2IntCache.this.loader == null)
                        return null;
                    Integer apply = LRUInt2IntCache.this.loader.apply(k);
                    if (apply == null)
                        return null;
                    cacheHolder = newCacheHolder(apply);
                    nodes.get(hashIndex).put(k.intValue(), cacheHolder);
                }
            } finally {
                cacheLock.writeLock.unlock();
            }
        }
        if (cacheHolder == null) return null;
        refresh(cacheHolder);
        return cacheHolder.getValue();
    }

    @Override public Integer put(Integer k, Integer v) {
        int hashIndex = hashIndex(k);
        CacheLock cacheLock = reentrantLocks.get(hashIndex);
        cacheLock.writeLock.lock();
        try {
            CacheHolderInt cacheHolder = newCacheHolder(v);
            CacheHolderInt old = nodes.get(hashIndex).put(k.intValue(), cacheHolder);
            return old == null ? null : old.getValue();
        } finally {
            cacheLock.writeLock.unlock();
        }
    }

    @Override public Integer putIfAbsent(Integer k, Integer v) {
        int hashIndex = hashIndex(k);
        CacheLock cacheLock = reentrantLocks.get(hashIndex);
        cacheLock.writeLock.lock();
        try {
            CacheHolderInt cacheHolder = newCacheHolder(v);
            CacheHolderInt old = nodes.get(hashIndex).putIfAbsent(k, cacheHolder);
            return old == null ? null : old.getValue();
        } finally {
            cacheLock.writeLock.unlock();
        }
    }

    @Override public Integer invalidate(Integer k) {
        int hashIndex = hashIndex(k);
        CacheLock cacheLock = reentrantLocks.get(hashIndex);
        cacheLock.writeLock.lock();
        try {
            CacheHolderInt cacheHolder = nodes.get(hashIndex).get(k.intValue());
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
                Int2ObjectOpenHashMap<CacheHolderInt> node = nodes.get(i);
                for (CacheHolderInt holder : node.values()) {
                    holder.setExpireEndTime(0);
                }
            } finally {
                cacheLock.writeLock.unlock();
            }
        }
    }

    @Override public Collection<Integer> keys() {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            CacheLock cacheLock = reentrantLocks.get(i);
            cacheLock.readLock.lock();
            try {
                Int2ObjectOpenHashMap<CacheHolderInt> node = nodes.get(i);
                List<Integer> tmp = new ArrayList<>(node.size());
                for (Int2ObjectMap.Entry<CacheHolderInt> holderLongEntry : node.int2ObjectEntrySet()) {
                    tmp.add(holderLongEntry.getIntKey());
                }
                result.addAll(tmp);
            } finally {
                cacheLock.readLock.unlock();
            }
        }
        return result;
    }

    /** 拷贝所有元素 */
    @Override public Collection<Integer> values() {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            CacheLock cacheLock = reentrantLocks.get(i);
            cacheLock.readLock.lock();
            try {
                Int2ObjectOpenHashMap<CacheHolderInt> node = nodes.get(i);
                List<Integer> tmp = new ArrayList<>(node.size());
                for (CacheHolderInt holder : node.values()) {
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
                Int2ObjectOpenHashMap<CacheHolderInt> node = nodes.get(i);
                size += node.size();
            } finally {
                cacheLock.readLock.unlock();
            }
        }
        return size;
    }

    @Deprecated
    @Override public void discard(Integer k) {
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
