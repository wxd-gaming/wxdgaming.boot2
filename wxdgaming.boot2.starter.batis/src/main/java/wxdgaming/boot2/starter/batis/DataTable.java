package wxdgaming.boot2.starter.batis;

import lombok.Getter;

import java.util.*;
import java.util.function.Function;

/**
 * 数据列表
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-28 09:53
 **/
@Getter
public class DataTable<T extends Entity> {

    final Class<T> cls;
    final DataHelper dataHelper;
    final Function<T, Object>[] keyFunctions;
    Map<Object, T> map = Map.of();

    @SafeVarargs public DataTable(Class<T> cls, DataHelper dataHelper, Function<T, Object>... keyFunctions) {
        this.cls = cls;
        this.dataHelper = dataHelper;
        this.keyFunctions = keyFunctions;
    }

    public void loadAll() {
        List<T> list = dataHelper.findList(cls);
        Map<Object, T> linkedHashMap = new LinkedHashMap<>();
        for (T t : list) {
            for (Function<T, Object> keyFunction : keyFunctions) {
                Object k = keyFunction.apply(t);
                if (k != null) {
                    linkedHashMap.put(k, t);
                }
            }
        }
        map = Collections.unmodifiableMap(linkedHashMap);
    }

    public Collection<T> getList() {
        return map.values();
    }

    public T get(Object k) {
        return map.get(k);
    }

}
