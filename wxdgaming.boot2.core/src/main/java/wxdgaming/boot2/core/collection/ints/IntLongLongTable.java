package wxdgaming.boot2.core.collection.ints;

import com.alibaba.fastjson.annotation.JSONType;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

import java.util.HashMap;
import java.util.Optional;

/**
 * int long long table
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-08 19:08
 **/
@Getter
@Setter
@JSONType(seeAlso = {HashMap.class})
public class IntLongLongTable extends ObjectBase {

    private HashMap<Integer, HashMap<Long, Long>> nodes = new HashMap<>();

    public HashMap<Long, Long> row(int row) {
        return nodes.computeIfAbsent(row, k -> new HashMap<>());
    }

    public Long put(int row, long col, long value) {
        return row(row).put(col, value);
    }

    public IntLongLongTable fluentPut(int row, long col, long value) {
        row(row).put(col, value);
        return this;
    }

    public Long merge(int row, long col, long value) {
        return row(row).merge(col, value, Math::addExact);
    }

    public HashMap<Long, Long> get(int row) {
        return nodes.get(row);
    }

    public Long get(int row, long col) {
        return Optional.ofNullable(nodes.get(row)).map(rows -> rows.get(col)).orElse(null);
    }

    public boolean containsKey(int row) {
        return nodes.containsKey(row);
    }

    public boolean containsKey(int row, long col) {
        return Optional.ofNullable(nodes.get(row)).map(rows -> rows.containsKey(col)).orElse(false);
    }

}
