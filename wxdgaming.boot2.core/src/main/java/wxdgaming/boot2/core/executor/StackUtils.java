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


    /** 追踪到上一层链路 */
    public static String stack() {
        return stack0(0, 0);
    }

    /** 追踪到上一层链路 */
    public static String stack(int skip) {
        return stack0(0, skip);
    }

    /**
     * 获取调用函数所在的文件行数
     *
     * @param initSkip 需要跳过的init函数
     * @param skip     需要跳过行数
     * @return 文件名行数
     */
    public static String stack(int initSkip, int skip) {
        return stack0(initSkip, skip);
    }

    private static String stack0(int initSkip, int skip) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 3; i < stackTrace.length; i++) {
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
            if (!builder.isEmpty()) {
                builder.append("=>");
            }
            builder.append(traceElement.getClassName()).append("#").append(traceElement.getMethodName())
                    .append("(").append(traceElement.getFileName()).append(":").append(traceElement.getLineNumber()).append(")");
        }
        return builder.toString();
    }

}
