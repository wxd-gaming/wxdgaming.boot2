package wxdgaming.boot2.core.executor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.util.AssertUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 当前线程 StopWatch 辅助调用
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-27 09:49
 **/
@Slf4j
public class ThreadStopWatch {

    private static final ThreadLocal<RecordInfo> recordInfoThreadLocal = new ThreadLocal<>();

    /** 初始化上下文记录仪 */
    public static void init(Object name) {
        init(TimeUnit.MICROSECONDS, name);
    }

    /** 初始化上下文记录仪 */
    public static void init(TimeUnit timeUnit, Object name) {
        RecordInfo recordInfo = recordInfoThreadLocal.get();
        AssertUtil.notNull(recordInfo, "上次资源未释放请检查代码");
        recordInfoThreadLocal.set(new RecordInfo(timeUnit, String.valueOf(name)));
    }

    /** 如果不存在记录仪则初始化 */
    public static void initNotPresent(Object name) {
        initNotPresent(TimeUnit.MICROSECONDS, name);
    }

    /** 如果不存在记录仪则初始化 */
    public static void initNotPresent(TimeUnit timeUnit, Object name) {
        RecordInfo recordInfo = recordInfoThreadLocal.get();
        if (recordInfo == null) {
            recordInfoThreadLocal.set(new RecordInfo(timeUnit, String.valueOf(name)));
        }
    }

    /** 当前上下文不存在记录仪会抛异常 */
    public static void start(Object name) {
        RecordInfo recordInfo = recordInfoThreadLocal.get();
        recordInfo.start(String.valueOf(name));
    }

    /** 当前上下文不存在记录仪会抛异常 */
    public static void stop() {
        RecordInfo recordInfo = recordInfoThreadLocal.get();
        recordInfo.stop();
    }

    /** 当前上下文不存在记录仪不会抛异常 */
    public static void startIfPresent(Object name) {
        RecordInfo recordInfo = recordInfoThreadLocal.get();
        if (recordInfo == null) return;
        recordInfo.start(String.valueOf(name));
    }

    /** 当前上下文不存在记录仪不会抛异常 */
    public static void stopIfPresent() {
        RecordInfo recordInfo = recordInfoThreadLocal.get();
        if (recordInfo == null) return;
        recordInfo.stop();
    }

    public static String releasePrint() {
        RecordInfo recordInfo = recordInfoThreadLocal.get();
        if (recordInfo == null) return null;
        recordInfoThreadLocal.remove();
        recordInfo.close();
        List<FrameInfo> children = recordInfo.getCurFrameInfo().getChildren();
        if (children.isEmpty()) return null;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("name: ").append(recordInfo.getCurFrameInfo().getName())
                .append(", ")
                .append("cost: ").append(recordInfo.timeUnit.convert(recordInfo.getCurFrameInfo().getFlagTime(), TimeUnit.NANOSECONDS))
                .append(" ")
                .append(recordInfo.timeUnit)
                .append("\n");
        stringBuilder.append("-----------------------------------------------------------------------").append("\n");
        for (FrameInfo child : children) {
            child.toString(stringBuilder, recordInfo.timeUnit);
        }
        return stringBuilder.toString();
    }

    public static void release() {
        recordInfoThreadLocal.remove();
    }

    @Getter
    private static class RecordInfo {

        private final TimeUnit timeUnit;
        private FrameInfo curFrameInfo;

        public RecordInfo(TimeUnit timeUnit, String name) {
            this.timeUnit = timeUnit;
            curFrameInfo = new FrameInfo(null, name);
        }

        public void start(String name) {
            FrameInfo parentFrameInfo = curFrameInfo;
            FrameInfo newFrameInfo = new FrameInfo(parentFrameInfo, name);
            if (parentFrameInfo != null) {
                parentFrameInfo.children.add(newFrameInfo);
            }
            curFrameInfo = newFrameInfo;
        }

        public void stop() {
            FrameInfo frameInfo = curFrameInfo;
            if (frameInfo == null) {
                return;
            }
            frameInfo.stop();
            FrameInfo parent = frameInfo.getParent();
            if (parent != null) {
                curFrameInfo = parent;
            }
        }

        public void close() {
            do {
                stop();
            } while (curFrameInfo.parent != null);
        }

    }

    @Getter
    private static class FrameInfo {

        private final FrameInfo parent;
        private final String name;
        private final long startTime = System.nanoTime();
        private Long flagTime = null;
        private final List<FrameInfo> children = new ArrayList<>();
        private final int layer;

        public FrameInfo(FrameInfo parent, String name) {
            this.parent = parent;
            this.name = name;
            if (parent != null) {
                layer = parent.layer + 1;
            } else {
                layer = -1;
            }
        }

        public void stop() {
            if (flagTime == null)
                flagTime = System.nanoTime() - startTime;
        }

        public void toString(StringBuilder stringBuilder, TimeUnit timeUnit) {
            if (layer > 0) {
                if (layer > 1) {
                    stringBuilder.append("    ".repeat(layer - 1));
                }
                stringBuilder.append("|---");
            }
            stringBuilder
                    .append("name: ").append(name)
                    .append(", ")
                    .append("cost: ").append(timeUnit.convert(flagTime, TimeUnit.NANOSECONDS))
                    .append("\n");
            for (FrameInfo child : children) {
                child.toString(stringBuilder, timeUnit);
            }
        }

    }

}
