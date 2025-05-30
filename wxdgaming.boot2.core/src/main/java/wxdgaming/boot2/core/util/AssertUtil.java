package wxdgaming.boot2.core.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * 断言辅助
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-07-31 09:34
 */
public class AssertUtil {

    public static class AssertException extends RuntimeException {

        public AssertException(String message, StackTraceElement[] stackTrace) {
            super(message);
            setStackTrace(stackTrace);
        }
    }

    public static AssertException assertException(String message) {
        return assertException(4, message);
    }

    public static AssertException assertException(String format, Object... args) {
        return assertException(4, String.format(format, args));
    }

    public static AssertException assertException(int stackIndex, String message) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        stackTrace = Arrays.copyOfRange(stackTrace, stackIndex, stackTrace.length);
        return new AssertException(message, stackTrace);
    }

    /** 条件如果是false 抛出异常 */
    public static void assertTrue(boolean success) {
        if (!success) throw assertException("检查异常");
    }

    /** 条件如果是false 抛出异常 */
    public static void assertTrue(boolean success, String format, Object... args) {
        if (!success) throw assertException(format, args);
    }

    /** 如果{@code !Objects.equals(o1, o2)} 抛出异常 */
    public static void assertEquals(Object o1, Object o2, String message) {
        if (!Objects.equals(o1, o2)) throw assertException(message);
    }

    /** 如果{@code Objects.equals(o1, o2)} 抛出异常 */
    public static void assertNotEquals(Object o1, Object o2, String message) {
        if (Objects.equals(o1, o2)) throw assertException(message);
    }

    /** 参数是 null 抛出异常 */
    public static void assertNull(Object object) {
        if (object == null) {
            throw assertException("参数 null");
        }
    }

    /** 参数是 null 抛出异常 */
    public static void assertNull(Object object, String format, Object... args) {
        if (object == null) {
            throw assertException(format, args);
        }
    }

    /** null empty */
    public static void assertNullEmpty(Object source, String message) {
        if (source == null
            || (source instanceof String str && str.isBlank())
            || (source instanceof Collection && ((Collection<?>) source).isEmpty())) {
            throw assertException(message);
        }
    }

}
