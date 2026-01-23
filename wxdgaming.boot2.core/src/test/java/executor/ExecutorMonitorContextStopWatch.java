package executor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

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
@Getter
class ExecutorMonitorContextStopWatch {

    private final TimeUnit timeUnit;
    private FrameInfo curFrameInfo;

    public ExecutorMonitorContextStopWatch(TimeUnit timeUnit, String name) {
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

    public String toString() {
        this.close();
        List<FrameInfo> children = this.getCurFrameInfo().getChildren();
        if (children.isEmpty()) return "";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("name: ").append(this.getCurFrameInfo().getName())
                .append(", ")
                .append("cost: ").append(this.timeUnit.convert(this.getCurFrameInfo().getFlagTime(), TimeUnit.NANOSECONDS))
                .append(" ")
                .append(this.timeUnit)
                .append("\n");
        stringBuilder.append("-----------------------------------------------------------------------").append("\n");
        for (FrameInfo child : children) {
            child.toString(stringBuilder, this.timeUnit);
        }
        return stringBuilder.toString();
    }

    @Getter
    static class FrameInfo {

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
