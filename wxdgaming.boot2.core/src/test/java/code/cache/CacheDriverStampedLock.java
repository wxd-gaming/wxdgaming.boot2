package code.cache;

import lombok.Builder;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.function.Predicate3;
import wxdgaming.boot2.core.util.AssertUtil;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Function;

/**
 * 缓存驱动
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-07-03 10:57
 **/
class CacheDriverStampedLock<K, V> {

    static final ScheduledExecutorService scheduledExecutorService = ExecutorFactory.newSingleThreadScheduledExecutor("cache-scheduled");

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

    protected class CacheBlock {
        /** 区块锁 */
        final StampedLock stampedLock = new StampedLock();
        final HashMap<K, CacheNode> nodeMap = new HashMap<>();
        final TreeSet<CacheNode> expireSet = new TreeSet<>();

        public void put(K key, V value) {
            long writeLock = stampedLock.writeLock();
            try {
                CacheNode newNode = new CacheNode(key, value);
                CacheNode oldNode = nodeMap.put(key, newNode);
                if (oldNode != null) {
                    expireSet.remove(oldNode);
                    if (!Objects.equals(oldNode.value, value)) {
                        onRemove(oldNode, RemovalCause.REPLACED);
                    }
                }
                expireSet.add(newNode);
            } finally {
                stampedLock.unlockWrite(writeLock);
            }
        }


        public V get(K key) {
            long readLock = stampedLock.tryOptimisticRead();
            CacheNode cacheNode = nodeMap.get(key);
            if (!stampedLock.validate(readLock)) {
                readLock = stampedLock.readLock(); // 退化为悲观读
                try {
                    cacheNode = nodeMap.get(key);
                } finally {
                    stampedLock.unlockRead(readLock);
                }
            }
            if (cacheNode == null) {
                if (loader == null)
                    return null;
                long writeLock = stampedLock.writeLock();
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
                    stampedLock.unlockWrite(writeLock);
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
            long optimisticRead = stampedLock.tryOptimisticRead();
            List<V> list = nodeMap.values().stream().map(n -> n.value).toList();
            if (!stampedLock.validate(optimisticRead)) {
                optimisticRead = stampedLock.readLock();
                try {
                    list = nodeMap.values().stream().map(n -> n.value).toList();
                } finally {
                    stampedLock.unlockRead(optimisticRead);
                }
            }
            return list;
        }

    }

    @Builder
    public CacheDriverStampedLock(int blockSize, Duration expireAfterAccess, Duration expireAfterWrite, Function<K, V> loader, Predicate3<K, V, RemovalCause> removalListener) {

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
            long writeLock = cacheBlock.stampedLock.writeLock();
            try {
                CacheNode remove = cacheBlock.nodeMap.remove(k);
                if (remove != null) {
                    cacheBlock.expireSet.remove(remove);
                    onRemove(remove, cause);
                }
            } finally {
                cacheBlock.stampedLock.unlockWrite(writeLock);
            }
        });
    }

    /** 强制过期所有 */
    public void invalidateAll() {
        scheduledExecutorService.execute(() -> {
            for (CacheBlock cacheBlock : cacheAtomicReference) {
                long writeLock = cacheBlock.stampedLock.writeLock();
                try {
                    Iterator<CacheNode> iterator = cacheBlock.expireSet.iterator();
                    while (iterator.hasNext()) {
                        CacheNode cacheNode = iterator.next();
                        onRemove(cacheNode, RemovalCause.EXPIRE);
                        iterator.remove();
                        cacheBlock.nodeMap.remove(cacheNode.key);
                    }
                } finally {
                    cacheBlock.stampedLock.unlockWrite(writeLock);
                }
            }
        });
    }

    /** 强制刷新，定时清理过期数据可能出现延迟，所以也可以手动调用清理 */
    public void cleanup() {
        scheduledExecutorService.execute(this::onCleanup);
    }

    private void onCleanup() {
        for (CacheBlock cacheBlock : cacheAtomicReference) {
            long writeLock = cacheBlock.stampedLock.writeLock();
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
                        cacheNode.refresh(true);
                        cacheBlock.expireSet.add(cacheNode);
                    }
                }
            } finally {
                cacheBlock.stampedLock.unlockWrite(writeLock);
            }
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
