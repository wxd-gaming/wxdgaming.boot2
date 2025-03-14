package wxdgaming.boot2.core.cache;

import wxdgaming.boot2.core.function.Function1;

import java.util.Collection;

interface ICacheArea<K, V> {
    default CacheHolder<K, V> buildValue(K k, V v, long heartTime) {
        CacheHolder<K, V> tuple = new CacheHolder<>(k, v, heartTime);
        refresh(tuple);
        return tuple;
    }

    void refresh(CacheHolder<K, V> holder);

    long getSize();

    boolean containsKey(K k);

    V get(K k);

    V get(K k, Function1<K, V> load);

    V getIfPresent(K k);

    V getIfPresent(K k, Function1<K, V> load);

    void put(K k, V v);

    void putIfAbsent(K k, V v);

    V invalidate(K k);

    void invalidateAll();

    Collection<V> values();

    void shutdown();

    String getCacheName();

    long getDelay();

    long getExpireAfterAccess();

    long getExpireAfterWrite();

    Function1<K, V> getLoader();

    wxdgaming.boot2.core.function.Function2<K, V, Boolean> getRemovalListener();

    long getHeartTime();

    wxdgaming.boot2.core.function.Consumer2<K, V> getHeartListener();

    wxdgaming.boot2.core.threading.TimerJob getTimerJob();
}
