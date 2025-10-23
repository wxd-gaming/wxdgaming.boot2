package wxdgaming.boot2.core.collection.concurrent;

import lombok.Getter;

import java.util.concurrent.ConcurrentHashMap;
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
public class ConcurrentMappingData<K1, K2, E> {

    public class MappingData<K> {
        /** 映射对象转换成key */
        private final Function<E, K> e2k;
        /** k1映射对象 */
        private final ConcurrentHashMap<K, E> keMapping = new ConcurrentHashMap<>();

        public MappingData(Function<E, K> e2k) {
            this.e2k = e2k;
        }

        /** 添加映射对象 */
        public void add(E e) {
            keMapping.put(e2k.apply(e), e);
        }

        /** 删除映射对象 */
        public void remove(E e) {
            keMapping.remove(e2k.apply(e));
        }

        /** 根据k删除 */
        public E removeByK(K k) {
            return keMapping.remove(k);
        }

        public boolean contains(E e) {
            return keMapping.containsKey(e2k.apply(e));
        }

        public boolean containsK(K k) {
            return keMapping.containsKey(k);
        }

        /** 根据k获取 */
        public E get(K k) {
            return keMapping.get(k);
        }

    }

    private final MappingData<K1> k1;
    private final MappingData<K2> k2;

    public ConcurrentMappingData(Function<E, K1> e2k1, Function<E, K2> e2k2) {
        k1 = new MappingData<>(e2k1);
        k2 = new MappingData<>(e2k2);
    }

    /** 添加映射对象 */
    public void add(E e) {
        k1.add(e);
        k2.add(e);
    }

    /** 删除映射对象 */
    public void remove(E e) {
        k1.remove(e);
        k2.remove(e);
    }

    /** 根据k1删除 */
    public E removeByK1(K1 k1) {
        E remove = this.k1.removeByK(k1);
        if (remove != null) {
            k2.remove(remove);
        }
        return remove;
    }

    /** 根据k2删除 */
    public E removeByK2(K2 k2) {
        E removeByK = this.k2.removeByK(k2);
        if (removeByK != null) {
            k1.remove(removeByK);
        }
        return removeByK;
    }

}
