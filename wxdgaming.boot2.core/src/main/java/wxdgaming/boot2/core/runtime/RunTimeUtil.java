package wxdgaming.boot2.core.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wxdgaming.boot2.core.executor.ExecutorEvent;
import wxdgaming.boot2.core.executor.ExecutorFactory;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 记录器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-12 09:34
 **/
public class RunTimeUtil extends ExecutorEvent {

    private static final Logger log = LoggerFactory.getLogger("RunTimeRecord");

    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private static final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
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
        readLock.lock();
        try {
            runTimeRecordMap.computeIfAbsent(name, l -> new RunTimeRecord(name)).record(costTimeNs);
        } finally {
            readLock.unlock();
        }
    }

    @Override public boolean isIgnoreRunTimeRecord() {
        return true;
    }

    @Override public void onEvent() throws Exception {
        writeLock.lock();
        try {
            List<RunTimeRecord> list = runTimeRecordMap.values().stream()
                    .filter(r -> r.getCount().get() > 1)
                    .peek(r -> {
                        long totalTime = r.getTotalRunTime().get() / 10000;
                        long totalCount = r.getCount().get();
                        r.setAvgMsRunTime((totalTime / totalCount) / 100f);
                    })
                    .sorted((o1, o2) -> {
                        if (o2.getCount().get() > 1 && o1.getCount().get() > 1) {
                            if (o2.getTotalRunTime().get() != o1.getTotalRunTime().get()) {
                                return Long.compare(o2.getTotalRunTime().get(), o1.getTotalRunTime().get());
                            }
                        }
                        if (o2.getCount().get() != o1.getCount().get()) {
                            return Long.compare(o2.getCount().get(), o1.getCount().get());
                        }
                        if (o2.getAvgMsRunTime() != o1.getAvgMsRunTime()) {
                            return Float.compare(o2.getAvgMsRunTime(), o1.getAvgMsRunTime());
                        }
                        return o1.getName().compareTo(o2.getName());
                    }).toList();

            StringBuilder sb = new StringBuilder();
            String format = "|%60s|%20s|%16s|%12s|%12s|%12s|%12s|%12s|%12s|%12s|%12s|%12s|%12s|";
            String title = format.formatted(
                    "event", "totalTimeMs", "totalCount", "avg",
                    "minMs", "maxMs",
                    "avg50Ms", "avg100Ms", "avg200Ms", "avg500Ms", "avg1000Ms", "avg5000Ms", "avg>5000Ms"
            );
            sb.append(title).append("\n\n");
            DecimalFormat df = new DecimalFormat("0.00");
            for (RunTimeRecord runTimeRecord : list) {
                String line = format.formatted(
                        subName(runTimeRecord.getName()),
                        df.format((runTimeRecord.getTotalRunTime().get() / 10000 / 100f)),
                        runTimeRecord.getCount().get(),
                        df.format((runTimeRecord.getAvgMsRunTime())),
                        df.format((runTimeRecord.getMinMsRunTime())),
                        df.format((runTimeRecord.getMaxMsRunTime())),
                        runTimeRecord.getAvg50MsCount().get(),
                        runTimeRecord.getAvg100MsCount().get(),
                        runTimeRecord.getAvg200MsCount().get(),
                        runTimeRecord.getAvg500MsCount().get(),
                        runTimeRecord.getAvg1000MsCount().get(),
                        runTimeRecord.getAvg5000MsCount().get(),
                        runTimeRecord.getAvgMsCount().get()
                );
                sb.append(line).append("\n");
            }
            log.info("\n执行耗时统计\n{}", sb);
        } finally {
            writeLock.unlock();
        }
        throw new RuntimeException("测试");
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
