package wxdgaming.boot2.core.executor;

import org.springframework.util.StopWatch;
import wxdgaming.boot2.core.util.AssertUtil;

import java.util.concurrent.TimeUnit;

/**
 * 当前线程 StopWatch 辅助调用
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-27 09:49
 **/
public class ThreadStopWatch {

    private static final ThreadLocal<StopWatch> THREAD_LOCAL = new ThreadLocal<>();

    public static void init(String name) {
        StopWatch stopWatch = THREAD_LOCAL.get();
        AssertUtil.notNull(stopWatch, "上次资源未释放请检查代码");
        stopWatch = new StopWatch(name);
        THREAD_LOCAL.set(stopWatch);
    }

    public static void release() {
        THREAD_LOCAL.remove();
    }

    public static StopWatch get() {
        StopWatch stopWatch = THREAD_LOCAL.get();
        AssertUtil.isNull(stopWatch, "请先调用init方法进行初始化");
        return stopWatch;
    }

    public static void start(String name) {
        StopWatch stopWatch = get();
        stopWatch.start(name);
    }

    public static void stop() {
        StopWatch stopWatch = get();
        AssertUtil.isTrue(stopWatch.isRunning(), "请先调用start()");
        stopWatch.stop();
    }

    public static long getTotalTimeMillis() {
        StopWatch stopWatch = get();
        AssertUtil.isTrue(stopWatch.getTaskCount() > 0, "尚未执行过任务");
        return stopWatch.getTotalTimeMillis();
    }

    public static String prettyPrint() {
        return prettyPrint(TimeUnit.MILLISECONDS);
    }

    public static String prettyPrintUs() {
        return prettyPrint(TimeUnit.MICROSECONDS);
    }

    public static String prettyPrint(TimeUnit timeUnit) {
        StopWatch stopWatch = get();
        AssertUtil.isTrue(stopWatch.getTaskCount() > 0, "尚未执行过任务");
        return stopWatch.prettyPrint(timeUnit);
    }


}
