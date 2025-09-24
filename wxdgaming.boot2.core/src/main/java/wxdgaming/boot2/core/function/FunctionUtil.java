package wxdgaming.boot2.core.function;

import java.util.function.Function;

public class FunctionUtil {

    /** 当参数空 返回 默认值 */
    public static <R, T1> R nullDefaultValue(T1 t1, Function<T1, R> function, R defaultValue) {
        if (t1 == null) return defaultValue;
        return function.apply(t1);
    }

}
