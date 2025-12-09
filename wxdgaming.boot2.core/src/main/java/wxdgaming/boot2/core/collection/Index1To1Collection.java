package wxdgaming.boot2.core.collection;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 单条件索引的1对1映射
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-12-08 20:32
 **/
public class Index1To1Collection<E> {

    private final Map<String, Function<E, Object>> indexMap = new HashMap<>();
    private final Map<String, Map<Object, E>> indexDataMap = new HashMap<>();

    public Index1To1Collection<E> registerIndex(String name, Function<E, Object> indexFunction) {
        indexMap.put(name, indexFunction);
        return this;
    }

    public Index1To1Collection<E> add(E e) {
        for (Map.Entry<String, Function<E, Object>> entry : indexMap.entrySet()) {
            Function<E, Object> objectFunction = entry.getValue();
            Object indexKey = objectFunction.apply(e);
            indexDataMap.computeIfAbsent(entry.getKey(), l -> new HashMap<>()).put(indexKey, e);
        }
        return this;
    }

    public Index1To1Collection<E> addIfAbsent(E e) {
        for (Map.Entry<String, Function<E, Object>> entry : indexMap.entrySet()) {
            Function<E, Object> objectFunction = entry.getValue();
            Object indexKey = objectFunction.apply(e);
            indexDataMap.computeIfAbsent(entry.getKey(), l -> new HashMap<>()).putIfAbsent(indexKey, e);
        }
        return this;
    }

    public Index1To1Collection<E> remove(E e) {
        for (Map.Entry<String, Function<E, Object>> entry : indexMap.entrySet()) {
            Function<E, Object> objectFunction = entry.getValue();
            Object indexKey = objectFunction.apply(e);
            Map<Object, E> objectEMap = indexDataMap.get(entry.getKey());
            if (objectEMap == null) continue;
            objectEMap.remove(indexKey);
        }
        return this;
    }

    public E get(String indexName, Object indexKey) {
        Map<Object, E> objectEMap = indexDataMap.get(indexName);
        if (objectEMap == null) return null;
        return objectEMap.get(indexKey);
    }

}
