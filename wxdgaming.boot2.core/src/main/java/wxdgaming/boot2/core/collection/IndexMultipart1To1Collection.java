package wxdgaming.boot2.core.collection;

import wxdgaming.boot2.core.lang.ComboKey;

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

    private final Map<String, Function<E, ComboKey>> indexMap = new HashMap<>();
    private final Map<String, Map<ComboKey, E>> indexDataMap = new HashMap<>();

    @SafeVarargs public final IndexMultipart1To1Collection<E> registerIndex(String name, Function<E, Object>... indexFunctions) {
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

    public IndexMultipart1To1Collection<E> add(E e) {
        for (Map.Entry<String, Function<E, ComboKey>> entry : indexMap.entrySet()) {
            Function<E, ComboKey> objectFunction = entry.getValue();
            ComboKey indexKey = objectFunction.apply(e);
            Map<ComboKey, E> objectListMap = indexDataMap.computeIfAbsent(entry.getKey(), l -> new HashMap<>());
            objectListMap.put(indexKey, e);
        }
        return this;
    }

    public IndexMultipart1To1Collection<E> addIfAbsent(E e) {
        for (Map.Entry<String, Function<E, ComboKey>> entry : indexMap.entrySet()) {
            Function<E, ComboKey> objectFunction = entry.getValue();
            ComboKey indexKey = objectFunction.apply(e);
            Map<ComboKey, E> objectListMap = indexDataMap.computeIfAbsent(entry.getKey(), l -> new HashMap<>());
            objectListMap.putIfAbsent(indexKey, e);
        }
        return this;
    }

    public IndexMultipart1To1Collection<E> remove(E e) {
        for (Map.Entry<String, Function<E, ComboKey>> entry : indexMap.entrySet()) {
            Function<E, ComboKey> objectFunction = entry.getValue();
            ComboKey indexKey = objectFunction.apply(e);
            Map<ComboKey, E> objectEMap = indexDataMap.get(entry.getKey());
            if (objectEMap == null) continue;
            objectEMap.remove(indexKey);
        }
        return this;
    }

    public E get(String indexName, Object... indexComboKey) {
        Map<ComboKey, E> objectEMap = indexDataMap.get(indexName);
        if (objectEMap == null) return null;
        return objectEMap.get(new ComboKey(indexComboKey));
    }

}
