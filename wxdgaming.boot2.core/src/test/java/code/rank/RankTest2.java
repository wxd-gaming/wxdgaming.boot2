package code.rank;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.lang.DiffTime;
import wxdgaming.boot2.core.rank.RankMap2;
import wxdgaming.boot2.core.rank.RankScore;
import wxdgaming.boot2.core.util.RandomUtils;

/**
 * 测试
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-20 21:08
 **/
@Slf4j
public class RankTest2 {

    public static void main(String[] args) {
        RankMap2 rankMap = new RankMap2();
        for (int k = 0; k < 5; k++) {
            Thread.ofPlatform().start(() -> {
                StringBuilder stringBuilder = new StringBuilder();

                DiffTime diffTime = new DiffTime();
                int iCount = 10000;
                for (int i = 0; i < iCount; i++) {
                    rankMap.updateScore(String.valueOf(i + 1), RandomUtils.random(100, 1000));
                }
                stringBuilder.append("插入 " + iCount + " 对象 " + diffTime.diffUs5() + "us").append("\n");
                diffTime.reset();
                for (int i = 0; i < iCount; i++) {
                    rankMap.updateScore(String.valueOf(i + 1), RandomUtils.random(100, 1000));
                }
                stringBuilder.append("修改 " + iCount + " 对象 " + diffTime.diffUs5() + "us").append("\n");
                String random = String.valueOf(RandomUtils.random(1, iCount));
                {
                    diffTime.reset();
                    rankMap.updateScore(random, RandomUtils.random(100, 1000));
                    stringBuilder.append("随机修改一个对象 " + random + " - " + diffTime.diffUs5() + "us").append("\n");
                }
                {
                    diffTime.reset();
                    int rank = rankMap.rank(random);
                    stringBuilder.append("随机读取一个对象 " + random + " 排名 " + rank + " - " + diffTime.diffUs5() + "us").append("\n");
                }
                {
                    diffTime.reset();
                    int rank = RandomUtils.random(1, iCount);
                    RankScore rankScore = rankMap.rankDataByRank(rank);
                    stringBuilder.append("随机读取一个排名 " + rank + " 对象 " + rankScore.getKey() + " - " + diffTime.diffUs5() + "us").append("\n");
                }
                diffTime.reset();
                rankMap.rankBySize(100);
                stringBuilder.append("返回前 100 名 " + diffTime.diffUs5() + "us").append("\n");
                stringBuilder.append("=========================================").append("\n");
                System.out.println(stringBuilder.toString());
            });
        }
        // for (RankScore rankScore : topN) {
        //     log.info("{}", rankScore);
        // }
    }

}
