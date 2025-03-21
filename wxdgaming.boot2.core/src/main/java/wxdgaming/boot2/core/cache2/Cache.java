package wxdgaming.boot2.core.cache2;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import wxdgaming.boot2.core.function.Consumer2;
import wxdgaming.boot2.core.function.Function1;
import wxdgaming.boot2.core.function.Function2;
import wxdgaming.boot2.core.threading.TimerJob;
import wxdgaming.boot2.core.util.AssertUtil;

import java.util.Collection;

/**
 * cache
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-03-20 16:05
 **/
@SuperBuilder
public abstract class Cache<K, V> {

    @Getter protected String cacheName;
    @Builder.Default
    protected int area = 1;
    protected Function1<K, V> loader;
    /** 返回true表示可以删除，如果返回false表示当前不能删除 */
    protected Function2<K, V, Boolean> removalListener;
    /** 访问过期，相当于滑动缓存 */
    protected long expireAfterReadMs;
    /** 写入过期，相当于固定缓存 */
    protected long expireAfterWriteMs;
    @Builder.Default
    protected long heartTimeMs = 1000;
    protected Consumer2<K, V> heartListener;
    protected TimerJob[] timerJobs = null;

    public void start() {
        AssertUtil.assertTrue(this.area > 0, "area must > 0");
        if (this.heartTimeMs < 100) {
            throw new RuntimeException("heartTime must > 100");
        }
        if ((this.expireAfterReadMs > 0 && this.expireAfterWriteMs > 0) || this.expireAfterReadMs < 1 && this.expireAfterWriteMs < 1) {
            throw new RuntimeException("只能选择 expireAfterRead or expireAfterWrite 其中一个方式");
        }
        if (this.heartTimeMs > expireAfterWriteMs && this.heartTimeMs > expireAfterReadMs) {
            throw new RuntimeException("心跳时间必须小于过期时间");
        }
        AssertUtil.assertTrue(this.timerJobs == null, "重复调用 start");
    }

    /** 关闭缓存 */
    public abstract void shutdown();

    /** 计算内存大小 注意特别耗时，并且可能死循环 */
    @Deprecated
    public long memorySize() {
        throw new UnsupportedOperationException("不支持");
    }

    protected int hashIndex(K k) {
        int i = k.hashCode();
        int h = 0;
        if (area > 1) {
            h = i % area;
        }
        /*不需要负数*/
        return Math.abs(h);
    }

    /** 检查是否包含一个元素 */
    public abstract boolean has(K k);

    public abstract V get(K k) throws NullPointerException;

    /** 获取一个元素如果存在 */
    public abstract V getIfPresent(K k);

    /** 添加一个缓存 */
    public abstract V put(K k, V v);

    /** 当缓存不存在是添加 */
    public abstract V putIfAbsent(K k, V v);

    /** 强制过期单个缓存 */
    public abstract V invalidate(K k);

    /** 强制过期所有缓存 */
    public abstract void invalidateAll();

    /** 获取所有缓存 */
    public abstract Collection<K> keys();

    /** 获取所有缓存 */
    public abstract Collection<V> values();

    public abstract long size();

    /** 丢弃当前缓存，直接从内存删除删除很危险的 */
    @Deprecated
    public abstract void discard(K k);

    /** 丢弃当前缓存，直接从内存删除删除很危险的 */
    @Deprecated
    public abstract void discardAll();

    @Override public String toString() {
        return cacheName;
    }

}
