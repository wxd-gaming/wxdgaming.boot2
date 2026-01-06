package wxdgaming.boot2.core.collection.longs;

import com.alibaba.fastjson2.annotation.JSONType;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

import java.util.HashMap;
import java.util.Optional;

/**
 * long int int table
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-08 19:08
 **/
@Getter
@Setter
@JSONType(seeAlso = {HashMap.class})
public class LongIntLongTable extends ObjectBase {

    private HashMap<Long, HashMap<Integer, Long>> nodes = new HashMap<>();

    public HashMap<Integer, Long> row(long row) {
        return nodes.computeIfAbsent(row, k -> new HashMap<>());
    }

    public Long put(long row, int col, long value) {
        return row(row).put(col, value);
    }

    public LongIntLongTable fluentPut(long row, int col, long value) {
        row(row).put(col, value);
        return this;
    }

    public Long merge(long row, int col, long value) {
        return row(row).merge(col, value, Math::addExact);
    }

    public HashMap<Integer, Long> get(long row) {
        return nodes.get(row);
    }

    public Long get(long row, int col) {
        return Optional.ofNullable(nodes.get(row)).map(rows -> rows.get(col)).orElse(null);
    }

    public boolean containsKey(long row) {
        return nodes.containsKey(row);
    }

    public boolean containsKey(long row, int col) {
        return Optional.ofNullable(nodes.get(row)).map(rows -> rows.containsKey(col)).orElse(false);
    }

}
