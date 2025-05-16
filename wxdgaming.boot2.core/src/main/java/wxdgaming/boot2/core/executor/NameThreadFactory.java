package wxdgaming.boot2.core.executor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NameThreadFactory implements ThreadFactory {

    private final String namePrefix;
    private final AtomicInteger nextId = new AtomicInteger(0);

    public NameThreadFactory(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    @Override public Thread newThread(Runnable r) {
        return new Thread(new ThreadRunnable(r), namePrefix + "-" + nextId.incrementAndGet());
    }

}
