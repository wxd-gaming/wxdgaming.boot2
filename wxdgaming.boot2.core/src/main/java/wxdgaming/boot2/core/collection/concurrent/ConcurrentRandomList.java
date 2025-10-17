package wxdgaming.boot2.core.collection.concurrent;

import lombok.Getter;
import wxdgaming.boot2.core.collection.ListOf;
import wxdgaming.boot2.core.lang.ObjectBaseRWLock;
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
public class ConcurrentRandomList<T> extends ObjectBaseRWLock implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    private Map<T, Integer> map = new HashMap<>();
    private List<T> list = ListOf.newArrayList();

    public void add(T t) {
        syncWrite(() -> {
            if (!map.containsKey(t)) {
                map.put(t, list.size());
                list.add(t);
            }
        });
    }

    public void remove(T t) {
        syncWrite(() -> {
            Integer index = map.remove(t);
            if (index == null) return;
            if (index < list.size() - 1) {
                list.set(index, list.getLast());
            }
            list.removeLast();
        });
    }

    public T random() {
        return supplierRead(() -> {
            if (list.isEmpty()) {
                return null;
            }
            return list.get(RandomUtils.random(list.size()));
        });
    }

}
