package code.rank;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.executor.ExecutorServicePlatform;
import wxdgaming.boot2.core.rank.RankByGroupMap;
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
public class RankTest {

    public static StopWatch newDiffTimeRecord() {
        return new StopWatch();
    }

    public static void main(String[] args) {
        RankByGroupMap rankByGroupMap = new RankByGroupMap();
        ExecutorServicePlatform executorService = ExecutorFactory.create("map", 3);
        for (int k = 0; k < 10; k++) {
            executorService.execute(() -> {

                StopWatch diffTime = newDiffTimeRecord();
                int iCount = 1000;
                int maxRandom = 5;
                for (int i = 0; i < iCount; i++) {
                    rankByGroupMap.updateScore(String.valueOf(i + 1), RandomUtils.random(1, maxRandom));
                }
                diffTime.start("插入 " + iCount + " 对象 ");
                for (int i = 0; i < iCount; i++) {
                    rankByGroupMap.updateScore(String.valueOf(i + 1), RandomUtils.random(1, maxRandom));
                }
                diffTime.start("修改 " + iCount + " 对象 ");
                String random = String.valueOf(RandomUtils.random(1, iCount));
                {
                    rankByGroupMap.updateScore(random, RandomUtils.random(1, maxRandom));
                    diffTime.start("随机修改一个对象 " + random + " - ");
                }
                {
                    int rank = rankByGroupMap.rank(random);
                    diffTime.start("随机读取一个对象 " + random + " 排名 " + rank + " - ");
                    RankElement rankElement = rankByGroupMap.rankDataByRank(rank);
                    diffTime.start("随机读取一个排名 " + rank + " 对象 " + rankElement.getKey() + " - ");
                }
                rankByGroupMap.rankBySize(100);
                diffTime.start("返回前 100 名 ");
                System.out.println(diffTime.prettyPrint());
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
