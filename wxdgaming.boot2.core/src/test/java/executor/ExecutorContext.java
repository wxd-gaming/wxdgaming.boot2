package executor;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DurationFormatUtils;
import wxdgaming.boot2.core.timer.MyClock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 线程上下文
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-26 09:38
 **/
@Slf4j
public class ExecutorContext {

    private ExecutorContext() {}

    private static final ConcurrentHashMap<Thread, Content> CONTEXT = new ConcurrentHashMap<>();

    public static Content context() {
        return CONTEXT.computeIfAbsent(Thread.currentThread(), thread -> new Content());
    }

    public static Content getContext() {
        return CONTEXT.get(Thread.currentThread());
    }

    static void setContext(Content content) {
        CONTEXT.put(Thread.currentThread(), content);
    }

    public static void cleanup() {
        Content content = CONTEXT.remove(Thread.currentThread());
        if (content != null && content.getNewTime() > 0 && content.getStartTime() > 0) {
            long costMillis = content.costMillis();
            long newMillis = content.newMillis();
            if (costMillis > 30 || newMillis > 30) {
                log.error("线程执行耗时过大：\n{}", content.costString());
            }
        }
    }

    @Getter
    public static class Content {

        Thread thread;
        AbstractExecutorService executorService;
        ExecutorQueue executorQueue;
        Runnable runnable;
        long newTime = 0;
        long actualNewTime = 0;
        private long startTime;
        private long actualStartTime; // 用于记录实际开始时间
        private ExecutorMonitorContextStopWatch stopWatch = null;
        final JSONObject data = new JSONObject();

        void running() {
            this.startTime = System.nanoTime();
            this.actualStartTime = MyClock.millis();
            this.stopWatch = new ExecutorMonitorContextStopWatch(TimeUnit.MICROSECONDS, String.valueOf(this.runnable));
        }

        public void startWatch(Object name) {
            this.stopWatch.start(String.valueOf(name));
        }

        public void stopWatch() {
            this.stopWatch.stop();
        }

        public String queueName() {
            if (executorQueue != null) {
                return executorQueue.getQueueName();
            }
            return null;
        }

        void execute(Runnable command) {
            if (executorQueue != null) {
                executorQueue.execute(command);
            } else {
                executorService.execute(command);
            }
        }

        /** 纳秒 */
        public long cost() {
            return System.nanoTime() - startTime;
        }

        /** 毫秒 */
        public long costMillis() {
            return TimeUnit.NANOSECONDS.toMillis(cost());
        }

        /** 毫秒 */
        public long newMillis() {
            return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - newTime);
        }

        public String costString() {
            return """
                        线程: %s
                      线程池: %s
                        队列: %s
                        任务: %s
                    提交时间: %s, 开始时间: %s
                    提交耗时: %s, 执行耗时: %s
                    线程数据：%s
                    耗时追踪: ↓↓↓↓
                    %s
                    """.formatted(
                    thread.getName(),
                    String.valueOf(executorService),
                    String.valueOf(executorQueue),
                    String.valueOf(runnable),
                    MyClock.formatDate(this.actualNewTime),
                    MyClock.formatDate(this.actualStartTime),
                    DurationFormatUtils.formatDuration(newMillis(), "HH:mm:ss.SSS"),
                    DurationFormatUtils.formatDuration(costMillis(), "HH:mm:ss.SSS"),
                    data,
                    stopWatch.toString()
            );
        }

        @Override public String toString() {
            return "Content{thread=%s, executorService=%s, executorQueue=%s, runnable=%s, data=%s, newTime=%s, startTime=%s}"
                    .formatted(
                            thread,
                            executorService,
                            executorQueue,
                            String.valueOf(runnable),
                            data,
                            MyClock.formatDate(actualNewTime),
                            MyClock.formatDate(actualStartTime)
                    );
        }
    }

}
