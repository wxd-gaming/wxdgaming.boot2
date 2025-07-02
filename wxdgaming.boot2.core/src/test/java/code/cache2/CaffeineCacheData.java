package code.cache2;

import com.github.benmanes.caffeine.cache.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

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
@Setter
@Accessors(chain = true)
public class CaffeineCacheData {

    static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    /** 内层数据，真实数据 */
    LoadingCache<String, Object> innerDataCache;
    /** 外层壳 数据缓存3秒钟 如果key对应的数据是null，hold的对象空，防止缓存穿透 */
    LoadingCache<String, Hold> outerDataCache;

    int maxCacheSize = 1000000;

    Duration heartDuration = Duration.ofSeconds(3);
    Duration removeDuration = Duration.ofMinutes(5);

    CacheLoader<String, Object> loader;
    RemovalListener<String, Object> heartListener;
    RemovalListener<String, Object> removalListener;

    public CaffeineCacheData() {

    }

    public CaffeineCacheData build() {
        Caffeine<Object, Object> innerCaffeine = Caffeine.newBuilder()
                .maximumSize(maxCacheSize)
                .expireAfterAccess(removeDuration)
                .executor(scheduledExecutorService);

        if (removalListener != null) {
            innerCaffeine.removalListener(removalListener);
        }

        innerDataCache = innerCaffeine.build(new CacheLoader<String, Object>() {
            @Override public Object load(@NotNull String key) throws Exception {
                if (loader == null) return null;
                return loader.load(key);
            }
        });

        Caffeine<Object, Object> outerCaffeine = Caffeine.newBuilder()
                .maximumSize(maxCacheSize)
                .expireAfterWrite(heartDuration)
                .executor(scheduledExecutorService);

        outerCaffeine.removalListener(new RemovalListener<Object, Hold>() {
            @Override public void onRemoval(@Nullable Object key, @Nullable Hold value, @NotNull RemovalCause cause) {
                if (value == null || value.getValue() == null) return;
                if (heartListener != null)
                    heartListener.onRemoval(String.valueOf(key), value.getValue(), cause);
            }
        });

        outerDataCache = outerCaffeine.build(new CacheLoader<String, Hold>() {
            @Override public Hold load(@NotNull String key) throws Exception {
                Object object = innerDataCache.get(key);
                /*防止缓存穿透，object 允许null*/
                return new Hold(object);
            }
        });

        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            outerDataCache.cleanUp();
            innerDataCache.cleanUp();
        }, 200, 200, TimeUnit.MILLISECONDS);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        Hold hold = outerDataCache.get(key);
        if (hold == null) return null;
        return (T) hold.getValue();
    }

    public void put(String key, Object value) {
        outerDataCache.put(key, new Hold(value));
        innerDataCache.put(key, value);
    }

    public void invalidate(String key) {
        outerDataCache.invalidate(key);
        innerDataCache.invalidate(key);
    }

    public void invalidateAll() {
        outerDataCache.invalidateAll();
        innerDataCache.invalidateAll();
    }

    @Getter
    public static class Hold {

        private final Object value;

        public Hold(Object value) {
            this.value = value;
        }

        @Override public String toString() {
            return String.valueOf(value);
        }
    }

}
