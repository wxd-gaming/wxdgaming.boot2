package wxdgaming.boot2.core.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wxdgaming.boot2.core.executor.ExecutorEvent;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.format.TableFormatter;
import wxdgaming.boot2.core.locks.MonitorReadWrite;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 记录器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-12 09:34
 **/
public class RunTimeUtil extends ExecutorEvent {

    private static final Logger log = LoggerFactory.getLogger("RunTimeRecord");

    private static final MonitorReadWrite MONITOR_READ_WRITE = new MonitorReadWrite();
    private static final AtomicBoolean Open = new AtomicBoolean(false);
    private static ConcurrentHashMap<String, RunTimeRecord> runTimeRecordMap = new ConcurrentHashMap<>();
    private static ScheduledFuture<?> scheduledFuture = null;

    public static void openRecord() {
        Open.set(true);
        scheduledFuture = ExecutorFactory.getExecutorServiceBasic().scheduleAtFixedRate(new RunTimeUtil(), 1, 1, TimeUnit.MINUTES);
    }

    public static void closeRecord() {
        Open.set(false);
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
    }

    public static long start() {
        return System.nanoTime();
    }

    public static void record(String name, long start) {
        long costTimeNs = System.nanoTime() - start;
        if (!Open.get()) return;
        MONITOR_READ_WRITE.readLock();
        try {
            runTimeRecordMap.computeIfAbsent(name, l -> new RunTimeRecord(name)).record(costTimeNs);
        } finally {
            MONITOR_READ_WRITE.unReadLock();
        }
    }

    @Override public boolean isIgnoreRunTimeRecord() {
        return true;
    }

    @Override public void onEvent() throws Exception {
        MONITOR_READ_WRITE.writeLock();
        try {
            if (runTimeRecordMap.isEmpty()) return;
            List<RunTimeRecord> list = runTimeRecordMap.values().stream()
                    .filter(r -> r.getCount().get() > 1)
                    .peek(r -> {
                        long totalTime = r.getTotalRunTimeNs().get();
                        long totalCount = r.getCount().get();
                        if (totalCount > 5) {
                            totalCount -= 2;
                            totalTime = totalTime - r.getMaxRunTimeNs() - r.getMinRunTimeNs();
                        }
                        r.setAvgRunTimeUs((totalTime / totalCount) / 1000);
                    })
                    .sorted((o1, o2) -> {
                        if (o2.getCount().get() != o1.getCount().get()) {
                            return Long.compare(o2.getCount().get(), o1.getCount().get());
                        }
                        if (o2.getTotalRunTimeNs().get() != o1.getTotalRunTimeNs().get()) {
                            return Long.compare(o2.getTotalRunTimeNs().get(), o1.getTotalRunTimeNs().get());
                        }
                        if (o2.getAvgRunTimeUs() != o1.getAvgRunTimeUs()) {
                            return Float.compare(o2.getAvgRunTimeUs(), o1.getAvgRunTimeUs());
                        }
                        return o1.getName().compareTo(o2.getName());
                    }).toList();

            TableFormatter tableFormatter = new TableFormatter();
            tableFormatter.addRow(
                    "event", "totalCount", "totalTimeUs", "avgUs",
                    "minUs", "maxUs",
                    "avg500Us", "avg1000Us", "avg10Ms", "avg50Ms", "avg100Ms", "avg1S", "avg5S", "avg10S", "avg>10S"
            );
            for (RunTimeRecord runTimeRecord : list) {
                tableFormatter.addRow(
                        subName(runTimeRecord.getName()),
                        runTimeRecord.getCount().get(),
                        runTimeRecord.getTotalRunTimeNs().get() / 1000,
                        runTimeRecord.getAvgRunTimeUs(),
                        runTimeRecord.getMinRunTimeNs() / 1000,
                        runTimeRecord.getMaxRunTimeNs() / 1000,
                        runTimeRecord.getAvg500UsCount().get(),
                        runTimeRecord.getAvg1000UsCount().get(),
                        runTimeRecord.getAvg10MsCount().get(),
                        runTimeRecord.getAvg50MsCount().get(),
                        runTimeRecord.getAvg100MsCount().get(),
                        runTimeRecord.getAvg1SCount().get(),
                        runTimeRecord.getAvg5SCount().get(),
                        runTimeRecord.getAvg10SCount().get(),
                        runTimeRecord.getAvgOtherUsCount().get()
                );
            }
            String head = "===============================================";
            log.info("\n{}\n执行耗时统计\n{}\n{}\n{}", head, head, tableFormatter.generateTable(), head);
        } finally {
            MONITOR_READ_WRITE.unWriteLock();
        }
    }

    static String subName(String name) {
        if (name.length() > 60) {
            return name.substring(name.length() - 58);
        }
        return name;
    }

    public static void main(String[] args) {
        String number = "123456789012345678901234567890123456789012345678901234567890123456789012345678";
        System.out.println(number);
        String x = subName(number);
        System.out.println(x);
    }

}
