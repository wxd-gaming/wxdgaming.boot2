package wxdgaming.boot2.core.collection.ints;

import com.alibaba.fastjson.annotation.JSONType;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

import java.util.HashMap;
import java.util.Optional;

/**
 * int int int table
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-08 19:08
 **/
@Getter
@Setter
@JSONType(seeAlso = {HashMap.class})
public class IntIntIntTable extends ObjectBase {

    private HashMap<Integer, HashMap<Integer, Integer>> nodes = new HashMap<>();

    public HashMap<Integer, Integer> row(int row) {
        return nodes.computeIfAbsent(row, k -> new HashMap<>());
    }

    public Integer put(int row, int col, int value) {
        return row(row).put(col, value);
    }

    public IntIntIntTable fluentPut(int row, int col, int value) {
        row(row).put(col, value);
        return this;
    }

    public Integer merge(int row, int col, int value) {
        return row(row).merge(col, value, Math::addExact);
    }

    public HashMap<Integer, Integer> get(int row) {
        return nodes.get(row);
    }

    public Integer get(int row, int col) {
        return Optional.ofNullable(nodes.get(row)).map(rows -> rows.get(col)).orElse(null);
    }

    public boolean containsKey(int row) {
        return nodes.containsKey(row);
    }

    public boolean containsKey(int row, int col) {
        return Optional.ofNullable(nodes.get(row)).map(rows -> rows.containsKey(col)).orElse(false);
    }

}
