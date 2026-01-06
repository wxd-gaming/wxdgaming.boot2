package wxdgaming.boot2.core.collection.longs;

import com.alibaba.fastjson2.annotation.JSONType;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

import java.util.HashMap;
import java.util.Optional;

/**
 * int long int table
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-08 19:08
 **/
@Getter
@Setter
@JSONType(seeAlso = {HashMap.class})
public class LongIntObjectTable<T> extends ObjectBase {

    private HashMap<Long, HashMap<Integer, T>> nodes = new HashMap<>();

    public HashMap<Integer, T> row(long row) {
        return nodes.computeIfAbsent(row, k -> new HashMap<>());
    }

    public T put(long row, int col, T value) {
        return row(row).put(col, value);
    }

    public LongIntObjectTable fluentPut(long row, int col, T value) {
        row(row).put(col, value);
        return this;
    }

    public HashMap<Integer, T> get(long row) {
        return nodes.get(row);
    }

    public T get(long row, int col) {
        return Optional.ofNullable(nodes.get(row)).map(rows -> rows.get(col)).orElse(null);
    }

    public boolean containsKey(long row) {
        return nodes.containsKey(row);
    }

    public boolean containsKey(long row, int col) {
        return Optional.ofNullable(nodes.get(row)).map(rows -> rows.containsKey(col)).orElse(false);
    }

}
