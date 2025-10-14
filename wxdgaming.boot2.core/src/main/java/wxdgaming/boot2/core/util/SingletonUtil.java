package wxdgaming.boot2.core.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 单例对象
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-12-15 20:54
 **/
public class SingletonUtil {

    static final Function identity = Function.identity();
    static final ConcurrentHashMap<Object, Object> map = new ConcurrentHashMap<>();

    /** 返回单例实例 */
    @SuppressWarnings("unchecked")
    public static <R> R singleton(R r) {
        R old = (R) map.computeIfAbsent(r, identity);
        return old != null ? old : r;
    }

}
