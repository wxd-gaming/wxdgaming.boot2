package code.rank;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.executor.ExecutorServicePlatform;
import wxdgaming.boot2.core.lang.DiffTimeRecord;
import wxdgaming.boot2.core.rank.RankByLazyListSort;
import wxdgaming.boot2.core.rank.RankScore;
import wxdgaming.boot2.core.util.RandomUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 测试
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-20 21:08
 **/
@Slf4j
public class RankLazyTest {

    public static void main(String[] args) {
        RankByLazyListSort rankMap = new RankByLazyListSort(10000);
        ExecutorServicePlatform executorService = ExecutorFactory.create("map", 2);
        for (int k = 0; k < 10; k++) {
            executorService.execute(() -> {

                DiffTimeRecord diffTime = DiffTimeRecord.start(DiffTimeRecord.IntervalConvertConst.US);
                int iCount = 1000;
                int maxRandom = 5;
                for (int i = 0; i < iCount; i++) {
                    rankMap.updateScore(String.valueOf(i + 1), RandomUtils.random(1, maxRandom));
                }
                diffTime.marker("插入 " + iCount + " 对象 ");
                for (int i = 0; i < iCount; i++) {
                    rankMap.updateScore(String.valueOf(i + 1), RandomUtils.random(1, maxRandom));
                }
                diffTime.marker("修改 " + iCount + " 对象 ");
                String random = String.valueOf(RandomUtils.random(1, iCount));
                {
                    rankMap.updateScore(random, RandomUtils.random(1, maxRandom));
                    diffTime.marker("随机修改一个对象 " + random + " - ");
                }
                {
                    int rank = rankMap.rank(random);
                    diffTime.marker("随机读取一个对象 " + random + " 排名 " + rank + " - ");
                    RankScore rankScore = rankMap.rankDataByRank(rank);
                    diffTime.marker("随机读取一个排名 " + rank + " 对象 " + rankScore.getKey() + " - ");
                }
                rankMap.rankBySize(100);
                diffTime.marker("返回前 100 名 ");
                System.out.println(diffTime.toString());
                System.out.println("=========================================");
            });
        }
        // for (RankScore rankScore : topN) {
        //     log.info("{}", rankScore);
        // }
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(10));
        System.exit(0);
    }

}
