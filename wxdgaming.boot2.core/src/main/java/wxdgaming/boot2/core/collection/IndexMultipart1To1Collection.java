package wxdgaming.boot2.core.collection;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 多重索引的1对1映射
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-12-08 20:32
 **/
public class IndexMultipart1To1Collection<E> {

    private final Map<String, Function<E, Keys>> indexMap = new HashMap<>();
    private final Map<String, Map<Keys, E>> indexDataMap = new HashMap<>();

    @SafeVarargs public final IndexMultipart1To1Collection<E> registerIndex(String name, Function<E, Object>... indexFunctions) {
        Function<E, Keys> indexFunction = new Function<E, Keys>() {
            @Override public Keys apply(E e) {
                Object[] keys = new Object[indexFunctions.length];
                for (int i = 0; i < indexFunctions.length; i++) {
                    keys[i] = indexFunctions[i].apply(e);
                }
                return new Keys(keys);
            }
        };
        indexMap.put(name, indexFunction);
        return this;
    }

    public IndexMultipart1To1Collection<E> add(E e) {
        for (Map.Entry<String, Function<E, Keys>> entry : indexMap.entrySet()) {
            Function<E, Keys> objectFunction = entry.getValue();
            Keys indexKey = objectFunction.apply(e);
            Map<Keys, E> objectListMap = indexDataMap.computeIfAbsent(entry.getKey(), l -> new HashMap<>());
            objectListMap.put(indexKey, e);
        }
        return this;
    }

    public IndexMultipart1To1Collection<E> addIfAbsent(E e) {
        for (Map.Entry<String, Function<E, Keys>> entry : indexMap.entrySet()) {
            Function<E, Keys> objectFunction = entry.getValue();
            Keys indexKey = objectFunction.apply(e);
            Map<Keys, E> objectListMap = indexDataMap.computeIfAbsent(entry.getKey(), l -> new HashMap<>());
            objectListMap.putIfAbsent(indexKey, e);
        }
        return this;
    }

    public IndexMultipart1To1Collection<E> remove(E e) {
        for (Map.Entry<String, Function<E, Keys>> entry : indexMap.entrySet()) {
            Function<E, Keys> objectFunction = entry.getValue();
            Keys indexKey = objectFunction.apply(e);
            Map<Keys, E> objectEMap = indexDataMap.get(entry.getKey());
            if (objectEMap == null) continue;
            objectEMap.remove(indexKey);
        }
        return this;
    }

    public E get(String indexName, Object... indexKeys) {
        Map<Keys, E> objectEMap = indexDataMap.get(indexName);
        if (objectEMap == null) return null;
        return objectEMap.get(new Keys(indexKeys));
    }

    private record Keys(Object... keys) {

        @Override public final boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Keys keys1 = (Keys) o;
            return Arrays.equals(keys, keys1.keys);
        }

        @Override public int hashCode() {
            return Arrays.hashCode(keys);
        }

    }

}
