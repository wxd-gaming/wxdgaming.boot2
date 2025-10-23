package wxdgaming.boot2.core.collection.concurrent;

import lombok.Getter;

import java.util.function.Function;

/**
 * 双映射数据
 *
 * @param <K1> 第一个映射key
 * @param <K2> 第二个映射key
 * @param <E>  映射对象
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-23 09:10
 */
@Getter
public class ConcurrentMappingData3<K1, K2, K3, E> extends ConcurrentMappingData<K1, K2, E> {

    private final MappingData<K3> k3;

    public ConcurrentMappingData3(Function<E, K1> e2k1, Function<E, K2> e2k2, Function<E, K3> e2k3) {
        super(e2k1, e2k2);
        this.k3 = this.new MappingData<K3>(e2k3);
    }

    @Override public void add(E e) {
        super.add(e);
        k3.add(e);
    }

    @Override public void remove(E e) {
        super.remove(e);
        k3.remove(e);
    }

    @Override public E removeByK1(K1 k1) {
        E removeByK1 = super.removeByK1(k1);
        if (removeByK1 != null) {
            k3.remove(removeByK1);
        }
        return removeByK1;
    }

    @Override public E removeByK2(K2 k2) {
        E removeByK2 = super.removeByK2(k2);
        if (removeByK2 != null)
            k3.remove(removeByK2);
        return removeByK2;
    }

    public E removeByK3(K3 k) {
        E removeByK = k3.removeByK(k);
        if (removeByK != null)
            super.remove(removeByK);
        return removeByK;
    }

}
