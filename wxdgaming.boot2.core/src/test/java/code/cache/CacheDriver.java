package code.cache;

import lombok.Builder;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.function.Predicate3;
import wxdgaming.boot2.core.util.AssertUtil;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

/**
 * 缓存驱动
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-07-03 10:57
 **/
class CacheDriver<K, V> {

    static final ScheduledExecutorService scheduledExecutorService = ExecutorFactory.newSingleThreadScheduledExecutor("cache-scheduled");

    /** 容器驱动锁 */
    protected final ReentrantLock driverLock = new ReentrantLock();
    protected final int blockSize;
    protected List<CacheBlock> cacheAtomicReference;

    /** 读取过期时间 */
    protected final Duration expireAfterAccess;
    /** 写入过期时间 */
    protected final Duration expireAfterWrite;

    protected final Function<K, V> loader;
    protected final Predicate3<K, V, RemovalCause> removalListener;

    public enum RemovalCause {
        /** 替换 */
        REPLACED,
        /** 过期删除 */
        EXPIRE,
        /** 手动删除 */
        EXPLICIT,
        ;
    }

    protected class CacheNode implements Comparable<CacheNode> {

        private final K key;
        private final V value;
        private long expireTime;

        public CacheNode(Lock selfLock, K key, V value) {
            AssertUtil.isNull(key, "value is null");
            AssertUtil.isNull(value, "value is null");
            this.key = key;
            this.value = value;
            refresh(selfLock, false);
        }

