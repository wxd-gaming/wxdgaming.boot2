package wxdgaming.boot2.core.collection;


import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.json.FastJsonUtil;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;

/** 非线程安全的 */
@Getter
@Setter
public class Object2LongMap<K> implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    final HashMap<K, Long> map = new HashMap<>();

    /** 会覆盖数据 */
    public Long put(K key, long newValue) {
        return map.put(key, newValue);
    }

    public long sum() {
        return map.values().stream().mapToLong(Long::longValue).sum();
    }

    /** 获取到最新的数据 +1 */
    public long incrementAndGet(K key) {
        return addAndGet(key, 1L);
    }

    /** 获取到最新的数据 -1 */
    public long decrementAndGet(K key) {
        return addAndGet(key, -1L);
    }

    /** 获取到最新的数据 */
    public long addAndGet(K key, long delta) {
        return accumulateAndGet(key, delta, Math::addExact);
    }

    /** 获取到的是老数据 */
    public long getAndIncrement(K key) {
        return getAndAdd(key, 1L);
    }

    /** 获取到的是老数据 */
    public long getAndDecrement(K key) {
        return getAndAdd(key, -1L);
    }

    /** 获取到的是老数据 */
    public long getAndAdd(K key, long delta) {
        return getAndAccumulate(key, delta, Math::addExact);
    }

    /** 获取到最新的数据 */
    public long updateAndGet(K key, LongUnaryOperator updaterFunction) {
        return map.compute(key, (k, value) -> updaterFunction.applyAsLong((value == null) ? 0L : value));
    }

    private long getAndUpdate(K key, LongUnaryOperator updaterFunction) {
        AtomicLong holder = new AtomicLong();
        map.compute(
                key,
                (k, value) -> {
                    long oldValue = (value == null) ? 0L : value;
                    holder.set(oldValue);
                    return updaterFunction.applyAsLong(oldValue);
                });
        return holder.get();
    }

    private long accumulateAndGet(K key, long x, LongBinaryOperator accumulatorFunction) {
        return updateAndGet(key, oldValue -> accumulatorFunction.applyAsLong(oldValue, x));
    }

    private long getAndAccumulate(K key, long x, LongBinaryOperator accumulatorFunction) {
        return getAndUpdate(key, oldValue -> accumulatorFunction.applyAsLong(oldValue, x));
    }

    /** 当前值和最新值谁大，用谁 */
    public long max(K key, long value) {
        return map.merge(key, value, Math::max);
    }

    /** 当前值和最新值谁小，用谁 */
    public long min(K key, long value) {
        return map.merge(key, value, Math::min);
    }

    public long getCount(K key) {
        return map.getOrDefault(key, 0L);
    }

    /** 重写了方法，获取的值，如果不存在返回 0 而不是null */
    public Long get(K key) {
        return map.getOrDefault(key, 0L);
    }

    @Override public String toString() {
        return FastJsonUtil.toJSONString(this);
    }

}
