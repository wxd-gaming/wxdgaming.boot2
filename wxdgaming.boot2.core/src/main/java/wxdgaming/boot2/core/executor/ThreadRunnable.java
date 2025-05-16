package wxdgaming.boot2.core.executor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
record ThreadRunnable(Runnable runnable) implements Runnable {

    @Override public void run() {
        Thread thread = Thread.currentThread();
        while (true) {
            try {
                runnable.run();
                break;
            } catch (Throwable throwable) {
                if (throwable instanceof InterruptedException) {
                    thread.interrupt();
                    break;
                }
                log.error("未知异常 {}", thread, throwable);
            }
        }
    }
}
