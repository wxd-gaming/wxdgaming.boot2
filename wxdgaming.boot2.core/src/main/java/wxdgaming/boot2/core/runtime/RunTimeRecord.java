package wxdgaming.boot2.core.runtime;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 运行时记录
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-12 09:16
 **/
@Getter
@Setter
public class RunTimeRecord {

    private final String name;
    private final AtomicLong count = new AtomicLong();
    /** 记录的时间是纳秒 */
    private final AtomicLong totalRunTimeNs = new AtomicLong();
    private long minRunTimeNs = Long.MAX_VALUE;
    private long maxRunTimeNs = Long.MIN_VALUE;
    /** 平均耗时 */
    private long avgRunTimeUs = 0;
    private final AtomicLong avg500UsCount = new AtomicLong();
    private final AtomicLong avg1000UsCount = new AtomicLong();
    private final AtomicLong avg10MsCount = new AtomicLong();
    private final AtomicLong avg50MsCount = new AtomicLong();
    private final AtomicLong avg100MsCount = new AtomicLong();
    private final AtomicLong avg1SCount = new AtomicLong();
    private final AtomicLong avg5SCount = new AtomicLong();
    private final AtomicLong avg10SCount = new AtomicLong();
    private final AtomicLong avgOtherUsCount = new AtomicLong();

    public RunTimeRecord(String name) {
        this.name = name;
    }

    public void record(long costTimeNs) {
        count.incrementAndGet();
        totalRunTimeNs.addAndGet(costTimeNs);
        if (costTimeNs < minRunTimeNs) minRunTimeNs = costTimeNs;
        if (costTimeNs > maxRunTimeNs) maxRunTimeNs = costTimeNs;
        long costUs = costTimeNs / 1000;
        if (costUs < 500) avg500UsCount.incrementAndGet();
        else if (costUs < 1000) avg1000UsCount.incrementAndGet();
        else if (costUs < 1000 * 10L) avg10MsCount.incrementAndGet();
        else if (costUs < 1000 * 50L) avg50MsCount.incrementAndGet();
        else if (costUs < 1000 * 100L) avg100MsCount.incrementAndGet();
        else if (costUs < 1000 * 1000L) avg1SCount.incrementAndGet();
        else if (costUs < 1000 * 1000L * 5) avg5SCount.incrementAndGet();
        else if (costUs < 1000 * 1000L * 10) avg10SCount.incrementAndGet();
        else avgOtherUsCount.incrementAndGet();
    }

}
