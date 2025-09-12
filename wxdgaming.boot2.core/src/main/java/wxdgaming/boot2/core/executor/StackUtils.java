package wxdgaming.boot2.core.executor;

/**
 * 帮助
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-15 14:24
 **/
public class StackUtils {

    private static class Lazy {
        static final String thisPackageName = StackUtils.class.getPackageName() + ".";
    }


    /** 追踪到上一次链路 */
    public static String stack() {
        return stack(1, 2);
    }

    public static String stack2() {
        return stack(1, 3);
    }

    public static String stack(int initSkip, int skip) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 2; i < stackTrace.length; i++) {
            StackTraceElement stackTraceElement = stackTrace[i];
            if (stackTraceElement.getMethodName().equals("<init>") && initSkip-- > 0) continue;/*跳过自身的init函数即可*/
            if (skip-- > 0) continue;
            return stackTraceElement.getClassName() + "#" + stackTraceElement.getMethodName() + "():" + stackTraceElement.getLineNumber();
        }
        return "<Unknown>";
    }

    /** 全链路 */
    public static String stackAll() {
        return stack(Thread.currentThread().getStackTrace());
    }

    public static String stack(StackTraceElement[] traceElements) {
        StringBuilder builder = new StringBuilder();
        for (int i = 2; i < traceElements.length; i++) {
            StackTraceElement traceElement = traceElements[i];
            builder.append(traceElement.getClassName()).append("#").append(traceElement.getMethodName())
                    .append("(").append(traceElement.getFileName()).append(":").append(traceElement.getLineNumber()).append(")");
            builder.append("=>");
        }
        return builder.toString();
    }

}
