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
    private final AtomicLong totalRunTime = new AtomicLong();
    /** 平均耗时 */
    private float avgMsRunTime = 0;
    private float minMsRunTime = Long.MAX_VALUE;
    private float maxMsRunTime = Long.MIN_VALUE;
    private final AtomicLong avg50MsCount = new AtomicLong();
    private final AtomicLong avg100MsCount = new AtomicLong();
    private final AtomicLong avg200MsCount = new AtomicLong();
    private final AtomicLong avg500MsCount = new AtomicLong();
    private final AtomicLong avg1000MsCount = new AtomicLong();
    private final AtomicLong avg5000MsCount = new AtomicLong();
    private final AtomicLong avgMsCount = new AtomicLong();

    public RunTimeRecord(String name) {
        this.name = name;
    }

    public void record(long costTimeNs) {
        count.incrementAndGet();
        totalRunTime.addAndGet(costTimeNs);
        float costMs = costTimeNs / 10000 / 100f;
        if (costMs < minMsRunTime) minMsRunTime = costMs;
        if (costMs > maxMsRunTime) maxMsRunTime = costMs;
        if (costMs < 50) avg50MsCount.incrementAndGet();
        else if (costMs < 100) avg100MsCount.incrementAndGet();
        else if (costMs < 200) avg200MsCount.incrementAndGet();
        else if (costMs < 500) avg500MsCount.incrementAndGet();
        else if (costMs < 1000) avg1000MsCount.incrementAndGet();
        else if (costMs < 5000) avg5000MsCount.incrementAndGet();
        else avgMsCount.incrementAndGet();
    }

}
