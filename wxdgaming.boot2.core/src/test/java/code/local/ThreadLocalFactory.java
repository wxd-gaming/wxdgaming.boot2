package code.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ThreadLocalFactory {

    static ConcurrentMap<Thread, HashMap<Class<?>, ArrayList<Object>>> guardThreadLocal = new ConcurrentHashMap<>();

    public static void push(Object object) {
        Thread thread = Thread.currentThread();
        HashMap<Class<?>, ArrayList<Object>> map = guardThreadLocal.computeIfAbsent(thread, l -> new HashMap<>());
        ArrayList<Object> list = map.computeIfAbsent(object.getClass(), l -> new ArrayList<>());
        list.add(object);
    }

    /** 弹出当前对象，不会删除记录，可以继续使用 */
    public static <T> T peek(Class<T> cls) {
        HashMap<Class<?>, ArrayList<Object>> classArrayListHashMap = guardThreadLocal.get(Thread.currentThread());
        if (classArrayListHashMap == null)
            return null;
        if (classArrayListHashMap.isEmpty()) {
            release();
            return null;
        }
        ArrayList<Object> objects = classArrayListHashMap.get(cls);
        if (objects == null) {
            return null;
        }
        return cls.cast(objects.getLast());
    }

    /** 弹出当前对象，不会删除记录，可以继续使用 */
    public static <T> Optional<T> peekOptional(Class<T> cls) {
        return Optional.ofNullable(peek(cls));
    }

    /** 弹出当前对象，并且会删除记录 */
    public static <T> T pop(Class<T> cls) {
        HashMap<Class<?>, ArrayList<Object>> classArrayListHashMap = guardThreadLocal.get(Thread.currentThread());
        if (classArrayListHashMap == null)
            return null;
        if (classArrayListHashMap.isEmpty()) {
            release();
            return null;
        }
        ArrayList<Object> objects = classArrayListHashMap.get(cls);
        try {
            return cls.cast(objects.removeLast());
        } finally {
            if (objects.isEmpty()) {
                classArrayListHashMap.remove(cls);
                if (classArrayListHashMap.isEmpty()) {
                    release();
                }
            }
        }
    }

    public static void release() {
        Thread thread = Thread.currentThread();
        guardThreadLocal.remove(thread);
    }

}
