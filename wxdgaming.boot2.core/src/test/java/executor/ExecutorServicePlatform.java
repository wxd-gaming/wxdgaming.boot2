package executor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.executor.QueuePolicyConst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 平台线程
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-22 14:21
 **/
@Slf4j
@Getter
public class ExecutorServicePlatform extends AbstractExecutorService {

    private final List<Thread> threads = Collections.synchronizedList(new ArrayList<>());

    public ExecutorServicePlatform(String namePrefix, int threadSize, int queueSize, QueuePolicyConst queuePolicy) {
        super(namePrefix, threadSize, queueSize, queuePolicy);
        newThread();
    }

    @Override protected synchronized void checkExecute() {
        int size = threads.size();
        if (size < getThreadSize()) {
            newThread();
        }
    }

    @Override protected void newThread() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                while (!getStoping().get()) {
                    Runnable task = null;
                    try {
                        task = getQueue().poll(10, TimeUnit.MILLISECONDS);
                        if (task != null) {
                            ExecutorVO executorVO = ExecutorVO.threadLocal();
                            executorVO.setExecutor(ExecutorServicePlatform.this);
                            task.run();
                            ExecutorVO.cleanup();
                        }
                    } catch (InterruptedException e) {
                        break;
                    } catch (Throwable e) {
                        log.error("{} {} error", task == null ? "null" : task.getClass(), task, e);
                    }
                }
                log.error("{} {} stop", this.getClass(), Thread.currentThread().getName());
                getThreads().remove(Thread.currentThread());
            }
        };
        thread.setName(getNamePrefix() + "-" + (getThreads().size() + 1));
        thread.start();
        getThreads().add(thread);
    }

}
