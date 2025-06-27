package code.rank;

import wxdgaming.boot2.core.rank.RankByLimitSet;
import wxdgaming.boot2.core.util.RandomUtils;

public class RankByLimitSetTest {


    public static void main(String[] args) {

        RankByLimitSet rankByLimitSet = new RankByLimitSet(10);

        for (int i = 0; i < 50; i++) {
            rankByLimitSet.updateScore(String.valueOf(i + 1), RandomUtils.random(1, 20));
        }

        int rank = rankByLimitSet.rank("49", 1);
        System.out.println(rank);

    }

}