        private void refresh(Lock selfLock, boolean force) {
//            selfLock.lock();
            try {
                if (expireAfterAccess != null || expireAfterWrite != null) {
                    if (expireAfterWrite != null) {
                        if (expireTime == 0 || force) {
                            expireTime = System.currentTimeMillis() + expireAfterWrite.toMillis();
                        }
                    } else {
                        expireTime = System.currentTimeMillis() + expireAfterAccess.toMillis();
                    }
                }
            } finally {
//                selfLock.unlock();
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

    protected class CacheBlock {
        /** 区块锁 */
        final ReentrantReadWriteLock blockLock = new ReentrantReadWriteLock();
        final ReentrantReadWriteLock.ReadLock blockReadLock = blockLock.readLock();
        final ReentrantReadWriteLock.WriteLock blockWriteLock = blockLock.writeLock();
        final HashMap<K, CacheNode> nodeMap = new HashMap<>();
        final TreeSet<CacheNode> expireSet = new TreeSet<>();

        public void put(K key, V value) {
            blockWriteLock.lock();
            try {
                CacheNode newNode = new CacheNode(blockReadLock, key, value);
                CacheNode oldNode = nodeMap.put(key, newNode);
                if (oldNode != null) {
                    expireSet.remove(oldNode);
                    if (!Objects.equals(oldNode.value, value)) {
                        onRemove(oldNode, RemovalCause.REPLACED);
                    }
                }
                expireSet.add(newNode);
            } finally {
                blockWriteLock.unlock();
            }
        }


        public V get(K key) {
            blockReadLock.lock();
            CacheNode cacheNode = null;
            try {
                cacheNode = nodeMap.get(key);
            } finally {
                blockReadLock.unlock();
            }
            if (cacheNode == null) {
                V value = null;
                if (loader == null)
                    return null;
                blockWriteLock.lock();
                try {
                    value = loader.apply(key);
                    if (value == null)
                        return null;
                    put(key, value);
                    return value;
                } finally {
                    blockWriteLock.unlock();
                }
            } else {
                if (expireAfterWrite == null) {
                    /*TODO 固定缓存不需要刷新，因为时间不会边*/
                    expireSet.remove(cacheNode);
                    cacheNode.refresh(blockReadLock, false);
                    expireSet.add(cacheNode);
                }
                return cacheNode.value;
            }
        }

        public int size() {
            return nodeMap.size();
        }

        public Collection<V> values() {
            blockReadLock.lock();
            try {
                return nodeMap.values().stream().map(n -> n.value).toList();
            } finally {
                blockReadLock.unlock();
            }
        }

    }

    @Builder
    public CacheDriver(int blockSize, Duration expireAfterAccess, Duration expireAfterWrite, Function<K, V> loader, Predicate3<K, V, RemovalCause> removalListener) {

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
        cacheAtomicReference = new ArrayList<>(blockSize);
        for (int i = 0; i < this.blockSize; i++) {
            cacheAtomicReference.add(new CacheBlock());
        }

        if (expireAfterAccess != null || expireAfterWrite != null) {
            long delay = expireAfterAccess != null ? expireAfterAccess.toMillis() : expireAfterWrite.toMillis();
            if (delay < 1000)
                throw new RuntimeException("expire < 1s");
            delay = delay / 100;
            scheduledExecutorService.scheduleWithFixedDelay(this::onCleanup, delay, delay, TimeUnit.MILLISECONDS);
        } else {
            AssertUtil.notNull(this.removalListener, "removalListener 非空 请配置过期时间");
        }
    }

    private int getBlockIndex(K key) {
        return Math.abs(key.hashCode()) % blockSize;
    }

    public long size() {
        return cacheAtomicReference.stream().mapToLong(CacheBlock::size).sum();
    }

    public void put(K key, V value) {
        int blockIndex = getBlockIndex(key);
        cacheAtomicReference.get(blockIndex).put(key, value);
    }

    public V get(K key) {
        int blockIndex = getBlockIndex(key);
        return cacheAtomicReference.get(blockIndex).get(key);
    }

    public void invalidate(K k) {
        invalidate(k, RemovalCause.EXPLICIT);
    }

    public void invalidate(K k, RemovalCause cause) {
        scheduledExecutorService.execute(() -> {
            int blockIndex = getBlockIndex(k);
            CacheBlock cacheBlock = cacheAtomicReference.get(blockIndex);
            cacheBlock.blockWriteLock.lock();
            try {
                CacheNode remove = cacheBlock.nodeMap.remove(k);
                if (remove != null) {
                    cacheBlock.expireSet.remove(remove);
                    onRemove(remove, cause);
                }
            } finally {
                cacheBlock.blockWriteLock.unlock();
            }
        });
    }

    /** 强制过期所有 */
    public void invalidateAll() {
        scheduledExecutorService.execute(() -> {
            driverLock.lock();
            try {
                for (CacheBlock cacheBlock : cacheAtomicReference) {
                    cacheBlock.blockWriteLock.lock();
                    try {
                        Iterator<CacheNode> iterator = cacheBlock.expireSet.iterator();
                        while (iterator.hasNext()) {
                            CacheNode cacheNode = iterator.next();
                            onRemove(cacheNode, RemovalCause.EXPIRE);
                            iterator.remove();
                            cacheBlock.nodeMap.remove(cacheNode.key);
                        }
                    } finally {
                        cacheBlock.blockWriteLock.unlock();
                    }
                }
            } finally {
                driverLock.unlock();
            }
        });
    }

    /** 强制刷新，定时清理过期数据可能出现延迟，所以也可以手动调用清理 */
    public void cleanup() {
        scheduledExecutorService.execute(this::onCleanup);
    }

    private void onCleanup() {
        driverLock.lock();
        try {
            for (CacheBlock cacheBlock : cacheAtomicReference) {
                cacheBlock.blockWriteLock.lock();
                try {
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
                            cacheNode.refresh(cacheBlock.blockReadLock, true);
                            cacheBlock.expireSet.add(cacheNode);
                        }
                    }
                } finally {
                    cacheBlock.blockWriteLock.unlock();
                }
            }
        } finally {
            driverLock.unlock();
        }
    }

    private boolean onRemove(CacheNode cacheNode, RemovalCause cause) {
        if (cacheNode != null) {
            if (removalListener != null && !cause.equals(RemovalCause.REPLACED)) {
                return removalListener.test(cacheNode.key, cacheNode.value, cause);
            }
        }
        return true;
    }

}
