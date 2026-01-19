package wxdgaming.boot2.core.io;

import org.apache.commons.lang3.exception.ExceptionUtils;
import wxdgaming.boot2.core.function.ConsumerE1;
import wxdgaming.boot2.core.function.FunctionE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

/**
 * 对象池, 不要使用 {@link java.util.List}，{@link java.lang.StringBuilder} {@link java.util.Map} 这类对象，可能缓存会非常大
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-19 10:41
 **/
public class ObjectFactory<T> {

    final LinkedBlockingQueue<T> pool;
    private final Supplier<T> supplier;

    public ObjectFactory(int max, Supplier<T> supplier) {
        pool = new LinkedBlockingQueue<>(max);
        this.supplier = supplier;
        for (int i = 0; i < max; i++) {
            pool.offer(supplier.get());
        }
    }

    /** 使用，自动归还 */
    public void accept(ConsumerE1<T> consumer) {
        T t = get();
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
        T t = get();
        try {
            return function.apply(t);
        } catch (Throwable e) {
            throw ExceptionUtils.asRuntimeException(e);
        } finally {
            if (t != null)
                release(t);
        }
    }

    public T get() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public void release(T t) {
        if (t instanceof Map<?, ?> map) map.clear();
        else if (t instanceof Collection<?> list) {
            list.clear();
            if (list instanceof ArrayList<?> arrayList) arrayList.trimToSize();
        }
        pool.add(t);
    }

}
