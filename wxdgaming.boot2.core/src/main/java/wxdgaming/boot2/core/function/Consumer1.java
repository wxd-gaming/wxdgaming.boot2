package wxdgaming.boot2.core.function;

/**
 * 传递两个参数的消费类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2023-10-10 10:25
 **/
@FunctionalInterface
public interface Consumer1<T1> extends SerializableLambda {

    void accept(T1 t1);

}
