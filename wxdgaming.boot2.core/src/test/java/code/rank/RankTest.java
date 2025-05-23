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
        for (int k = 0; k < 5; k++) {
            RankMap rankMap = new RankMap();

            DiffTime diffTime = new DiffTime();
            int iCount = 10000;
            for (int i = 0; i < iCount; i++) {
                rankMap.updateScore(String.valueOf(i + 1), RandomUtils.random(100, 1000));
            }
            System.out.println("插入" + iCount + " 对象 " + diffTime.diff() + "ms");
            diffTime.reset();
            for (int i = 0; i < iCount; i++) {
                rankMap.updateScore(String.valueOf(i + 1), RandomUtils.random(100, 1000));
            }
            System.out.println("修改" + iCount + " 对象 " + diffTime.diff() + "ms");
            String random = String.valueOf(RandomUtils.random(1, iCount));
            diffTime.reset();
            int rank = rankMap.rank(random);
            System.out.println("随机读一个 " + random + " 对象 当前排名 " + rank + " - " + diffTime.diff() + "ms ");
            diffTime.reset();
            List<RankScore> topN = rankMap.topN(1000);
            System.out.println("返回前 1000 名 " + diffTime.diff() + "ms");
            diffTime.reset();
            System.out.println("=========================================");
        }
        // for (RankScore rankScore : topN) {
        //     log.info("{}", rankScore);
        // }
    }

}
