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
        RankMap rankMap = new RankMap();

        DiffTime diffTime = new DiffTime();
        for (int i = 0; i < 10000; i++) {
            rankMap.updateScore(String.valueOf(i + 1), RandomUtils.random(100, 1000));
        }
        System.out.println(diffTime.diff() + "ms");
        diffTime.reset();
        for (int i = 0; i < 10000; i++) {
            rankMap.updateScore(String.valueOf(i + 1), RandomUtils.random(100, 1000));
        }
        System.out.println(diffTime.diff() + "ms");
        diffTime.reset();
        int rank = rankMap.rank("1000");
        System.out.println(diffTime.diff() + "ms " + rank);
        diffTime.reset();
        List<RankScore> topN = rankMap.getTopN(1000);
        System.out.println(diffTime.diff() + "ms");
        diffTime.reset();
        for (RankScore rankScore : topN) {
            log.info("{}", rankScore);
        }
    }

}
