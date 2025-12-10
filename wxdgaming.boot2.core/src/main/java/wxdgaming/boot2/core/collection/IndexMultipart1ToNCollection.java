package wxdgaming.boot2.core.collection;

import wxdgaming.boot2.core.lang.ComboKey;

import java.util.*;
import java.util.function.Function;

/**
 * 多重索引的1对多映射
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-12-08 20:32
 **/
public class IndexMultipart1ToNCollection<E> {

    private final Map<String, Function<E, ComboKey>> indexMap = new HashMap<>();
    private final Map<String, Map<ComboKey, List<E>>> indexDataMap = new HashMap<>();

    @SafeVarargs public final IndexMultipart1ToNCollection<E> registerIndex(String name, Function<E, Object>... indexFunctions) {
        Function<E, ComboKey> indexFunction = new Function<E, ComboKey>() {
            @Override public ComboKey apply(E e) {
                Object[] keys = new Object[indexFunctions.length];
                for (int i = 0; i < indexFunctions.length; i++) {
                    keys[i] = indexFunctions[i].apply(e);
                }
                return new ComboKey(keys);
            }
        };
        indexMap.put(name, indexFunction);
        return this;
    }

    public IndexMultipart1ToNCollection<E> add(E e) {
        for (Map.Entry<String, Function<E, ComboKey>> entry : indexMap.entrySet()) {
            Function<E, ComboKey> objectFunction = entry.getValue();
            ComboKey indexKey = objectFunction.apply(e);
            Map<ComboKey, List<E>> objectListMap = indexDataMap.computeIfAbsent(entry.getKey(), l -> new HashMap<>());
            List<E> es = objectListMap.computeIfAbsent(indexKey, l -> new ArrayList<>());
            if (es.contains(e)) continue;
            es.add(e);
        }
        return this;
    }

    public IndexMultipart1ToNCollection<E> remove(E e) {
        for (Map.Entry<String, Function<E, ComboKey>> entry : indexMap.entrySet()) {
            Function<E, ComboKey> objectFunction = entry.getValue();
            ComboKey indexKey = objectFunction.apply(e);
            Map<ComboKey, List<E>> objectEMap = indexDataMap.get(entry.getKey());
            if (objectEMap == null) continue;
            List<E> es = objectEMap.get(indexKey);
            if (es == null) continue;
            es.remove(e);
        }
        return this;
    }

    public List<E> get(String indexName, Object... indexKeys) {
        Map<ComboKey, List<E>> objectEMap = indexDataMap.get(indexName);
        if (objectEMap == null) return null;
        return objectEMap.get(new ComboKey(indexKeys));
    }

    public E getFist(String indexName, Object... indexKeys) {
        List<E> es = get(indexName, indexKeys);
        if (es == null) return null;
        return es.getFirst();
    }

    public E getLast(String indexName, Object... indexKeys) {
        List<E> es = get(indexName, indexKeys);
        if (es == null) return null;
        return es.getLast();
    }

}
