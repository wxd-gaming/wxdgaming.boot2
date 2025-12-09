package wxdgaming.boot2.core.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 单条件索引的1对多映射
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-12-08 20:32
 **/
public class Index1ToNCollection<E> {

    private final Map<String, Function<E, Object>> indexMap = new HashMap<>();
    private final Map<String, Map<Object, List<E>>> indexDataMap = new HashMap<>();

    public Index1ToNCollection<E> registerIndex(String name, Function<E, Object> indexFunction) {
        indexMap.put(name, indexFunction);
        return this;
    }

    public Index1ToNCollection<E> add(E e) {
        for (Map.Entry<String, Function<E, Object>> entry : indexMap.entrySet()) {
            Function<E, Object> objectFunction = entry.getValue();
            Object indexKey = objectFunction.apply(e);
            Map<Object, List<E>> objectListMap = indexDataMap.computeIfAbsent(entry.getKey(), l -> new HashMap<>());
            List<E> es = objectListMap.computeIfAbsent(indexKey, l -> new ArrayList<>());
            if (es.contains(e)) continue;
            es.add(e);
        }
        return this;
    }

    public Index1ToNCollection<E> remove(E e) {
        for (Map.Entry<String, Function<E, Object>> entry : indexMap.entrySet()) {
            Function<E, Object> objectFunction = entry.getValue();
            Object indexKey = objectFunction.apply(e);
            Map<Object, List<E>> objectEMap = indexDataMap.get(entry.getKey());
            if (objectEMap == null) continue;
            List<E> es = objectEMap.get(indexKey);
            if (es == null) continue;
            es.remove(e);
        }
        return this;
    }

    public List<E> get(String indexName, Object indexKey) {
        Map<Object, List<E>> objectEMap = indexDataMap.get(indexName);
        if (objectEMap == null) return null;
        return objectEMap.get(indexKey);
    }

    public E getFist(String indexName, Object indexKey) {
        Map<Object, List<E>> objectEMap = indexDataMap.get(indexName);
        if (objectEMap == null) return null;
        List<E> es = objectEMap.get(indexKey);
        if (es == null) return null;
        return es.getFirst();
    }

    public E getLast(String indexName, Object indexKey) {
        Map<Object, List<E>> objectEMap = indexDataMap.get(indexName);
        if (objectEMap == null) return null;
        List<E> es = objectEMap.get(indexKey);
        if (es == null) return null;
        return es.getLast();
    }

}
