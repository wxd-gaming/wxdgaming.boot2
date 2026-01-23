package executor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
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
            ExecutorVO executorVO = ExecutorVO.threadLocal();
            log.debug("taskId:{} 发起异步 {}", taskId, executorVO);
            CompletableFuture<Void> future = executorServiceVirtual.future(() -> {
                ExecutorVO executorVO1 = ExecutorVO.threadLocal();
                log.debug("taskId:{} 异步执行中 {}", taskId, executorVO1);
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));
                log.debug("taskId:{} 异步执行结束 {}", taskId, executorVO1);
            });
            future.whenComplete((v, e) -> {
                ExecutorVO whenComplete = ExecutorVO.threadLocal();
                log.debug("taskId:{} 异步回调 {}", taskId, whenComplete);
            });
        });

        executorServicePlatform.execute(new Run1() {

            @Override public void run() {

                int taskId = taskIdFactory.incrementAndGet();

                log.debug("taskId:{} 发起异步队列 queue:{}", taskId, ExecutorVO.threadLocal().queueName());
                CompletableFuture<Void> future = executorServiceVirtual.future(() -> {
                    log.debug("taskId:{} 异步队列执行中", taskId);
                    LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));
                    log.debug("taskId:{} 异步队列执行结束", taskId);
                });
                future.whenComplete((v, e) -> {
                    ExecutorVO whenComplete = ExecutorVO.threadLocal();
                    log.debug("taskId:{} 异步队列 回调 queue:{}", taskId, whenComplete.queueName());
                });
            }

        });

        executorServicePlatform.execute(new Run1() {

            @Override public void run() {
                int taskId = taskIdFactory.incrementAndGet();
                ExecutorVO executorVO = ExecutorVO.threadLocal();
                log.debug("taskId:{} 发起协程队列 queue:{}", taskId, executorVO.queueName());
                CompletableFuture<Void> coroutine = executorServiceVirtual.coroutine(() -> {
                    log.debug("taskId:{} 协程队列执行中", taskId);
                    LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));
                    log.debug("taskId:{} 协程队列执行结束", taskId);
                });
                coroutine.whenComplete((v, e) -> {
                    ExecutorVO whenComplete = ExecutorVO.threadLocal();
                    log.debug("taskId:{} 协程队列回调 queue:{}", taskId, whenComplete.queueName());
                });
            }

        });

        executorServicePlatform.execute(new Run1() {

            @Override public void run() {
                int taskId = taskIdFactory.incrementAndGet();
                ExecutorVO executorVO = ExecutorVO.threadLocal();
                log.debug("taskId:{} 队列任务发起协程仍然是队列任务 queue:{}", taskId, executorVO.queueName());
                CompletableFuture<Void> coroutine = executorServiceVirtual.coroutine(new Run1() {
                    @Override public void run() {
                        ExecutorVO executorVO1 = ExecutorVO.threadLocal();
                        log.debug("taskId:{} 队列任务发起协程仍然是队列任务 协程队列执行中 queue:{}", taskId, executorVO1.queueName());
                        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));
                        log.debug("taskId:{} 队列任务发起协程仍然是队列任务 协程队列执行结束 queue:{}", taskId, executorVO1.queueName());
                    }
                });
                coroutine.whenComplete((v, e) -> {
                    ExecutorVO whenComplete = ExecutorVO.threadLocal();
                    log.debug("taskId:{} 队列任务发起协程仍然是队列任务 回调 queue:{}", taskId, whenComplete.queueName());
                });
            }

        });

        LockSupport.parkNanos(TimeUnit.MINUTES.toNanos(3));
    }

    public abstract class Run1 implements Runnable, RunnableQueue {

        @Override public String queueName() {
            return "abc";//RandomStringUtils.secure().next(4, true, true);
        }

    }

}
