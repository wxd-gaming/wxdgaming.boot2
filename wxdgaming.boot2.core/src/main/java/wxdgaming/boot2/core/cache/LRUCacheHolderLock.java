package wxdgaming.boot2.core.cache;

import lombok.Builder;
import wxdgaming.boot2.core.function.Predicate3;
import wxdgaming.boot2.core.locks.MonitorReadWrite;
import wxdgaming.boot2.core.util.AssertUtil;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 缓存驱动
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-07-03 10:57
 **/
class LRUCacheHolderLock<K, V> {

    protected final int blockSize;
    protected List<CacheBlock> blockList;
    protected ScheduledFuture<?> scheduledFuture;
    /** 读取过期时间 */
    protected final Duration expireAfterAccess;
    /** 写入过期时间 */
    protected final Duration expireAfterWrite;

    protected final Function<K, V> loader;
    protected final Predicate3<K, V, RemovalCause> removalListener;

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

    protected class CacheBlock extends MonitorReadWrite {
        /** 区块锁 */
        final HashMap<K, CacheNode> nodeMap = new HashMap<>();
        final ConcurrentSkipListSet<CacheNode> expireSet = new ConcurrentSkipListSet<>();

        public void put(K key, V value) {
            syncWrite(() -> {
                CacheNode newNode = new CacheNode(key, value);
                CacheNode oldNode = nodeMap.put(key, newNode);
                if (oldNode != null) {
                    expireSet.remove(oldNode);
                    if (!Objects.equals(oldNode.value, value)) {
                        onRemove(oldNode, RemovalCause.REPLACED);
                    }
                }
                expireSet.add(newNode);
            });
        }

        public boolean has(K key) {
            readLock();
            try {
                return nodeMap.containsKey(key);
            } finally {
                unReadLock();
            }
        }

        public V get(K key) {
            readLock();
            CacheNode cacheNode = null;
            try {
                cacheNode = nodeMap.get(key);
            } finally {
                unReadLock();
            }
            if (cacheNode == null) {
                if (loader == null)
                    return null;
                writeLock();
                try {
                    cacheNode = nodeMap.get(key);
                    if (cacheNode == null) {
                        V loadValue = loader.apply(key);
                        if (loadValue == null)
                            return null;
                        cacheNode = new CacheNode(key, loadValue);
                        expireSet.add(cacheNode);
                        nodeMap.put(key, cacheNode);
                        return loadValue;
                    }
                } finally {
                    unWriteLock();
                }
            }
            if (expireAfterWrite == null) {
                /*TODO 固定缓存不需要刷新，因为时间不会边*/
                expireSet.remove(cacheNode);
                cacheNode.refresh(false);
                expireSet.add(cacheNode);
            }
            return cacheNode.value;
        }

        public int size() {
            return nodeMap.size();
        }

        public Collection<V> values() {
            return supplierRead(() -> nodeMap.values().stream().map(n -> n.value).toList());
        }

    }

    @Builder
    public LRUCacheHolderLock(int blockSize, Duration expireAfterAccess, Duration expireAfterWrite, Function<K, V> loader, Predicate3<K, V, RemovalCause> removalListener) {

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
            scheduledFuture = CacheConst.scheduledExecutorService.scheduleWithFixedDelay(() -> onCleanup(RemovalCause.EXPIRE), delay, delay, TimeUnit.MILLISECONDS);
        } else {
            AssertUtil.notNull(this.removalListener, "removalListener 非空 请配置过期时间");
        }
    }

    /** 关闭缓存 */
    public void close() {
        if (scheduledFuture != null)
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

    public Collection<V> values() {
        return blockList.stream().flatMap(v -> v.values().stream()).toList();
    }

    public boolean has(K key) {
        int blockIndex = getBlockIndex(key);
        return blockList.get(blockIndex).has(key);
    }

    public void invalidate(K k) {
        invalidate(k, RemovalCause.EXPLICIT);
    }

    public void invalidate(K k, RemovalCause cause) {
        CacheConst.scheduledExecutorService.execute(() -> {
            int blockIndex = getBlockIndex(k);
            CacheBlock cacheBlock = blockList.get(blockIndex);
            cacheBlock.syncWrite(() -> {
                CacheNode remove = cacheBlock.nodeMap.remove(k);
                if (remove != null) {
                    cacheBlock.expireSet.remove(remove);
                    onRemove(remove, cause);
                }
            });
        });
    }

    /** 强制过期所有 */
    public void invalidateAll() {
        invalidateAll(RemovalCause.EXPIRE);
    }

    public void invalidateAll(RemovalCause removalCause) {
        CacheConst.scheduledExecutorService.execute(() -> {
            for (CacheBlock cacheBlock : blockList) {
                cacheBlock.syncWrite(() -> {
                    Iterator<CacheNode> iterator = cacheBlock.expireSet.iterator();
                    while (iterator.hasNext()) {
                        CacheNode cacheNode = iterator.next();
                        onRemove(cacheNode, removalCause);
                        iterator.remove();
                        cacheBlock.nodeMap.remove(cacheNode.key);
                    }
                });
            }
        });
    }

    /** 强制刷新，定时清理过期数据可能出现延迟，所以也可以手动调用清理 */
    public void cleanup() {
        CacheConst.scheduledExecutorService.execute(() -> onCleanup(RemovalCause.EXPIRE));
    }

    public void cleanup(RemovalCause removalCause) {
        CacheConst.scheduledExecutorService.execute(() -> onCleanup(removalCause));
    }

    private void onCleanup(RemovalCause removalCause) {
        for (CacheBlock cacheBlock : blockList) {
            cacheBlock.syncWrite(() -> {
                Iterator<CacheNode> iterator = cacheBlock.expireSet.iterator();
                while (iterator.hasNext()) {
                    CacheNode cacheNode = iterator.next();
                    if (cacheNode.expireTime > System.currentTimeMillis()) {
                        break;
                    }
                    if (onRemove(cacheNode, RemovalCause.EXPIRE)) {
                        iterator.remove();
                        cacheBlock.nodeMap.remove(cacheNode.key);
                    } else {
                        cacheBlock.expireSet.remove(cacheNode);
                        cacheNode.refresh(true);
                        cacheBlock.expireSet.add(cacheNode);
                    }
                }
            });
        }
    }

    private boolean onRemove(CacheNode cacheNode, RemovalCause cause) {
        if (cacheNode != null) {
            if (removalListener != null) {
                return removalListener.test(cacheNode.key, cacheNode.value, cause);
            }
        }
        return true;
    }

}
