package code.cache2;

import com.google.common.cache.*;
import com.google.common.util.concurrent.UncheckedExecutionException;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.util.AssertUtil;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 基于 guava.cache 缓存数据
 * <p>核心存储是用于存储额加载数据
 * <p>心跳存储，用户触发心跳，比如数据有修改需要存入数据库
 * <p>外置存储，用于对外发布数据，hold对象包裹是为了防止缓存穿透
 *
 * @param <Key>
 * @param <Value>
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-13 14:04
 */
@Slf4j
@Getter
public class GuavaCacheImpl<Key, Value> {

    static final ScheduledExecutorService scheduledExecutorService = ExecutorFactory.newSingleThreadScheduledExecutor("GuavaCacheImpl");
    static final Duration minHeartDuration = Duration.ofSeconds(5);
    static final Duration outerDuration = Duration.ofSeconds(2);
    static final Duration removeDurationDefault = Duration.ofMinutes(5);
    static final Duration heartDurationDefault = Duration.ofSeconds(5);

    final String cacheName;
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
    public GuavaCacheImpl(String cacheName, int maxCacheSize, Duration heartDuration, Duration removeDuration, CacheLoader<Key, Value> loader, RemovalListener<Key, Value> heartListener, RemovalListener<Key, Value> removalListener) {
        this.cacheName = cacheName;
        this.maxCacheSize = Math.max(100_0000, maxCacheSize);
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
            CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder()
                    .maximumSize(maxCacheSize)
                    .expireAfterAccess(removeDuration);

            if (removalListener != null) {
                cacheBuilder.removalListener(new RemovalListener<Key, Value>() {
                    @Override public void onRemoval(RemovalNotification<Key, Value> notification) {
                        RemovalCause cause = notification.getCause();
                        if (cause == RemovalCause.EXPLICIT || cause == RemovalCause.REPLACED) return;
                        if (removalListener != null) {
                            removalListener.onRemoval(notification);
                        }
                    }
                });
            }

            coreDataCache = cacheBuilder.build(new CacheLoader<>() {
                @Override public Value load(Key key) throws Exception {
                    if (loader == null) return null;
                    return loader.load(key);
                }
            });
        }

        {
            CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder()
                    .maximumSize(maxCacheSize)
                    .expireAfterWrite(heartDuration);

            cacheBuilder.removalListener(new RemovalListener<Key, Value>() {
                @Override public void onRemoval(RemovalNotification<Key, Value> notification) {
                    RemovalCause cause = notification.getCause();
                    if (cause == RemovalCause.EXPLICIT || cause == RemovalCause.REPLACED) return;
                    if (heartListener != null) {
                        heartListener.onRemoval(notification);
                    }
                }
            });

            heartDataCache = cacheBuilder.build(new CacheLoader<Key, Value>() {
                @Override public Value load(Key key) throws Exception {
                    return coreDataCache.get(key);
                }
            });
        }
        {
            Duration outerDuration = Duration.ofSeconds(3);
            outerDataCache = CacheBuilder.newBuilder()
                    .maximumSize(maxCacheSize)
                    .expireAfterWrite(outerDuration)
                    .build(new CacheLoader<Key, Hold>() {
                        @Override public Hold load(Key key) throws Exception {
                            Value object = null;
                            try {
                                object = heartDataCache.get(key);
                            } catch (UncheckedExecutionException e) {
                                log.trace("load key={}, {}", key, e.getMessage());
                            }
                            /*防止缓存穿透，object 允许null*/
                            return new Hold(object);
                        }
                    });
        }
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            outerDataCache.cleanUp();
            heartDataCache.cleanUp();
            coreDataCache.cleanUp();
        }, 200, 200, TimeUnit.MILLISECONDS);
    }

    /** 计算内存大小 注意特别耗时，并且可能死循环 */
    public long memorySize() {
        return 0;
    }

    public long size() {
        return coreDataCache.size();
    }

    public Value get(Key key) {
        try {
            Hold hold = outerDataCache.get(key);
            return hold.value();
        } catch (ExecutionException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public Value getIfPresent(Key key) {
        Hold hold = outerDataCache.getIfPresent(key);
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

    /** 指定键过期 */
    public void invalidate(Key key) {
        outerDataCache.invalidate(key);
        heartDataCache.invalidate(key);
        coreDataCache.invalidate(key);
    }

    /** 全部过期 */
    public void invalidateAll() {
        outerDataCache.cleanUp();
        heartDataCache.cleanUp();
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
