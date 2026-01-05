package wxdgaming.boot2.core.executor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import wxdgaming.boot2.core.Throw;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-30 15:57
 **/
@Slf4j
public enum QueuePolicyConst implements QueuePolicy {
    /** 拒绝策略, 会直接抛异常 */
    AbortPolicy() {
        @Override public RejectedExecutionHandler getRejectedExecutionHandler() {
            return new ThreadPoolExecutor.AbortPolicy();
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override public void execute(ArrayBlockingQueue queue, Runnable task) {
            /*add方式如果队列满了会有异常*/
            queue.add(task);
        }

    },
    /** 静默的方式丢弃 */
    DiscardPolicy() {
        @Override public RejectedExecutionHandler getRejectedExecutionHandler() {
            return new ThreadPoolExecutor.DiscardPolicy();
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override public void execute(ArrayBlockingQueue queue, Runnable task) {
            /*队列已满添加是直接忽略*/
            boolean offer = queue.offer(task);
            if (!offer) {
                log.info("队列已满，任务被丢弃 {}, {}", task, Throw.ofString(new RejectedExecutionException("队列已满拒绝提醒"), false));
            }
        }
    },
    /** 队列已满添加会阻塞等待 */
    WaitPolicy() {
        @Override public RejectedExecutionHandler getRejectedExecutionHandler() {
            return new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    try {
                        // 阻塞等待直到可以添加任务
                        executor.getQueue().put(r);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RejectedExecutionException("Thread interrupted while waiting to add task", e);
                    }
                }
            };
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override public void execute(ArrayBlockingQueue queue, Runnable task) {
            /*队列已满添加会阻塞等待*/
            try {
                queue.put(task);
            } catch (InterruptedException e) {
                throw ExceptionUtils.asRuntimeException(e);
            }
        }
    },
    ;

}