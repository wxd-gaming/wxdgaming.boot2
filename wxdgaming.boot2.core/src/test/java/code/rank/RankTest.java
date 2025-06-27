package code.rank;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.executor.ExecutorServicePlatform;
import wxdgaming.boot2.core.lang.DiffTime;
import wxdgaming.boot2.core.rank.RankByGroupMap;
import wxdgaming.boot2.core.rank.RankScore;
import wxdgaming.boot2.core.util.RandomUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 测试
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-20 21:08
 **/
@Slf4j
public class RankTest {

    public static void main(String[] args) {
        RankByGroupMap rankByGroupMap = new RankByGroupMap();
        ExecutorServicePlatform executorService = ExecutorFactory.create("map", 3);
        for (int k = 0; k < 10; k++) {
            executorService.execute(() -> {
                StringBuilder stringBuilder = new StringBuilder();

                DiffTime diffTime = new DiffTime();
                int iCount = 1000;
                int maxRandom = 5;
                for (int i = 0; i < iCount; i++) {
                    rankByGroupMap.updateScore(String.valueOf(i + 1), RandomUtils.random(1, maxRandom));
                }
                stringBuilder.append("插入 " + iCount + " 对象 " + diffTime.diffUs5() + "us").append("\n");
                diffTime.reset();
                for (int i = 0; i < iCount; i++) {
                    rankByGroupMap.updateScore(String.valueOf(i + 1), RandomUtils.random(1, maxRandom));
                }
                stringBuilder.append("修改 " + iCount + " 对象 " + diffTime.diffUs5() + "us").append("\n");
                String random = String.valueOf(RandomUtils.random(1, iCount));
                {
                    diffTime.reset();
                    rankByGroupMap.updateScore(random, RandomUtils.random(1, maxRandom));
                    stringBuilder.append("随机修改一个对象 " + random + " - " + diffTime.diffUs5() + "us").append("\n");
                }
                {
                    diffTime.reset();
                    int rank = rankByGroupMap.rank(random);
                    stringBuilder.append("随机读取一个对象 " + random + " 排名 " + rank + " - " + diffTime.diffUs5() + "us").append("\n");
                    diffTime.reset();
                    RankScore rankScore = rankByGroupMap.rankDataByRank(rank);
                    stringBuilder.append("随机读取一个排名 " + rank + " 对象 " + rankScore.getKey() + " - " + diffTime.diffUs5() + "us").append("\n");
                }
                diffTime.reset();
                rankByGroupMap.rankBySize(100);
                stringBuilder.append("返回前 100 名 " + diffTime.diffUs5() + "us").append("\n");
                stringBuilder.append("=========================================").append("\n");
                System.out.println(stringBuilder.toString());
            });
        }
        // for (RankScore rankScore : topN) {
        //     log.info("{}", rankScore);
        // }

        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(10));
        System.exit(0);
    }

}
