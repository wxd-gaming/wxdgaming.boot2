package code.cache;

import lombok.Builder;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.function.Consumer3;
import wxdgaming.boot2.core.util.AssertUtil;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * 缓存驱动
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-07-03 10:57
 **/
class CacheDriver<K, V> {

    static final ScheduledExecutorService scheduledExecutorService = ExecutorFactory.newSingleThreadScheduledExecutor("cache-scheduled");

    protected final ReentrantLock lock = new ReentrantLock();
    protected final int block;
    protected final List<CacheBlock> cacheAtomicReference;

    /** 读取过期时间 */
    protected final Duration expireAfterAccess;
    /** 写入过期时间 */
    protected final Duration expireAfterWrite;

    protected final Function<K, V> loader;
    protected final Consumer3<K, V, RemovalCause> removalListener;

    public enum RemovalCause {
        /** 删除 */
        DELETE,
        /** 替换 */
        REPLACED,
        /** 过期删除 */
        EXPIRE,
        /** 手动删除 */
        EXPLICIT,
        /** 特殊 */
        SPECIAL,
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
            refresh();
        }

        private void refresh() {
            lock.lock();
            try {
                if (expireAfterAccess != null || expireAfterWrite != null) {
                    if (expireAfterWrite != null) {
                        if (expireTime == 0) {
                            expireTime = System.currentTimeMillis() + expireAfterWrite.toMillis();
                        }
                    } else {
                        expireTime = System.currentTimeMillis() + expireAfterAccess.toMillis();
                    }
                }
            } finally {
                lock.unlock();
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

        final ReentrantLock lock = new ReentrantLock();
        final HashMap<K, CacheNode> nodeMap = new HashMap<>();
        final TreeSet<CacheNode> expireSet = new TreeSet<>();

        public void put(K key, V value) {
            lock.lock();
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
                lock.unlock();
            }
        }


        public V get(K key) {
            lock.lock();
            try {
                CacheNode cacheNode = nodeMap.get(key);
                if (cacheNode == null) {
                    V value = null;
                    if (loader != null)
                        value = loader.apply(key);
                    if (value == null)
                        return null;
                    cacheNode = new CacheNode(key, value);
                    nodeMap.put(key, cacheNode);
                    expireSet.add(cacheNode);
                } else {
                    if (expireAfterWrite == null) {
                        /*TODO 固定缓存不需要刷新，因为时间不会边*/
                        expireSet.remove(cacheNode);
                        cacheNode.refresh();
                        expireSet.add(cacheNode);
                    }
                }
                return cacheNode.value;
            } finally {
                lock.unlock();
            }
        }

        public Collection<V> values() {
            lock.lock();
            try {
                return nodeMap.values().stream().map(n -> n.value).toList();
            } finally {
                lock.unlock();
            }
        }

    }

    @Builder
    public CacheDriver(int block, Duration expireAfterAccess, Duration expireAfterWrite, Function<K, V> loader, Consumer3<K, V, RemovalCause> removalListener) {

        if (expireAfterAccess != null && expireAfterWrite != null)
            throw new RuntimeException("expireAfterAccess or expireAfterWrite");

        this.block = block;
        this.expireAfterAccess = expireAfterAccess;
        this.expireAfterWrite = expireAfterWrite;
        this.loader = loader;
        this.removalListener = removalListener;

        cacheAtomicReference = new ArrayList<>(block);
        for (int i = 0; i < block; i++) {
            cacheAtomicReference.add(new CacheBlock());
        }

        if (expireAfterAccess != null || expireAfterWrite != null) {
            long delay = expireAfterAccess != null ? expireAfterAccess.toMillis() : expireAfterWrite.toMillis();
            if (delay < 1000)
                throw new RuntimeException("expire < 1s");
            delay = delay / 100;
            scheduledExecutorService.scheduleWithFixedDelay(this::refresh, delay, delay, TimeUnit.MILLISECONDS);
        }
    }

    private int getBlockIndex(K key) {
        return Math.abs(key.hashCode()) % block;
    }

    public void put(K key, V value) {
        int blockIndex = getBlockIndex(key);
        cacheAtomicReference.get(blockIndex).put(key, value);
    }

    public V get(K key) {
        int blockIndex = getBlockIndex(key);
        return cacheAtomicReference.get(blockIndex).get(key);
    }

    public V remove(K k) {
        return remove(k, RemovalCause.EXPLICIT);
    }

    public V remove(K k, RemovalCause cause) {
        int blockIndex = getBlockIndex(k);
        CacheBlock cacheBlock = cacheAtomicReference.get(blockIndex);
        cacheBlock.lock.lock();
        try {
            CacheNode remove = cacheBlock.nodeMap.remove(k);
            if (remove != null) {
                cacheBlock.expireSet.remove(remove);
                onRemove(remove, cause);
                return remove.value;
            }
            return null;
        } finally {
            cacheBlock.lock.unlock();
        }
    }

    /** 强制刷新，定时清理过期数据可能出现延迟，所以也可以手动调用清理 */
    public void refresh() {
        lock.lock();
        try {
            for (CacheBlock cacheBlock : cacheAtomicReference) {
                cacheBlock.lock.lock();
                try {
                    Iterator<CacheNode> iterator = cacheBlock.expireSet.iterator();
                    while (iterator.hasNext()) {
                        CacheNode cacheNode = iterator.next();
                        if (cacheNode.expireTime > System.currentTimeMillis()) {
                            break;
                        }
                        iterator.remove();
                        cacheBlock.nodeMap.remove(cacheNode.key);
                        onRemove(cacheNode, RemovalCause.EXPIRE);
                    }
                } finally {
                    cacheBlock.lock.unlock();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private void onRemove(CacheNode cacheNode, RemovalCause cause) {
        if (cacheNode != null) {
            if (removalListener != null) {
                removalListener.accept(cacheNode.key, cacheNode.value, cause);
            }
        }
    }

}
