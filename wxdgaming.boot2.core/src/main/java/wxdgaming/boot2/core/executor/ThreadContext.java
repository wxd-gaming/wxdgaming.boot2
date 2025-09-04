package wxdgaming.boot2.core.executor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.Throw;
import wxdgaming.boot2.core.ann.ThreadParam;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.json.FastJsonUtil;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 本地线程变量
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-04-24 20:26
 **/
@Slf4j
@Getter
public class ThreadContext extends HashMap<String, Object> {

    private static final ThreadLocal<ThreadContext> local = new ThreadLocal<>();

    @SuppressWarnings("unchecked")
    public static <R> R context(ThreadParam threadParam, Type type) {
        String name = threadParam.path();
        if (StringUtils.isBlank(name)) {
            name = type.getTypeName();
        }
        R r;
        try {
            ThreadContext context = ThreadContext.context();
            r = (R) context.get(name);
            if (r == null && StringUtils.isNotBlank(threadParam.defaultValue())) {
                r = FastJsonUtil.parse(threadParam.defaultValue(), type);
            }
        } catch (Exception e) {
            throw Throw.of("threadParam 参数：" + name, e);
        }
        if (threadParam.required() && r == null) {
            throw new RuntimeException("threadParam:" + name + " is null");
        }
        return r;
    }

    /** 获取参数 */
    @SuppressWarnings("unchecked")
    public static <T> T context(final Class<T> clazz) {
        return (T) context().get(clazz.getName());
    }

    /** 获取参数 */
    @SuppressWarnings("unchecked")
    public static <T> T context(final String name) {
        return (T) context().get(name);
    }

    /** put参数 */
    public static <T> T putContent(final Class<T> clazz) {
        try {
            T ins = clazz.getDeclaredConstructor().newInstance();
            putContentIfAbsent(ins);
            return ins;
        } catch (Exception e) {
            throw new RuntimeException(clazz.getName(), e);
        }
    }

    /** put参数 */
    public static <T> void putContent(final T ins) {
        context().put(ins.getClass().getName(), ins);
    }

    /** put参数 */
    public static <T> void putContent(final String name, T ins) {
        context().put(name, ins);
    }

    /** put参数 */
    public static <T> void putContentIfAbsent(final T ins) {
        context().putIfAbsent(ins.getClass().getName(), ins);
    }

    /** put参数 */
    public static <T> void putContentIfAbsent(final String name, T ins) {
        context().putIfAbsent(name, ins);
    }

    /** 获取参数 */
    public static ThreadContext context() {
        ThreadContext threadContext = local.get();
        if (threadContext == null) {
            threadContext = new ThreadContext();
            local.set(threadContext);
        }
        return threadContext;
    }

    /** 设置参数 */
    public static void set() {
        local.set(new ThreadContext());
    }

    /** 设置参数 */
    public static void set(ThreadContext threadContext) {
        local.set(threadContext);
    }

    /** 清理缓存 */
    public static void cleanup() {
        local.remove();
    }

    /** 清理缓存 */
    public static void cleanup(Class<?> clazz) {
        context().remove(clazz.getName());
    }

    /** 清理缓存 */
    public static void cleanup(String name) {
        context().remove(name);
    }

    public ThreadContext() {
    }

    public ThreadContext(Map m) {
        super();
        putAll(m);
    }

    public void putAllIfAbsent(Map<? extends String, ?> m) {
        for (Entry<? extends String, ?> entry : m.entrySet()) {
            putIfAbsent(entry.getKey(), entry.getValue());
        }
    }

    @Override public void putAll(Map<? extends String, ?> m) {
        super.putAll(m);
    }

    /** 获取当前任务所在队列 */
    public ExecutorQueue queue() {
        return (ExecutorQueue) get("queue");
    }

    /** 获取当然任务队列名 */
    public String queueName() {
        return (String) get("queueName");
    }

    public String getString(String key) {
        Object value = get(key);
        if (value != null) {
            return value.toString();
        }
        return null;
    }

    public int getIntValue(String key) {
        Object value = get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return 0;
    }

    public Integer getInteger(String key) {
        Object value = get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return null;
    }

    public Long getLong(String key) {
        Object value = get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }

    public long getLongValue(String key) {
        Object value = get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return 0;
    }

}
