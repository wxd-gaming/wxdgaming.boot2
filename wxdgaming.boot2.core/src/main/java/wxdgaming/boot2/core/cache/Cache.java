package wxdgaming.boot2.core.cache;

import java.time.Duration;
import java.util.Collection;

public interface Cache<K, V> {

    void close();

    long memorySize();

    long size();

    boolean has(K k);

    Collection<V> values();

    V get(K k);

    void put(K k, V v);

    void invalidate(K k);

    void invalidateAll();

    void invalidateAll(RemovalCause cause);

    void refresh();

    String getCacheName();

    int getBlockSize();

    Duration getHeartExpireAfterWrite();

    Duration getExpireAfterAccess();

    Duration getExpireAfterWrite();

    java.util.function.Function<K, V> getLoader();

    wxdgaming.boot2.core.function.Consumer3<K, V, RemovalCause> getHeartListener();

    wxdgaming.boot2.core.function.Predicate3<K, V, RemovalCause> getRemovalListener();
}
