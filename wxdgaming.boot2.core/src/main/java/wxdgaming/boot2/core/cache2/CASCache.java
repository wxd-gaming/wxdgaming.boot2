package wxdgaming.boot2.core.cache2;

import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.format.data.Data2Size;
import wxdgaming.boot2.core.threading.Event;
import wxdgaming.boot2.core.threading.ExecutorUtil;
import wxdgaming.boot2.core.threading.TimerJob;
import wxdgaming.boot2.core.timer.MyClock;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * cas 类型的缓存
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-03-20 16:14
 **/
@Slf4j
@SuperBuilder
public class CASCache<K, V> extends Cache<K, V> {

    List<ConcurrentHashMap<K, CacheHolder<V>>> nodes;
    protected TimerJob[] timerJobs;

    /** 计算内存大小 注意特别耗时，并且可能死循环 */
    @Deprecated
    public long memorySize() {
        long size = 0;
        for (ConcurrentHashMap<K, CacheHolder<V>> node : nodes) {
            size += Data2Size.totalSize0(node);
        }
        return size;
    }

    protected CacheHolder<V> newCacheHolder(V value) {
        CacheHolder<V> cacheHolder = new CacheHolder<>(value);
        cacheHolder.setLastHeartTime(MyClock.millis() + heartTimeMs);
        if (this.expireAfterWriteMs > 0) {
            cacheHolder.setExpireEndTime(MyClock.millis() + expireAfterWriteMs);
        }
        return cacheHolder;
    }

    protected void refresh(CacheHolder<V> cacheHolder) {
        if (this.expireAfterReadMs > 0) {
            cacheHolder.setExpireEndTime(MyClock.millis() + expireAfterReadMs);
        }
    }

    public void start() {
        super.start();
        initNodes();
        this.timerJobs = new TimerJob[this.area];
        for (int i = 0; i < this.area; i++) {
            final int a = i;
            Event heartEvent = new Event(500, 1000) {
                @Override public void onEvent() throws Exception {
                    Iterator<Map.Entry<K, CacheHolder<V>>> iterator = nodes.get(a).entrySet().iterator();
                    long millis = MyClock.millis();
                    while (iterator.hasNext()) {
                        Map.Entry<K, CacheHolder<V>> next = iterator.next();
                        CacheHolder<V> holder = next.getValue();
                        if (millis > holder.getExpireEndTime()) {
                            boolean remove = true;
                            if (CASCache.this.removalListener != null) {
                                remove = CASCache.this.removalListener.apply(next.getKey(), holder.getValue());
                            }
                            if (remove)
                                iterator.remove();
                            else
                                refresh(holder);/*移除缓存失败刷新一次*/
                        } else {
                            if (CASCache.this.heartListener != null && millis > holder.getLastHeartTime()) {
                                CASCache.this.heartListener.accept(next.getKey(), holder.getValue());
                            }
                        }
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
        List<ConcurrentHashMap<K, CacheHolder<V>>> tmpNodes = new ArrayList<>(this.area);
        for (int i = 0; i < this.area; i++) {
            tmpNodes.add(new ConcurrentHashMap<>());
        }

        this.nodes = Collections.unmodifiableList(tmpNodes);
    }

    @Override public void shutdown() {
        for (TimerJob timerJob : timerJobs) {
            timerJob.cancel();
        }
        invalidateAll();
    }

    @Override public boolean has(K k) {
        int hashIndex = hashIndex(k);
        return nodes.get(hashIndex).containsKey(k);
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
        CacheHolder<V> cacheHolder = nodes.get(hashIndex).computeIfAbsent(k, c -> {
            if (CASCache.this.loader == null) return null;
            V apply = CASCache.this.loader.apply(k);
            if (apply == null) {
                return null;
            }
            return newCacheHolder(apply);
        });
        if (cacheHolder == null) return null;
        refresh(cacheHolder);
        return cacheHolder.getValue();
    }

    @Override public V put(K k, V v) {
        int hashIndex = hashIndex(k);
        CacheHolder<V> cacheHolder = newCacheHolder(v);
        CacheHolder<V> old = nodes.get(hashIndex).put(k, cacheHolder);
        return old == null ? null : old.getValue();
    }

    @Override public V putIfAbsent(K k, V v) {
        int hashIndex = hashIndex(k);
        CacheHolder<V> cacheHolder = newCacheHolder(v);
        CacheHolder<V> old = nodes.get(hashIndex).putIfAbsent(k, cacheHolder);
        return old == null ? null : old.getValue();
    }

    @Override public V invalidate(K k) {
        int hashIndex = hashIndex(k);
        CacheHolder<V> cacheHolder = nodes.get(hashIndex).get(k);
        if (cacheHolder == null) {
            return null;
        }
        cacheHolder.setExpireEndTime(0);
        return cacheHolder.getValue();
    }

    @Override public void invalidateAll() {
        for (int i = 0; i < nodes.size(); i++) {
            ConcurrentHashMap<K, CacheHolder<V>> node = nodes.get(i);
            for (CacheHolder<V> holder : node.values()) {
                holder.setExpireEndTime(0);
            }
        }
    }

    /** 拷贝所有元素 */
    @Override public Collection<V> values() {
        return nodes.stream()
                .flatMap(v -> v.values().stream())
                .map(CacheHolder::getValue)
                .toList();
    }

    @Override public long size() {
        long size = 0;
        for (int i = 0; i < nodes.size(); i++) {
            ConcurrentHashMap<K, CacheHolder<V>> node = nodes.get(i);
            size += node.size();
        }
        return size;
    }

    @Deprecated
    @Override public void discard(K k) {
        int hashIndex = hashIndex(k);
        nodes.get(hashIndex).remove(k);
    }

    @Deprecated
    @Override public void discardAll() {
        initNodes();
    }

}
