package executor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.executor.*;
import wxdgaming.boot2.core.executor.QueuePolicyConst;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * 线程池异步回调
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-23 09:25
 **/
@Slf4j
public class ExecutorServiceFutureCallBackTest {

    @Test
    public void t2() {
        AbstractExecutorService executorServiceVirtual = new ExecutorServiceVirtual("virtual", 100, 1000, QueuePolicyConst.AbortPolicy);
        AbstractExecutorService executorServicePlatform = new ExecutorServicePlatform("platform", 4, 1000, QueuePolicyConst.AbortPolicy);

        AtomicInteger taskIdFactory = new AtomicInteger(0);

        executorServicePlatform.execute(() -> {
            int taskId = taskIdFactory.incrementAndGet();
            ExecutorContext.Content context = ExecutorContext.context();
            context.getData().put("async", "d");
            log.debug("taskId:{} 发起异步 {}", taskId, context);
            CompletableFuture<Void> future = executorServiceVirtual.future(() -> {

                log.debug("taskId:{} 异步执行中 {}", taskId, ExecutorContext.context());
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));
                log.debug("taskId:{} 异步执行结束 {}", taskId, ExecutorContext.context());
            });
            future.whenComplete((v, e) -> {
                log.debug("taskId:{} 异步回调 {}", taskId, ExecutorContext.context());
            });
        });

        executorServicePlatform.execute(new Run1() {

            @Override public void run() {

                int taskId = taskIdFactory.incrementAndGet();

                ExecutorContext.Content context = ExecutorContext.context();
                context.getData().put("async", "d");
                log.debug("taskId:{} 发起异步队列 queue:{}", taskId, ExecutorContext.context().queueName());
                CompletableFuture<Void> future = executorServiceVirtual.future(() -> {
                    log.debug("taskId:{} 异步队列执行中", taskId);
                    LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));
                    log.debug("taskId:{} 异步队列执行结束", taskId);
                });
                future.whenComplete((v, e) -> {
                    log.debug("taskId:{} 异步队列 回调 queue:{}", taskId, ExecutorContext.context().queueName());
                });
            }

        });

        executorServicePlatform.execute(new Run1() {

            @Override public void run() {

                ExecutorContext.Content context = ExecutorContext.context();
                context.getData().put("async", "d");
                int taskId = taskIdFactory.incrementAndGet();
                log.debug("taskId:{} 发起协程队列 queue:{}", taskId, ExecutorContext.context().queueName());
                CompletableFuture<Void> coroutine = executorServiceVirtual.coroutine(() -> {
                    log.debug("taskId:{} 协程队列执行中", taskId);
                    LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));
                    log.debug("taskId:{} 协程队列执行结束", taskId);
                });
                coroutine.whenComplete((v, e) -> {
                    log.debug("taskId:{} 协程队列回调 queue:{}", taskId, ExecutorContext.context().queueName());
                });
            }

        });

        executorServicePlatform.execute(new Run1() {

            @Override public void run() {
                int taskId = taskIdFactory.incrementAndGet();
                log.debug("taskId:{} 队列任务发起协程仍然是队列任务 queue:{}", taskId, ExecutorContext.context().queueName());
                CompletableFuture<Void> coroutine = executorServiceVirtual.coroutine(new Run1() {
                    @Override public void run() {
                        log.debug("taskId:{} 队列任务发起协程仍然是队列任务 协程队列执行中 queue:{}", taskId, ExecutorContext.context().queueName());
                        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));
                        log.debug("taskId:{} 队列任务发起协程仍然是队列任务 协程队列执行结束 queue:{}", taskId, ExecutorContext.context().queueName());
                    }
                });
                coroutine.whenComplete((v, e) -> {
                    log.debug("taskId:{} 队列任务发起协程仍然是队列任务 回调 queue:{}", taskId, ExecutorContext.context().queueName());
                });
            }

        });

        LockSupport.parkNanos(TimeUnit.MINUTES.toNanos(3));
    }

    public abstract class Run1 implements Runnable, RunnableQueue {

        @Override public String getQueueName() {
            return "abc";//RandomStringUtils.secure().next(4, true, true);
        }

    }

}
