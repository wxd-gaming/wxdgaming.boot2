package wxdgaming.boot2.core.collection.ints;

import com.alibaba.fastjson.annotation.JSONType;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

import java.util.HashMap;
import java.util.Optional;

/**
 * int int object table
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-08 19:08
 **/
@Getter
@Setter
@JSONType(seeAlso = {HashMap.class})
public class IntIntObjectTable<T> extends ObjectBase {

    private HashMap<Integer, HashMap<Integer, T>> nodes = new HashMap<>();

    public HashMap<Integer, T> row(int row) {
        return nodes.computeIfAbsent(row, k -> new HashMap<>());
    }

    public T put(int row, int col, T value) {
        return row(row).put(col, value);
    }

    public IntIntObjectTable<T> fluentPut(int row, int col, T value) {
        row(row).put(col, value);
        return this;
    }


    public HashMap<Integer, T> get(int row) {
        return nodes.get(row);
    }

    public T get(int row, int col) {
        return Optional.ofNullable(nodes.get(row)).map(rows -> rows.get(col)).orElse(null);
    }

    public boolean containsKey(int row) {
        return nodes.containsKey(row);
    }

    public boolean containsKey(int row, int col) {
        return Optional.ofNullable(nodes.get(row)).map(rows -> rows.containsKey(col)).orElse(false);
    }

}
