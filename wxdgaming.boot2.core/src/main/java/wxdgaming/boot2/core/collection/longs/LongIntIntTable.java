package wxdgaming.boot2.core.collection.longs;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

import java.util.HashMap;
import java.util.Optional;

/**
 * long int int table
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-08 19:08
 **/
@Getter
@Setter
public class LongIntIntTable extends ObjectBase {

    private final HashMap<Long, HashMap<Integer, Integer>> nodes = new HashMap<>();

    public HashMap<Integer, Integer> row(long row) {
        return nodes.computeIfAbsent(row, k -> new HashMap<>());
    }

    public Integer put(long row, int col, int value) {
        return row(row).put(col, value);
    }

    public LongIntIntTable fluentPut(long row, int col, int value) {
        row(row).put(col, value);
        return this;
    }

    public Integer merge(long row, int col, int value) {
        return row(row).merge(col, value, Math::addExact);
    }

    public HashMap<Integer, Integer> get(long row) {
        return nodes.get(row);
    }

    public Integer get(long row, int col) {
        return Optional.ofNullable(nodes.get(row)).map(rows -> rows.get(col)).orElse(null);
    }

    public boolean containsKey(long row) {
        return nodes.containsKey(row);
    }

    public boolean containsKey(long row, int col) {
        return Optional.ofNullable(nodes.get(row)).map(rows -> rows.containsKey(col)).orElse(false);
    }

}
