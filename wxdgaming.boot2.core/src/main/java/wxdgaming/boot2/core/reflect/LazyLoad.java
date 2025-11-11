package wxdgaming.boot2.core.reflect;

import java.util.function.Supplier;

/**
 * 懒加载
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-11-11 10:38
 **/
public class LazyLoad<T> implements Supplier<T> {

    private boolean isLoaded = false;
    private final Supplier<T> loader;
    private T obj = null;

    public LazyLoad(Supplier<T> loader) {
        this.loader = loader;
    }

    @Override public synchronized T get() {
        if (!isLoaded) {
            obj = loader.get();
            isLoaded = true;
        }
        return obj;
    }

}
