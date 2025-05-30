package wxdgaming.boot2.core.function;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2022-04-22 15:00
 **/
@FunctionalInterface
public interface Predicate2<T1, T2> extends SerializableLambda {

    boolean test(T1 t1, T2 t2);

}
