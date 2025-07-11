package wxdgaming.boot2.core.collection.concurrent;


import com.alibaba.fastjson.annotation.JSONType;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;

/** 线程安全的 */
@Getter
@Setter
@JSONType(seeAlso = {ConcurrentHashMap.class})
public class ConcurrentObjIntMap<K> extends ConcurrentHashMap<K, Integer> implements Map<K, Integer> {

    public int getCount(K key) {
        return this.getOrDefault(key, 0);
    }

    public int putCount(K key, int newValue) {
        return getAndUpdate(key, x -> newValue);
    }

    public int sum() {
        return this.values().stream().mapToInt(Integer::intValue).sum();
    }

    /** 获取到最新的数据 */
    public int incrementAndGet(K key) {
        return addAndGet(key, 1);
    }

    /** 获取到最新的数据 */
    public int decrementAndGet(K key) {
        return addAndGet(key, -1);
    }

    /** 获取到最新的数据 */
    public int addAndGet(K key, int delta) {
        return accumulateAndGet(key, delta, Math::addExact);
    }

    /** 返回老数据，更新新数据 */
    public int getAndIncrement(K key) {
        return getAndAdd(key, 1);
    }

    /** 返回老数据，更新新数据 */
    public int getAndDecrement(K key) {
        return getAndAdd(key, -1);
    }

    /** 返回老数据，更新新数据 */
    public int getAndAdd(K key, int delta) {
        return getAndAccumulate(key, delta, Math::addExact);
    }

    /** 更新新数据 */
    public int updateAndGet(K key, IntUnaryOperator updaterFunction) {
        return this.compute(key, (k, value) -> updaterFunction.applyAsInt((value == null) ? 0 : value));
    }

    /** 返回老数据，更新新数据 */
    private int getAndUpdate(K key, IntUnaryOperator updaterFunction) {
        AtomicInteger holder = new AtomicInteger();
        this.compute(
                key,
                (k, value) -> {
                    int oldValue = (value == null) ? 0 : value;
                    holder.set(oldValue);
                    return updaterFunction.applyAsInt(oldValue);
                });
        return holder.get();
    }

    /** 拿到的新数据 */
    private int accumulateAndGet(K key, int x, IntBinaryOperator accumulatorFunction) {
        return updateAndGet(key, oldValue -> accumulatorFunction.applyAsInt(oldValue, x));
    }

    /** 拿到的是老数据 */
    private int getAndAccumulate(K key, int x, IntBinaryOperator accumulatorFunction) {
        return getAndUpdate(key, oldValue -> accumulatorFunction.applyAsInt(oldValue, x));
    }

    /** 当前值和最新值谁大，用谁 */
    public int max(K key, int value) {
        return super.merge(key, value, Math::max);
    }

    /** 当前值和最新值谁大，用谁 */
    public int min(K key, int value) {
        return super.merge(key, value, Math::min);
    }

    /** 重写了方法，获取的值，如果不存在返回 0 而不是null */
    @Override
    public Integer get(Object key) {
        return super.getOrDefault(key, 0);
    }

    @Override public String toString() {
        return FastJsonUtil.toJSONString(this);
    }

}
