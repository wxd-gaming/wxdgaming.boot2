package wxdgaming.boot2.core.function;

import java.util.Objects;
import java.util.function.Function;

/**
 * 传递1个参数的消费类
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2023-10-10 10:25
 **/
@FunctionalInterface
public interface Function1<T1, R> extends SerializableLambda {

    R apply(T1 t1);

    default <V> Function1<T1, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T1 t1) -> after.apply(apply(t1));
    }

}
