package code.rank;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.lang.DiffTime;
import wxdgaming.boot2.core.rank.RankMap;
import wxdgaming.boot2.core.rank.RankScore;
import wxdgaming.boot2.core.util.RandomUtils;

import java.util.List;

/**
 * 测试
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-20 21:08
 **/
@Slf4j
public class RankTest {

    public static void main(String[] args) {
        List<RankScore> topN = List.of();
        for (int k = 0; k < 5; k++) {
            RankMap rankMap = new RankMap();

            DiffTime diffTime = new DiffTime();
            int iCount = 10000;
            for (int i = 0; i < iCount; i++) {
                rankMap.updateScore(String.valueOf(i + 1), RandomUtils.random(100, 1000));
            }
            System.out.println("插入" + iCount + " 对象 " + diffTime.diffMs5() + "ms");
            diffTime.reset();
            for (int i = 0; i < iCount; i++) {
                rankMap.updateScore(String.valueOf(i + 1), RandomUtils.random(100, 1000));
            }
            System.out.println("修改" + iCount + " 对象 " + diffTime.diffMs5() + "ms");
            String random = String.valueOf(RandomUtils.random(1, iCount));
            {
                diffTime.reset();
                rankMap.updateScore(random, RandomUtils.random(100, 1000));
                System.out.println("随机修改一个对象 " + random + " - " + diffTime.diffMs5() + "ms ");
            }
            {
                diffTime.reset();
                int rank = rankMap.rank(random);
                System.out.println("随机读取一个对象 " + random + " 当前排名 " + rank + " - " + diffTime.diffMs5() + "ms ");
            }
            {
                diffTime.reset();
                int rank = RandomUtils.random(1, 10);
                RankScore rankScore = rankMap.topByRank(rank);
                System.out.println("随机读排名 " + rank + " 对象 " + rankScore.getKey() + " - " + diffTime.diffMs5() + "ms ");
            }
            diffTime.reset();
            topN = rankMap.topN(100);
            System.out.println("返回前 100 名 " + diffTime.diffMs5() + "ms");
            diffTime.reset();
            System.out.println("=========================================");
        }
        for (RankScore rankScore : topN) {
            log.info("{}", rankScore);
        }
    }

}
