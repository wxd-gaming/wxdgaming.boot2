package wxdgaming.boot2.core.collection.concurrent;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 双向绑定
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-23 09:58
 **/
public class ConcurrentDataBinding<E1, E2> {

    private final ConcurrentHashMap<E1, E2> e1ToE2map = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<E2, E1> e2ToE1map = new ConcurrentHashMap<>();

    public void bind(E1 e1, E2 e2) {
        e1ToE2map.put(e1, e2);
        e2ToE1map.put(e2, e1);
    }

    public void unbindByE1(E1 e1) {
        E2 remove = e1ToE2map.remove(e1);
        if (remove != null)
            e2ToE1map.remove(remove);
    }

    public void unbindByE2(E2 e2) {
        E1 remove = e2ToE1map.remove(e2);
        if (remove != null)
            e1ToE2map.remove(remove);
    }

    public void unbind(E1 e1, E2 e2) {
        unbindByE1(e1);
        unbindByE2(e2);
    }

    public boolean containsE1(E1 e1) {
        return e1ToE2map.containsKey(e1);
    }

    public boolean containsE2(E2 e2) {
        return e2ToE1map.containsKey(e2);
    }

    public E2 getByE1(E1 e1) {
        return e1ToE2map.get(e1);
    }

    public E1 getByE2(E2 e2) {
        return e2ToE1map.get(e2);
    }

}
