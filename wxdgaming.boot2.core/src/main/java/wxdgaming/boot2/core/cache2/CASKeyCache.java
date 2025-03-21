package wxdgaming.boot2.core.cache2;

import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.format.data.Data2Size;
import wxdgaming.boot2.core.threading.Event;
import wxdgaming.boot2.core.threading.ExecutorUtil;
import wxdgaming.boot2.core.threading.TimerJob;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.core.util.AssertUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 仅有key的缓存
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-03-20 16:14
 **/
@Slf4j
@SuperBuilder
public class CASKeyCache<K> extends Cache<K, Boolean> {

    List<ConcurrentHashMap<K, CacheHolderNone>> nodes;

    /** 计算内存大小 注意特别耗时，并且可能死循环 */
    @Deprecated
    public long memorySize() {
        long size = 0;
        for (ConcurrentHashMap<K, CacheHolderNone> node : nodes) {
            size += Data2Size.totalSize0(node);
        }
        return size;
    }

    CacheHolderNone newCacheHolder() {
        CacheHolderNone cacheHolder = new CacheHolderNone();
        cacheHolder.setLastHeartTime(MyClock.millis() + heartTimeMs);
        if (this.expireAfterWriteMs > 0) {
            cacheHolder.setExpireEndTime(MyClock.millis() + expireAfterWriteMs);
        }
        return cacheHolder;
    }

    void refresh(CacheHolderNone cacheHolder) {
        if (this.expireAfterReadMs > 0) {
            cacheHolder.setExpireEndTime(MyClock.millis() + expireAfterReadMs);
        }
    }

    public void start() {
        super.start();
        AssertUtil.assertTrue(this.loader == null, "不支持调用 load 函数");
        initNodes();
        this.timerJobs = new TimerJob[this.area];
        for (int i = 0; i < this.area; i++) {
            final int a = i;
            Event heartEvent = new Event(500, 1000) {
                @Override public void onEvent() throws Exception {
                    Iterator<Map.Entry<K, CacheHolderNone>> iterator = nodes.get(a).entrySet().iterator();
                    long millis = MyClock.millis();
                    while (iterator.hasNext()) {
                        Map.Entry<K, CacheHolderNone> next = iterator.next();
                        CacheHolderNone holder = next.getValue();
                        if (millis > holder.getExpireEndTime()) {
                            boolean remove = true;
                            if (CASKeyCache.this.removalListener != null) {
                                remove = CASKeyCache.this.removalListener.apply(next.getKey(), true);
                            }
                            if (remove)
                                iterator.remove();
                            else
                                refresh(holder);/*移除缓存失败刷新一次*/
                        } else {
                            if (CASKeyCache.this.heartListener != null && millis > holder.getLastHeartTime()) {
                                CASKeyCache.this.heartListener.accept(next.getKey(), true);
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
        List<ConcurrentHashMap<K, CacheHolderNone>> tmpNodes = new ArrayList<>(this.area);
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

    @Override public Boolean get(K k) throws NullPointerException {
        Boolean ifPresent = getIfPresent(k);
        if (ifPresent == null) {
            throw new NullPointerException(String.format("cache key=%s value is null", k));
        }
        return ifPresent;
    }

    @Override public Boolean getIfPresent(K k) {
        int hashIndex = hashIndex(k);
        CacheHolderNone cacheHolder = nodes.get(hashIndex).get(k);
        if (cacheHolder == null) return null;
        refresh(cacheHolder);
        return true;
    }

    @Override public Boolean put(K k, Boolean v) {
        int hashIndex = hashIndex(k);
        CacheHolderNone cacheHolder = newCacheHolder();
        CacheHolderNone old = nodes.get(hashIndex).put(k, cacheHolder);
        return old != null;
    }

    @Override public Boolean putIfAbsent(K k, Boolean v) {
        int hashIndex = hashIndex(k);
        CacheHolderNone cacheHolder = newCacheHolder();
        CacheHolderNone old = nodes.get(hashIndex).putIfAbsent(k, cacheHolder);
        return old != null;
    }

    @Override public Boolean invalidate(K k) {
        int hashIndex = hashIndex(k);
        CacheHolderNone cacheHolder = nodes.get(hashIndex).get(k);
        if (cacheHolder == null) {
            return null;
        }
        cacheHolder.setExpireEndTime(0);
        return true;
    }

    @Override public void invalidateAll() {
        for (int i = 0; i < nodes.size(); i++) {
            ConcurrentHashMap<K, CacheHolderNone> node = nodes.get(i);
            for (CacheHolderNone holder : node.values()) {
                holder.setExpireEndTime(0);
            }
        }
    }

    @Override public Collection<K> keys() {
        return nodes.stream()
                .flatMap(v -> v.keySet().stream())
                .toList();
    }

    /** 拷贝所有元素 */
    @Override public Collection<Boolean> values() {
        throw new UnsupportedOperationException("不支持");
    }

    @Override public long size() {
        long size = 0;
        for (int i = 0; i < nodes.size(); i++) {
            ConcurrentHashMap<K, CacheHolderNone> node = nodes.get(i);
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
