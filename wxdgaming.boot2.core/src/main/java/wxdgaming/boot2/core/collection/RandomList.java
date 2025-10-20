package wxdgaming.boot2.core.collection;

import lombok.Getter;
import wxdgaming.boot2.core.util.RandomUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 随机列表
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-16 14:40
 **/
@Getter
public class RandomList<T> implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    /**
     * <p> key: 数据
     * <p> value: 数据在list中的索引
     */
    private final Map<T, Integer> map = new HashMap<>();
    /**
     * 用于操作的数据，因为random 要快速高效，数组下标是最高效的
     */
    private final List<T> list = ListOf.newArrayList();

    public void add(T t) {
        if (!map.containsKey(t)) {
            map.put(t, list.size());
            list.add(t);
        }
    }

    public void remove(T t) {
        Integer index = map.remove(t);
        if (index == null) return;
        if (index < list.size() - 1) {
            /*TODO 如果删除的元素并非最后一个，那么把最后一个元素替换到需要删除的位置*/
            T last = list.getLast();
            map.put(last, index);
            list.set(index, last);
        }
        /*TODO 删除最后一个*/
        list.removeLast();
    }

    public T random() {
        if (list.isEmpty()) return null;
        return list.get(RandomUtils.random(list.size()));
    }

    public String toString2() {
        return """
                {
                     map=%s,
                    list=%s
                }""".formatted(map, list);
    }

    @Override public String toString() {
        return "RandomList{%s}".formatted(list);
    }
}
