package code.cache;

import lombok.Builder;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.function.Predicate3;
import wxdgaming.boot2.core.util.AssertUtil;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * 缓存持有容器，通过blockSize 切割成多个容器，避免数据过多但容器太大
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-07-03 10:57
 **/
class CASCacheHolder<K, V> {

    static final ScheduledExecutorService scheduledExecutorService = ExecutorFactory.newSingleThreadScheduledExecutor("cache-scheduled");

    protected final int blockSize;
    protected List<CacheBlock> blockList;
    protected ScheduledFuture<?> scheduledFuture;
    /** 读取过期时间 */
    protected final Duration expireAfterAccess;
    /** 写入过期时间 */
    protected final Duration expireAfterWrite;

    protected final Function<K, V> loader;
    protected final Predicate3<K, V, CASCache.RemovalCause> removalListener;

    protected class CacheNode implements Comparable<CacheNode> {

        private final K key;
        private final V value;
        private volatile long expireTime;

        public CacheNode(K key, V value) {
            AssertUtil.isNull(key, "value is null");
            AssertUtil.isNull(value, "value is null");
            this.key = key;
            this.value = value;
            refresh(false);
        }

        private void refresh(boolean force) {
            if (expireAfterAccess != null || expireAfterWrite != null) {
                if (expireAfterWrite != null) {
                    if (expireTime == 0 || force) {
                        expireTime = System.currentTimeMillis() + expireAfterWrite.toMillis();
                    }
                } else {
                    expireTime = System.currentTimeMillis() + expireAfterAccess.toMillis();
                }
            }
        }

        @SuppressWarnings("unchecked")
        @Override public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;

            CacheNode cacheNode = (CacheNode) (o);
            return Objects.equals(key, cacheNode.key);
        }

        @Override public int hashCode() {
            return Objects.hashCode(key);
        }

