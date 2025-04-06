package reactor;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import wxdgaming.boot2.core.threading.ExecutorUtilImpl;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * 数据流
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-03-29 22:10
 **/
@Slf4j
public class MonoTest {

    static {
        ExecutorUtilImpl.impl();
    }

    public Mono<String> getUserInfo(String userId) {
        return Mono.just("userInfo");
    }

    public Mono<String> getUserInfo1(String userId) {
        CompletableFuture<String> stringCompletableFuture = ExecutorUtilImpl.getInstance().getLogicExecutor().completableFuture(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "userInfo";
        });
        return Mono.fromFuture(stringCompletableFuture);
    }

    public Mono<String> getUserInfo2(String userId) {
        return Mono.fromSupplier(() -> {
            log.info("out2");
            return "getUserInfo2";
        });
    }

    public Mono<String> getUserInfo3(String userId) {
        return Mono.fromFuture(CompletableFuture.supplyAsync(() -> {
            log.info("out3");
            return "getUserInfo3";
        }));
    }

    @Test
    public void t0() throws Exception {
        Mono.delay(Duration.ofSeconds(1)).map(v -> "ss").subscribe(log::info);

        // 异步延迟示例
        Flux.interval(Duration.ofSeconds(1))
                .take(5)
                .subscribe(v -> log.info("{}", v));

        getUserInfo("userId").subscribe(log::info);
        getUserInfo1("userId").subscribe(log::info);
        Thread.sleep(3000);
        getUserInfo2("userId").subscribe(log::info);
        getUserInfo3("userId").subscribe(log::info);
        Thread.sleep(10000);
    }

}
