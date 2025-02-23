package wxdgaming.boot2.core.threading;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.Throw;
import wxdgaming.boot2.core.ann.ThreadParam;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * 本地线程变量
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-04-24 20:26
 **/
@Slf4j
@Getter
public class ThreadContext extends JSONObject {

    private static final ThreadLocal<ThreadContext> local = new InheritableThreadLocal<>();

    public static <R> R context(ThreadParam threadParam, Type type) {
        String name = threadParam.path();
        if (StringUtils.isBlank(name)) {
            name = type.getTypeName();
        }
        R r;
        try {
            r = ThreadContext.context(name);
            if (type instanceof Class<?> clazz && clazz.isInstance(r)) {
                return (R) clazz.cast(r);
            }
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
    public static <T> T context(final Class<T> clazz) {
        return (T) context().get(clazz.getName());
    }

    /** 获取参数 */
    public static <T> T context(final Object name) {
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
        super(m);
    }

}
