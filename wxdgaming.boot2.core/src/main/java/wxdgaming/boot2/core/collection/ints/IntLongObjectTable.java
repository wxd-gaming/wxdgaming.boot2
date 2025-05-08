package wxdgaming.boot2.core.collection.ints;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

import java.util.HashMap;
import java.util.Optional;

/**
 * int long int table
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-08 19:08
 **/
@Getter
@Setter
public class IntLongObjectTable<T> extends ObjectBase {

    private final HashMap<Integer, HashMap<Long, T>> nodes = new HashMap<>();

    public HashMap<Long, T> row(int row) {
        return nodes.computeIfAbsent(row, k -> new HashMap<>());
    }

    public T put(int row, long col, T value) {
        return row(row).put(col, value);
    }

    public IntLongObjectTable fluentPut(int row, long col, T value) {
        row(row).put(col, value);
        return this;
    }


    public HashMap<Long, T> get(int row) {
        return nodes.get(row);
    }

    public T get(int row, long col) {
        return Optional.ofNullable(nodes.get(row)).map(rows -> rows.get(col)).orElse(null);
    }

    public boolean containsKey(int row) {
        return nodes.containsKey(row);
    }

    public boolean containsKey(int row, long col) {
        return Optional.ofNullable(nodes.get(row)).map(rows -> rows.containsKey(col)).orElse(false);
    }

}
