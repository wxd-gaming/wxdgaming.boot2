package wxdgaming.boot2.core.collection;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 带索引的集合
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-12-08 20:32
 **/
public class IndexCollection<E> {

    private final Map<String, Function<E, Object>> indexMap = new HashMap<>();
    private final Map<String, Map<Object, E>> indexDataMap = new HashMap<>();

    public IndexCollection<E> registerIndex(String name, Function<E, Object> indexFunction) {
        indexMap.put(name, indexFunction);
        return this;
    }

    public IndexCollection<E> add(E e) {
        for (Map.Entry<String, Function<E, Object>> entry : indexMap.entrySet()) {
            Function<E, Object> objectFunction = entry.getValue();
            Object indexKey = objectFunction.apply(e);
            indexDataMap.computeIfAbsent(entry.getKey(), l -> new HashMap<>()).put(indexKey, e);
        }
        return this;
    }

    public IndexCollection<E> addIfAbsent(E e) {
        for (Map.Entry<String, Function<E, Object>> entry : indexMap.entrySet()) {
            Function<E, Object> objectFunction = entry.getValue();
            Object indexKey = objectFunction.apply(e);
            indexDataMap.computeIfAbsent(entry.getKey(), l -> new HashMap<>()).putIfAbsent(indexKey, e);
        }
        return this;
    }

    public IndexCollection<E> remove(E e) {
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
