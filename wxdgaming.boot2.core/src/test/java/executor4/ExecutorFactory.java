package executor4;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 执行器工厂
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-06-04 17:43
 **/
public class ExecutorFactory {

    static final ConcurrentHashMap<Thread, Executor> __currentExecutorMap = new ConcurrentHashMap<>();

    public static Executor currentExecutor() {
        Thread currentThread = Thread.currentThread();
        Executor executor = __currentExecutorMap.get(currentThread);
        if (executor == null) {
            return null;
        }
        return executor;
    }

    public static String currentExecutorName() {
        Thread currentThread = Thread.currentThread();
        Executor executor = __currentExecutorMap.get(currentThread);
        if (executor == null) {
            return currentThread.getName();
        }
        return currentThread.getName() + "-" + executor.toString();
    }

    public ExecutorFactory() {
    }

    public static class NameThreadFactory implements ThreadFactory {

        final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        public NameThreadFactory(String namePrefix) {
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, namePrefix + "-" + threadNumber.getAndIncrement());
        }

    }

}
