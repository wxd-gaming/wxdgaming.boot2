package wxdgaming.boot2.core.lang;

import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 运行时间差记录器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-25 13:58
 */
public class DiffTimeRecord {

    /** 时间转化器，纳秒 转换 */
    public interface IntervalConvert {

        /** 传入纳秒，返回自定义数据 */
        long apply(long diffNs);

        /** 时间单位 */
        String unit();

    }

    public enum IntervalConvertConst implements IntervalConvert {
        /** 纳秒 */
        NS() {
            @Override
            public long apply(long diffNs) {
                return diffNs;
            }

            @Override public String unit() {
                return "ns";
            }
        },
        /** 微妙 */
        US() {
            @Override
            public long apply(long diffNs) {
                return diffNs / 1000;
            }

            @Override public String unit() {
                return "us";
            }
        },
        /** 毫秒 */
        MS() {
            @Override
            public long apply(long diffNs) {
                return diffNs / 10000_00;
            }

            @Override public String unit() {
                return "ms";
            }
        },
        /** 秒 */
        S() {
            @Override public long apply(long diffNs) {
                return diffNs / 1000000_000;
            }

            @Override public String unit() {
                return "s";
            }
        },
        ;

    }

    public static DiffTimeRecord start() {
        return new DiffTimeRecord();
    }

    public static DiffTimeRecord start(IntervalConvert convert) {
        DiffTimeRecord diffTimeRecord = new DiffTimeRecord();
        diffTimeRecord.convert = convert;
        return diffTimeRecord;
    }

    @Setter private IntervalConvert convert = IntervalConvertConst.MS;
    private final List<RecordTime> recordTimes = new ArrayList<>();
    private final RecordTime totalTime;
    private RecordTime recordTime = new RecordTime();

    public DiffTimeRecord() {
        totalTime = new RecordTime();
        totalTime.name = "total";
    }

    public void reset() {
        recordTimes.clear();
        recordTime.init();
        totalTime.init();
    }

    /** 标记 */
    public void marker(String marker) {
        RecordTime clone = recordTime.clone();
        clone.name = marker;
        clone.end();
        recordTimes.add(clone);
        recordTime.init();
    }

    public RecordTime getInterval(String marker) {
        for (RecordTime recordTime : recordTimes) {
            if (recordTime.name.equals(marker)) {
                return recordTime.clone();
            }
        }
        return null;
    }

    public RecordTime interval() {
        RecordTime clone = recordTime.clone();
        clone.end();
        return clone;
    }

    public RecordTime totalInterval() {
        RecordTime clone = totalTime.clone();
        clone.end();
        return clone;
    }

    @Override public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (RecordTime recordTime : recordTimes) {
            recordTime.toString(stringBuilder);
            stringBuilder.append("\n");
        }
        RecordTime obj = totalInterval();
        stringBuilder.append(obj).append("\n");
        return stringBuilder.toString();
    }

    public class RecordTime implements Cloneable {

        volatile String name;
        volatile long startTime;
        volatile long endTime;

        public RecordTime() {
            init();
        }

        private void init() {
            startTime = System.nanoTime();
        }

        @Override protected RecordTime clone() {
            try {
                return (RecordTime) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        private void end() {
            endTime = System.nanoTime();
        }

        public long interval() {
            return DiffTimeRecord.this.convert.apply(endTime - startTime);
        }

        private void toString(StringBuilder stringBuilder) {
            if (StringUtils.isNotBlank(name)) {
                stringBuilder.append(name).append(": ");
            }
            stringBuilder.append(interval()).append(" ").append(DiffTimeRecord.this.convert.unit());
        }

        @Override public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            toString(stringBuilder);
            return stringBuilder.toString();
        }
    }
}
