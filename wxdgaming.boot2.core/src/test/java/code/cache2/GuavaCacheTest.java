package code.cache2;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Getter;
import org.junit.Test;
import wxdgaming.boot2.core.util.RandomUtils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class GuavaCacheTest {

    @Test
    public void t1() {


        for (int i = 0; i < 10; i++) {
            Object ifPresent = get(RandomUtils.randomBoolean() ? "1" : "2");
            System.out.println(ifPresent);
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
        }

    }

    LoadingCache<String, Object> build2 = CacheBuilder.newBuilder()
            .expireAfterAccess(3, TimeUnit.MINUTES)
            .build(new CacheLoader<String, Object>() {
                @Override public Object load(String key) throws Exception {
                    System.out.println("load2:" + key);
                    return "1".equals(key) ? "1" : null;
                }
            });

    LoadingCache<String, Hold> build1 = CacheBuilder.newBuilder()
            .expireAfterWrite(3, TimeUnit.SECONDS)
            .build(new CacheLoader<String, Hold>() {
                @Override public Hold load(String key) throws Exception {
                    System.out.println("load1:" + key);
                    Object object = null;
                    try {
                        object = build2.get(key);
                    } catch (Exception ignore) {}
                    return new Hold(object);
                }
            });


    public <T> T get(String key) {
        try {
            return (T) build1.get(key).getValue();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    @Getter
    public static class Hold {

        private final Object value;

        public Hold(Object value) {
            this.value = value;
        }

    }

}
