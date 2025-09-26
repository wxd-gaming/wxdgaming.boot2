package wxdgaming.boot2.core.cache2;

import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.executor.ExecutorEvent;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.format.data.Data2Size;
import wxdgaming.boot2.core.timer.MyClock;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * cas 类型的缓存, 具有防止缓存穿透效果
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-03-20 16:14
 **/
@Slf4j
@SuperBuilder
public class CASCache<K, V> extends Cache<K, V> {

    List<ConcurrentHashMap<K, CacheHolder<V>>> nodes;

    /** 计算内存大小 注意特别耗时，并且可能死循环 */
    @Deprecated
    public long memorySize() {
        long size = 0;
        for (ConcurrentHashMap<K, CacheHolder<V>> node : nodes) {
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
            final int a = i;
            Runnable heartEvent = new ExecutorEvent() {

                @Override public String getQueueName() {
                    return "cache-heart-event";
                }

                @Override public String getStack() {
                    return this.getQueueName();
                }

                @Override public void onEvent() throws Exception {
                    Iterator<Map.Entry<K, CacheHolder<V>>> iterator = nodes.get(a).entrySet().iterator();
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
                                if (holder.getValue() != null && CASCache.this.heartListener != null && millis > holder.getLastHeartTime()) {
                                    /*TODO 防止缓存穿透 holder.getValue() 可能为 null*/
                                    CASCache.this.heartListener.accept(next.getKey(), holder.getValue());
                                }
                            }
                        } catch (Exception e) {
                            log.error("CASCache 心跳异常 {}", holder.getValue(), e);
                        }
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
        List<ConcurrentHashMap<K, CacheHolder<V>>> tmpNodes = new ArrayList<>(this.area);
        for (int i = 0; i < this.area; i++) {
            tmpNodes.add(new ConcurrentHashMap<>());
        }

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
            /*TODO 即便是数据库 null 也要缓存, 防止缓存穿透*/
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
        if (old != null && old.getValue() == null) {
            nodes.get(hashIndex).put(k, cacheHolder);
        }
        return old == null ? null : old.getValue();
    }

    @Override public V invalidate(K k) {
        int hashIndex = hashIndex(k);
        CacheHolder<V> cacheHolder = nodes.get(hashIndex).remove(k);
        if (cacheHolder == null) {
            return null;
        }
        cacheHolder.setExpireEndTime(0);
        onRemove(k, cacheHolder);
        return cacheHolder.getValue();
    }

    @Override public void invalidateAll() {
        for (int i = 0; i < nodes.size(); i++) {
            ConcurrentHashMap<K, CacheHolder<V>> node = nodes.get(i);
            Iterator<Map.Entry<K, CacheHolder<V>>> iterator = node.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<K, CacheHolder<V>> next = iterator.next();
                K key = next.getKey();
                CacheHolder<V> holder = next.getValue();
                onRemove(key, holder);
                iterator.remove();
            }
        }
    }

    boolean onRemove(K key, CacheHolder<V> cacheHolder) {
        if (CASCache.this.removalListener != null && cacheHolder.getValue() != null) {
            try {
                /*TODO 防止缓存穿透 holder.getValue() 可能为 null*/
                return CASCache.this.removalListener.apply(key, cacheHolder.getValue());
            } catch (Exception e) {
                log.error("removalListener 执行异常 {}", cacheHolder.getValue(), e);
            }
        }
        return true;
    }

    @Override public Collection<K> keys() {
        return nodes.stream()
                .flatMap(v -> v.entrySet().stream())
                /*TODO 防止缓存穿透 holder.getValue() 可能为 null*/
                .filter(v -> v.getValue() != null)
                .map(Map.Entry::getKey)
                .toList();
    }

    /** 拷贝所有元素 */
    @Override public Collection<V> values() {
        return nodes.stream()
                .flatMap(v -> v.values().stream())
                /*TODO 防止缓存穿透 holder.getValue() 可能为 null*/
                .filter(v -> v.getValue() != null)
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
