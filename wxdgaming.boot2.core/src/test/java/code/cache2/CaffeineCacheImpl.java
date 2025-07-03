package code.cache2;

import com.github.benmanes.caffeine.cache.*;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.executor.NameThreadFactory;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 基于 caffeine 缓存数据
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-30 20:09
 */
@Slf4j
@Builder
public class CaffeineCacheImpl<Key, Value> {

    static final ScheduledExecutorService scheduledExecutorService = ExecutorFactory.newSingleThreadScheduledExecutor("CaffeineCache");

    /** 内层数据，真实数据 */
    LoadingCache<Key, Value> innerDataCache;
    /** 外层壳 数据缓存3秒钟 如果key对应的数据是null，hold的对象空，防止缓存穿透 */
    LoadingCache<Key, Hold> outerDataCache;

    @Builder.Default
    int maxCacheSize = 1000000;
    @Builder.Default
    Duration heartDuration = Duration.ofSeconds(3);
    @Builder.Default
    Duration removeDuration = Duration.ofMinutes(5);

    CacheLoader<Key, Value> loader;
    /** 外层的壳数据缓存过期失效监听触发 */
    RemovalListener<Key, Value> heartListener;
    /** 内层数据，正在的数据过期触发 */
    RemovalListener<Key, Value> removalListener;

    public <K, V> CaffeineCacheImpl<K, V> start() {
        Caffeine<Object, Object> innerCaffeine = Caffeine.newBuilder().maximumSize(maxCacheSize).expireAfterAccess(removeDuration);

        if (removalListener != null) {
            innerCaffeine.removalListener(removalListener);
        }

        innerDataCache = innerCaffeine.build(new CacheLoader<Key, Value>() {
            @Override public Value load(Key key) throws Exception {
                if (loader == null) return null;
                return loader.load(key);
            }
        });

        outerDataCache = Caffeine.newBuilder()
                .maximumSize(maxCacheSize)
                .expireAfterWrite(heartDuration)
                .removalListener(new RemovalListener<Key, Hold>() {
                    @Override public void onRemoval(Key key, Hold hold, RemovalCause cause) {
                        if (hold == null || hold.value() == null) return;
                        if (cause == RemovalCause.EXPLICIT || cause == RemovalCause.REPLACED) return;
                        if (heartListener != null)
                            heartListener.onRemoval(key, hold.value(), cause);
                    }
                })
                .build(new CacheLoader<Key, Hold>() {
                    @Override public Hold load(Key key) throws Exception {
                        Value object = innerDataCache.get(key);
                        /*防止缓存穿透，object 允许null*/
                        return new Hold(object);
                    }
                });

        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            outerDataCache.cleanUp();
            innerDataCache.cleanUp();
        }, 200, 200, TimeUnit.MILLISECONDS);
        return (CaffeineCacheImpl<K, V>) this;
    }

    public Value get(Key key) {
        Hold hold = outerDataCache.get(key);
        if (hold == null) return null;
        return hold.value();
    }

    @SuppressWarnings("unchecked")
    public <T> T getObject(Key key) {
        return (T) get(key);
    }

    public void put(Key key, Value value) {
        outerDataCache.put(key, new Hold(value));
        innerDataCache.put(key, value);
    }

    public void invalidate(Key key) {
        outerDataCache.invalidate(key);
        innerDataCache.invalidate(key);
    }

    public void invalidateAll() {
        outerDataCache.cleanUp();
        innerDataCache.invalidateAll();
    }

    public static class Hold {

        private final Object value;

        public Hold(Object value) {
            this.value = value;
        }

        @SuppressWarnings("unchecked")
        public <R> R value() {
            return (R) value;
        }

        @Override public String toString() {
            return String.valueOf(value);
        }
    }

}
