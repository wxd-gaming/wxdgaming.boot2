package wxdgaming.boot2.core.collection.longs;

import com.alibaba.fastjson2.annotation.JSONType;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

import java.util.HashMap;
import java.util.Optional;

/**
 * long long long table
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-08 19:08
 **/
@Getter
@Setter
@JSONType(seeAlso = {HashMap.class})
public class LongLongLongTable extends ObjectBase {

    private HashMap<Long, HashMap<Long, Long>> nodes = new HashMap<>();

    public HashMap<Long, Long> row(long row) {
        return nodes.computeIfAbsent(row, k -> new HashMap<>());
    }

    public Long put(long row, long col, long value) {
        return row(row).put(col, value);
    }

    public LongLongLongTable fluentPut(long row, long col, long value) {
        row(row).put(col, value);
        return this;
    }

    public Long merge(long row, long col, long value) {
        return row(row).merge(col, value, Math::addExact);
    }

    public HashMap<Long, Long> get(long row) {
        return nodes.get(row);
    }

    public Long get(long row, long col) {
        return Optional.ofNullable(nodes.get(row)).map(rows -> rows.get(col)).orElse(null);
    }

    public boolean containsKey(long row) {
        return nodes.containsKey(row);
    }

    public boolean containsKey(long row, long col) {
        return Optional.ofNullable(nodes.get(row)).map(rows -> rows.containsKey(col)).orElse(false);
    }

}
