package wxdgaming.boot2.core.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * 断言辅助
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-07-31 09:34
 */
public class AssertUtil {

    public static IllegalArgumentException assertException(String format, Object... args) {
        String message = format;
        if (args.length > 0) {
            message = String.format(format, args);
        }
        return assertException(4, message);
    }

    public static IllegalArgumentException assertException(int stackIndex, String message) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        stackTrace = Arrays.copyOfRange(stackTrace, stackIndex, stackTrace.length);
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException(message);
        illegalArgumentException.setStackTrace(stackTrace);
        return illegalArgumentException;
    }

    /** 条件如果是false 抛出异常 */
    public static void isTrue(boolean success) {
        if (!success) throw assertException("检查异常");
    }

    /** 条件如果是false 抛出异常 */
    public static void isTrue(boolean success, String format, Object... args) {
        if (!success) throw assertException(format, args);
    }

    /** 如果{@code !Objects.equals(o1, o2)} 抛出异常 */
    public static void isEquals(Object o1, Object o2, String format, Object... args) {
        if (!Objects.equals(o1, o2)) throw assertException(format, args);
    }

    /** 如果{@code Objects.equals(o1, o2)} 抛出异常 */
    public static void notEquals(Object o1, Object o2, String format, Object... args) {
        if (Objects.equals(o1, o2)) throw assertException(format, args);
    }

    /** 如果{@code !type.isInstance(o)} 抛出异常 */
    public static void isInstanceOf(Class<?> type, Object o, String format, Object... args) {
        if (!type.isInstance(o))
            throw assertException(format, args);
    }

    /** 如果{@code type.isInstance(o)} 抛出异常 */
    public static void notInstanceOf(Class<?> type, Object o, String format, Object... args) {
        if (type.isInstance(o))
            throw assertException(format, args);
    }

    /** 参数是 null 抛出异常 */
    public static void isNull(Object object) {
        if (object == null) {
            throw assertException("参数 null");
        }
    }

    /** 参数是 null 抛出异常 */
    public static void isNull(Object object, String format, Object... args) {
        if (object == null) {
            throw assertException(format, args);
        }
    }

    /** 参数是 null 抛出异常 */
    public static void notNull(Object object) {
        if (object != null) {
            throw assertException("参数 null");
        }
    }

    /** 参数是 null 抛出异常 */
    public static void notNull(Object object, String format, Object... args) {
        if (object != null) {
            throw assertException(format, args);
        }
    }

    /** null empty */
    public static void nullEmpty(Object source, String format, Object... args) {
        if (source == null
            || (source instanceof String str && str.isBlank())
            || (source instanceof Collection && ((Collection<?>) source).isEmpty())) {
            throw assertException(format, args);
        }
    }

}
