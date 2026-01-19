package wxdgaming.boot2.core.io;

import org.apache.commons.lang3.exception.ExceptionUtils;
import wxdgaming.boot2.core.function.ConsumerE1;
import wxdgaming.boot2.core.function.FunctionE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Supplier;

/**
 * 对象池, 不要使用 {@link java.util.List}，{@link java.lang.StringBuilder} {@link java.util.Map} 这类对象，可能缓存会非常大
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-19 10:41
 **/
public class ObjectFactory<T> {

    final ArrayBlockingQueue<T> blockingQueue;
    final Supplier<T> supplier;

    public ObjectFactory(int max, Supplier<T> supplier) {
        this.blockingQueue = new ArrayBlockingQueue<>(max);
        this.supplier = supplier;
    }

    /** 使用，自动归还 */
    public void accept(ConsumerE1<T> consumer) {
        T t = poll();
        try {
            consumer.accept(t);
        } catch (Throwable e) {
            throw ExceptionUtils.asRuntimeException(e);
        } finally {
            if (t != null)
                release(t);
        }
    }

    /** 使用，自动归还 */
    public <R> R apply(FunctionE<T, R> function) {
        T t = poll();
        try {
            return function.apply(t);
        } catch (Throwable e) {
            throw ExceptionUtils.asRuntimeException(e);
        } finally {
            if (t != null)
                release(t);
        }
    }

    /** 会按需分配，只要需要就会 new, 理论来说 pool 会合理的重复利用 */
    public T poll() {
        T poll = blockingQueue.poll();
        if (poll == null) {
            /*如果队列没有可用的就new一个归还的时候如果超过max会自动丢弃*/
            poll = supplier.get();
        }
        return poll;
    }

    /** 归还对象 */
    public void release(T t) {
        if (t instanceof Map<?, ?> map) map.clear();
        else if (t instanceof Collection<?> list) {
            list.clear();
            if (list instanceof ArrayList<?> arrayList) arrayList.trimToSize();
        } else if (t instanceof StringBuilder stringBuilder) stringBuilder.setLength(0);
        else if (t instanceof StringBuffer stringBuffer) stringBuffer.setLength(0);
        /* TODO 不需要那么精准,如果队列满了会自动丢弃多余对象*/
        blockingQueue.offer(t);
    }

}
