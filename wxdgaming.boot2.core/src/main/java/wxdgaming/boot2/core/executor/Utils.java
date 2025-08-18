package wxdgaming.boot2.core.executor;

/**
 * 帮助
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-15 14:24
 **/
public class Utils {

    public static boolean isBank(String source) {
        return source == null || source.isBlank();
    }

    public static boolean isNotBlank(String source) {
        return !isBank(source);
    }

    public static String stack() {
        final String packageName = Utils.class.getPackageName() + ".";
        String name = Thread.class.getName();
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int initSkip = 1;
        for (StackTraceElement stackTraceElement : stackTrace) {
            if (name.equals(stackTraceElement.getClassName())) continue;
            if (stackTraceElement.getClassName().startsWith(packageName)) continue;
            if (stackTraceElement.getMethodName().equals("<init>") && initSkip-- > 0) continue;/*跳过自身的init函数即可*/
            return stackTraceElement.getClassName() + "#" + stackTraceElement.getMethodName() + "():" + stackTraceElement.getLineNumber();

        }
        return "<Unknown>";
    }

    public static String stack(StackTraceElement[] traceElements) {
        StringBuilder builder = new StringBuilder();
        for (StackTraceElement traceElement : traceElements) {
            builder.append("    at ");
            builder.append(traceElement.getClassName()).append("#").append(traceElement.getMethodName())
                    .append("(").append(traceElement.getFileName()).append(":").append(traceElement.getLineNumber()).append(")");
            builder.append("=>");
        }
        return builder.toString();
    }

}
