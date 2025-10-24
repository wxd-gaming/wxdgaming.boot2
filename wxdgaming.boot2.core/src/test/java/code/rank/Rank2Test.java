package code.rank;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.executor.ExecutorServicePlatform;
import wxdgaming.boot2.core.rank.RankByTreeSet;
import wxdgaming.boot2.core.rank.RankElement;
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
public class Rank2Test {

    public static void main(String[] args) {
        RankByTreeSet rankMap = new RankByTreeSet();
        ExecutorServicePlatform executorService = ExecutorFactory.create("map", 3);
        for (int k = 0; k < 10; k++) {
            executorService.execute(() -> {
                StopWatch stopWatch = new StopWatch("rank test");
                int iCount = 1000;
                int maxRandom = 5000;
                for (int i = 0; i < iCount; i++) {
                    rankMap.updateScore(String.valueOf(i + 1), RandomUtils.random(1, maxRandom));
                }
                stopWatch.start("插入 " + iCount + " 对象 ");
                for (int i = 0; i < iCount; i++) {
                    rankMap.updateScore(String.valueOf(i + 1), RandomUtils.random(1, maxRandom));
                }
                stopWatch.start("修改 " + iCount + " 对象 ");
                String random = String.valueOf(RandomUtils.random(1, iCount));
                {
                    rankMap.updateScore(random, RandomUtils.random(1, maxRandom));
                    stopWatch.start("随机修改一个对象 " + random);
                }
                {
                    int rank = rankMap.rank(random);
                    stopWatch.start("随机读取一个对象 " + random + " 排名 " + rank);
                    RankElement rankElement = rankMap.rankDataByRank(rank);
                    stopWatch.start("随机读取一个排名 " + rank + " 对象 " + rankElement.getKey());
                }
                rankMap.rankBySize(100);
                stopWatch.start("返回前 100 名 ");
                System.out.println(stopWatch.toString());
                System.out.println("==================================");
            });
        }
        // for (RankScore rankScore : topN) {
        //     log.info("{}", rankScore);
        // }

        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(10));
        System.exit(0);
    }

}
