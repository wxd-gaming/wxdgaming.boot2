package code.cache2;

import com.github.benmanes.caffeine.cache.*;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.util.AssertUtil;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 基于 caffeine 缓存数据
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-30 20:09
 */
@Slf4j
@Getter
public class CaffeineCacheImpl<Key, Value> {

    static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    static final Duration minHeartDuration = Duration.ofSeconds(5);
    static final Duration outerDuration = Duration.ofSeconds(2);
    static final Duration removeDurationDefault = Duration.ofMinutes(5);
    static final Duration heartDurationDefault = Duration.ofSeconds(5);

    private final String cacheName;
    /** 核心数据，真实数据 */
    LoadingCache<Key, Value> coreDataCache;
    LoadingCache<Key, Value> heartDataCache;
    /** 外层壳 数据缓存3秒钟 如果key对应的数据是null，hold的对象空，防止缓存穿透 */
    LoadingCache<Key, Hold> outerDataCache;

    int maxCacheSize;
    Duration heartDuration;
    Duration removeDuration;

    CacheLoader<Key, Value> loader;
    /** 外层的壳数据缓存过期失效监听触发 */
    RemovalListener<Key, Value> heartListener;
    /** 内层数据，正在的数据过期触发 */
    RemovalListener<Key, Value> removalListener;

    @Builder
    public CaffeineCacheImpl(String cacheName, int maxCacheSize, Duration heartDuration, Duration removeDuration, CacheLoader<Key, Value> loader, RemovalListener<Key, Value> heartListener, RemovalListener<Key, Value> removalListener) {
        this.cacheName = cacheName;
        this.maxCacheSize = maxCacheSize == 0 ? 100_0000 : maxCacheSize;
        this.heartDuration = heartDuration == null ? heartDurationDefault : heartDuration;
        this.removeDuration = removeDuration == null ? removeDurationDefault : removeDuration;
        this.loader = loader;
        this.heartListener = heartListener;
        this.removalListener = removalListener;
        init();
    }

    private void init() {

        AssertUtil.isTrue(this.removeDuration.toMillis() >= this.heartDuration.toMillis() * 3, "过期时间不得低于心跳时间的3倍");
        AssertUtil.isTrue(this.heartDuration.toMillis() >= minHeartDuration.toMillis(), "心跳时间不得低于%s秒", minHeartDuration.toSeconds());

        {
            Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                    .executor(scheduledExecutorService)
                    .maximumSize(this.maxCacheSize)
                    .expireAfterAccess(this.removeDuration);

            if (removalListener != null) {
                caffeine.removalListener(new RemovalListener<Key, Value>() {
                    @Override public void onRemoval(Key key, Value value, RemovalCause cause) {
                        if (value == null)
                            return;
                        if (cause == RemovalCause.REPLACED)
                            return;
                        removalListener.onRemoval(key, value, cause);
                    }
                });
            }

            coreDataCache = caffeine.build(new CacheLoader<Key, Value>() {
                @Override public Value load(Key key) throws Exception {
                    if (loader == null) return null;
                    return loader.load(key);
                }
            });
        }

        {
            Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                    .executor(scheduledExecutorService)
                    .maximumSize(this.maxCacheSize)
                    .expireAfterWrite(this.heartDuration);

            if (heartListener != null) {
                caffeine.removalListener(new RemovalListener<Key, Value>() {
                    @Override public void onRemoval(Key key, Value value, RemovalCause cause) {
                        if (cause == RemovalCause.REPLACED)
                            return;
                        heartListener.onRemoval(key, value, cause);
                    }
                });
            }

            heartDataCache = caffeine.build(new CacheLoader<Key, Value>() {
                @Override public Value load(Key key) throws Exception {
                    return coreDataCache.get(key);
                }
            });
        }
        {
            outerDataCache = Caffeine.newBuilder()
                    .executor(scheduledExecutorService)
                    .maximumSize(this.maxCacheSize)
                    .expireAfterWrite(outerDuration)
                    .build(new CacheLoader<Key, Hold>() {
                        @Override public Hold load(Key key) throws Exception {
                            Value object = heartDataCache.get(key);
                            /*防止缓存穿透，object 允许null*/
                            return new Hold(object);
                        }
                    });
        }
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            outerDataCache.cleanUp();
            heartDataCache.cleanUp();
            coreDataCache.cleanUp();
        }, 500, 500, TimeUnit.MILLISECONDS);
    }

    /** 计算内存大小 注意特别耗时，并且可能死循环 */
    public long memorySize() {
        return 0;
    }

    public long size() {
        return coreDataCache.estimatedSize();
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
        heartDataCache.put(key, value);
        coreDataCache.put(key, value);
    }

    public void invalidate(Key key) {
        outerDataCache.invalidate(key);
        heartDataCache.invalidate(key);
        coreDataCache.invalidate(key);
    }

    public void invalidateAll() {
        outerDataCache.invalidateAll();
        heartDataCache.invalidateAll();
        coreDataCache.invalidateAll();
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
