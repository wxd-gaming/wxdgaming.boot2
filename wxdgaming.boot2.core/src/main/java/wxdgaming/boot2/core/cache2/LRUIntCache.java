package wxdgaming.boot2.core.cache2;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
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
public class LRUIntCache<V> extends Cache<Integer, V> {

    List<CacheLock> reentrantLocks;
    List<Int2ObjectOpenHashMap<CacheHolder<V>>> nodes;
    TimerJob[] timerJobs;

    /** 计算内存大小 注意特别耗时，并且可能死循环 */
    @Deprecated
    public long memorySize() {
        long size = 0;
        for (Int2ObjectOpenHashMap<CacheHolder<V>> node : nodes) {
            size += Data2Size.totalSize0(node);
        }
        return size;
    }

    CacheHolder<V> newCacheHolder(V value) {
        CacheHolder<V> cacheHolder = new CacheHolder<>(value);
        cacheHolder.setLastHeartTime(MyClock.millis() + heartTimeMs);
        if (this.expireAfterWriteMs > 0) {
            cacheHolder.setExpireEndTime(MyClock.millis() + expireAfterWriteMs);
        }
        return cacheHolder;
    }

    void refresh(CacheHolder<V> cacheHolder) {
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
                        ObjectIterator<Int2ObjectMap.Entry<CacheHolder<V>>> iterator = nodes.get(hashIndex).int2ObjectEntrySet().iterator();
                        long millis = MyClock.millis();
                        while (iterator.hasNext()) {
                            Int2ObjectMap.Entry<CacheHolder<V>> next = iterator.next();
                            CacheHolder<V> holder = next.getValue();
                            if (millis > holder.getExpireEndTime()) {
                                boolean remove = true;
                                if (LRUIntCache.this.removalListener != null) {
                                    remove = LRUIntCache.this.removalListener.apply(next.getIntKey(), holder.getValue());
                                }
                                if (remove)
                                    iterator.remove();
                                else
                                    refresh(holder);/*移除缓存失败刷新一次*/
                            } else {
                                if (LRUIntCache.this.heartListener != null && millis > holder.getLastHeartTime()) {
                                    LRUIntCache.this.heartListener.accept(next.getIntKey(), holder.getValue());
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
        List<Int2ObjectOpenHashMap<CacheHolder<V>>> tmpNodes = new ArrayList<>(this.area);
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

    @Override public V get(Integer k) throws NullPointerException {
        V ifPresent = getIfPresent(k);
        if (ifPresent == null) {
            throw new NullPointerException(String.format("cache key=%s value is null", k));
        }
        return ifPresent;
    }

    @Override public V getIfPresent(Integer k) {
        return getLongIfPresent(k);
    }

    public V getLongIfPresent(int k) {
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
            cacheLock.writeLock.lock();
            try {
                cacheHolder = nodes.get(hashIndex).get(k);
                if (cacheHolder == null) {
                    /*双重锁确保正确命中*/
                    if (LRUIntCache.this.loader == null)
                        return null;
                    V apply = LRUIntCache.this.loader.apply(k);
                    if (apply == null)
                        return null;
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

    @Override public V put(Integer k, V v) {
        int hashIndex = hashIndex(k);
        CacheLock cacheLock = reentrantLocks.get(hashIndex);
        cacheLock.writeLock.lock();
        try {
            CacheHolder<V> cacheHolder = newCacheHolder(v);
            CacheHolder<V> old = nodes.get(hashIndex).put(k.intValue(), cacheHolder);
            return old == null ? null : old.getValue();
        } finally {
            cacheLock.writeLock.unlock();
        }
    }

    @Override public V putIfAbsent(Integer k, V v) {
        int hashIndex = hashIndex(k);
        CacheLock cacheLock = reentrantLocks.get(hashIndex);
        cacheLock.writeLock.lock();
        try {
            CacheHolder<V> cacheHolder = newCacheHolder(v);
            CacheHolder<V> old = nodes.get(hashIndex).putIfAbsent(k, cacheHolder);
            return old == null ? null : old.getValue();
        } finally {
            cacheLock.writeLock.unlock();
        }
    }

    @Override public V invalidate(Integer k) {
        int hashIndex = hashIndex(k);
        CacheLock cacheLock = reentrantLocks.get(hashIndex);
        cacheLock.writeLock.lock();
        try {
            CacheHolder<V> cacheHolder = nodes.get(hashIndex).get(k.intValue());
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
                Int2ObjectOpenHashMap<CacheHolder<V>> node = nodes.get(i);
                for (CacheHolder<V> holder : node.values()) {
                    holder.setExpireEndTime(0);
                }
            } finally {
                cacheLock.writeLock.unlock();
            }
        }
    }

    /** 拷贝所有元素 */
    @Override public Collection<V> values() {
        List<V> result = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            CacheLock cacheLock = reentrantLocks.get(i);
            cacheLock.readLock.lock();
            try {
                Int2ObjectOpenHashMap<CacheHolder<V>> node = nodes.get(i);
                List<V> tmp = new ArrayList<>(node.size());
                for (CacheHolder<V> holder : node.values()) {
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
                Int2ObjectOpenHashMap<CacheHolder<V>> node = nodes.get(i);
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
