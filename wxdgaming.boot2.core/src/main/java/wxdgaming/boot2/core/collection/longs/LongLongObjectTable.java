package wxdgaming.boot2.core.collection.longs;

import com.alibaba.fastjson.annotation.JSONType;
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
@JSONType(seeAlso = {HashMap.class})
public class LongLongObjectTable<T> extends ObjectBase {

    private HashMap<Long, HashMap<Long, T>> nodes = new HashMap<>();

    public HashMap<Long, T> row(long row) {
        return nodes.computeIfAbsent(row, k -> new HashMap<>());
    }

    public T put(long row, long col, T value) {
        return row(row).put(col, value);
    }

    public LongLongObjectTable fluentPut(long row, long col, T value) {
        row(row).put(col, value);
        return this;
    }

    public HashMap<Long, T> get(long row) {
        return nodes.get(row);
    }

    public T get(long row, long col) {
        return Optional.ofNullable(nodes.get(row)).map(rows -> rows.get(col)).orElse(null);
    }

    public boolean containsKey(long row) {
        return nodes.containsKey(row);
    }

    public boolean containsKey(long row, long col) {
        return Optional.ofNullable(nodes.get(row)).map(rows -> rows.containsKey(col)).orElse(false);
    }

}
