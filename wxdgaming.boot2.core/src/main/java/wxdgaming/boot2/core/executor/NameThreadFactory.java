package wxdgaming.boot2.core.executor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NameThreadFactory implements ThreadFactory {

    private final String namePrefix;
    private final boolean daemon;
    private final AtomicInteger nextId = new AtomicInteger(0);

    public NameThreadFactory(String namePrefix, boolean daemon) {
        this.namePrefix = namePrefix;
        this.daemon = daemon;
    }

    @Override public Thread newThread(Runnable r) {
        Thread thread = new Thread(new ThreadRunnable(r), namePrefix + "-" + nextId.incrementAndGet());
        thread.setDaemon(daemon);
        return thread;
    }

}