        @Override public int compareTo(CacheNode o) {
            if (this.expireTime != o.expireTime)
                return Long.compare(this.expireTime, o.expireTime);
            return Integer.compare(this.hashCode(), o.hashCode());
        }
    }

    /** 容器快 */
    protected class CacheBlock {
        /** 区块锁 */
        final ConcurrentHashMap<K, CacheNode> nodeMap = new ConcurrentHashMap<>();
        final ConcurrentSkipListSet<CacheNode> expireSet = new ConcurrentSkipListSet<>();

        public void put(K key, V value) {
            CacheNode newNode = new CacheNode(key, value);
            CacheNode oldNode = nodeMap.put(key, newNode);
            if (oldNode != null) {
                expireSet.remove(oldNode);
                if (!Objects.equals(oldNode.value, value)) {
                    onRemove(oldNode, CASCache.RemovalCause.REPLACED);
                }
            }
            expireSet.add(newNode);
        }


        public V get(K key) {
            CacheNode cacheNode = nodeMap.computeIfAbsent(key, l -> {
                if (loader == null)
                    return null;
                V value = loader.apply(key);
                if (value == null)
                    return null;
                CacheNode loadNode = new CacheNode(key, value);
                expireSet.add(loadNode);
                return loadNode;
            });
            if (cacheNode != null && expireAfterWrite == null) {
                /*TODO 固定缓存不需要刷新，因为时间不会边*/
                expireSet.remove(cacheNode);
                cacheNode.refresh(false);
                expireSet.add(cacheNode);
            }
            return cacheNode == null ? null : cacheNode.value;
        }

        public int size() {
            return nodeMap.size();
        }

        public Collection<V> values() {
            return nodeMap.values().stream().map(n -> n.value).toList();
        }

    }

    @Builder
    public CASCacheHolder(int blockSize,
                          Duration expireAfterAccess,
                          Duration expireAfterWrite,
                          Function<K, V> loader,
                          Predicate3<K, V, CASCache.RemovalCause> removalListener) {

        if (expireAfterAccess != null && expireAfterWrite != null)
            throw new RuntimeException("expireAfterAccess or expireAfterWrite");

        this.blockSize = blockSize == 0 ? 16 : blockSize;
        this.expireAfterAccess = expireAfterAccess;
        this.expireAfterWrite = expireAfterWrite;
        this.loader = loader;
        this.removalListener = removalListener;

        init();
    }


    private void init() {
        blockList = new ArrayList<>(blockSize);
        for (int i = 0; i < this.blockSize; i++) {
            blockList.add(new CacheBlock());
        }

        if (expireAfterAccess != null || expireAfterWrite != null) {
            long delay = expireAfterAccess != null ? expireAfterAccess.toMillis() : expireAfterWrite.toMillis();
            if (delay < 1000)
                throw new RuntimeException("expire < 1s");
            delay = delay / 100;
            scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> onCleanup(CASCache.RemovalCause.EXPIRE), delay, delay, TimeUnit.MILLISECONDS);
        } else {
            AssertUtil.notNull(this.removalListener, "removalListener 非空 请配置过期时间");
        }
    }

    public void close() {
        scheduledFuture.cancel(true);
    }

    private int getBlockIndex(K key) {
        return Math.abs(key.hashCode()) % blockSize;
    }

    public long size() {
        return blockList.stream().mapToLong(CacheBlock::size).sum();
    }

    public void put(K key, V value) {
        int blockIndex = getBlockIndex(key);
        blockList.get(blockIndex).put(key, value);
    }

    public V get(K key) {
        int blockIndex = getBlockIndex(key);
        return blockList.get(blockIndex).get(key);
    }

    public void invalidate(K k) {
        invalidate(k, CASCache.RemovalCause.EXPLICIT);
    }

    public void invalidate(K k, CASCache.RemovalCause cause) {
        int blockIndex = getBlockIndex(k);
        CacheBlock cacheBlock = blockList.get(blockIndex);
        CacheNode remove = cacheBlock.nodeMap.remove(k);
        if (remove != null) {
            cacheBlock.expireSet.remove(remove);
            onRemove(remove, cause);
        }
    }

    /** 强制过期所有 */
    public void invalidateAll() {
        invalidateAll(CASCache.RemovalCause.EXPIRE);
    }

    public void invalidateAll(CASCache.RemovalCause removalCause) {
        scheduledExecutorService.execute(() -> {
            for (CacheBlock cacheBlock : blockList) {
                Iterator<CacheNode> iterator = cacheBlock.expireSet.iterator();
                while (iterator.hasNext()) {
                    CacheNode cacheNode = iterator.next();
                    onRemove(cacheNode, removalCause);
                    iterator.remove();
                    cacheBlock.nodeMap.remove(cacheNode.key);
                }
            }
        });
    }

    /** 强制刷新，定时清理过期数据可能出现延迟，所以也可以手动调用清理 */
    public void cleanup() {
        scheduledExecutorService.execute(() -> onCleanup(CASCache.RemovalCause.EXPIRE));
    }

    public void cleanup(CASCache.RemovalCause removalCause) {
        scheduledExecutorService.execute(() -> onCleanup(removalCause));
    }

    private void onCleanup(CASCache.RemovalCause removalCause) {
        for (CacheBlock cacheBlock : blockList) {
            Iterator<CacheNode> iterator = cacheBlock.expireSet.iterator();
            while (iterator.hasNext()) {
                CacheNode cacheNode = iterator.next();
                if (cacheNode.expireTime > System.currentTimeMillis()) {
                    break;
                }
                if (onRemove(cacheNode, removalCause)) {
                    iterator.remove();
                    cacheBlock.nodeMap.remove(cacheNode.key);
                } else {
                    cacheBlock.expireSet.remove(cacheNode);
                    cacheNode.refresh(true);
                    cacheBlock.expireSet.add(cacheNode);
                }
            }
        }
    }

    private boolean onRemove(CacheNode cacheNode, CASCache.RemovalCause cause) {
        if (cacheNode != null) {
            if (removalListener != null) {
                return removalListener.test(cacheNode.key, cacheNode.value, cause);
            }
        }
        return true;
    }

}
