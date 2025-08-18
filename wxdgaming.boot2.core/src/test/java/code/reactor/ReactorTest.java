package code.reactor;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 函数参数泛型类型获取
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-09 17:17
 **/
@Slf4j
public class ReactorTest {

    public void login(String command, Mono<String> stringMono, Mono<List<String>> stringMonoList, Map<String, Object> map) {

    }

    @Test
    public void t1() throws Exception {
        Method declaredMethod = Arrays.stream(this.getClass().getDeclaredMethods())
                .filter(method -> method.getName().equals("login"))
                .findFirst()
                .orElse(null);
        Type[] genericParameterTypes = declaredMethod.getGenericParameterTypes();
        for (Type genericParameterType : genericParameterTypes) {
            Class<?> type = null;
            Class<?> firstT = null;
            if (genericParameterType instanceof Class<?> clazz) {
                type = clazz;
            } else if (genericParameterType instanceof ParameterizedType parameterizedType) {
                type = (Class<?>) parameterizedType.getRawType();
                Type actualTypeArgument = parameterizedType.getActualTypeArguments()[0];
                if (actualTypeArgument instanceof Class<?> clazz) {
                    firstT = clazz;
                } else if (actualTypeArgument instanceof ParameterizedType parameterizedType1) {
                    firstT = (Class<?>) parameterizedType1.getRawType();
                }
            }
            System.out.println(type + " - " + firstT);
        }
    }

    @Test
    public void r2() {
        /*flux的持续发布*/
        LinkedBlockingQueue<String> stack = new LinkedBlockingQueue<>();
        log.info("{}", 22);
        Flux.create((FluxSink<String> sink) -> {
                    Thread.ofPlatform().start(() -> {
                        while (true) {
                            try {
                                sink.next(stack.take());
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                })
                .subscribe(s -> log.info("{}", s));
        log.info("{}", 11);
        stack.add("111");
        stack.add("222");
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
        stack.add("333");
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
    }

    @Test
    public void r3() {
        // 模拟请求创建uid
        Flux<Integer> requestFlux = Flux.range(1, 100);

        // 模拟并发请求处理，每个请求处理需要 1 秒
        requestFlux.flatMap(request ->
                        // 每个请求在单独的线程中处理
                        Flux.just(request).publishOn(Schedulers.parallel())/*把事件转发*/
                                .map(req -> {
                                    /*模拟获取数据库数据需要1秒钟*/
                                    log.info("Processing request 1 " + req + " on thread " + Thread.currentThread().getName());
                                    LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
                                    log.info("Processing request 2 " + req + " on thread " + Thread.currentThread().getName());
                                    return req * 2;
                                })
                )
                .subscribe(result -> log.info("Result of request: " + result));

        try {
            // 主线程等待足够的时间，确保所有请求处理完成
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void r4() {
        // 模拟请求创建uid
        Flux<Integer> requestFlux = Flux.range(1, 10);

        // 模拟并发请求处理，每个请求处理需要 1 秒
        requestFlux
                .publishOn(Schedulers.parallel())/*把事件转发*/
                .map(req -> {
                    /*模拟获取数据库数据需要1秒钟*/
                    log.info("Processing request 1 " + req + " on thread " + Thread.currentThread().getName());
                    LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
                    log.info("Processing request 2 " + req + " on thread " + Thread.currentThread().getName());
                    return req * 2;
                })
                .subscribe(result -> log.info("Result of request: " + result));

        try {
            // 主线程等待足够的时间，确保所有请求处理完成
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void r5() {
        Mono<String> ss = Mono.just("ss");
        ss.subscribe(v -> log.info("1{}", v));
        ss.doOnSuccess(v -> log.info("2{}", v));
    }

}
