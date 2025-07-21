package code;

import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.lang.LNumHeart;
import wxdgaming.boot2.core.timer.MyClock;

import java.util.concurrent.TimeUnit;

/**
 * test
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-07-21 16:35
 **/
public class LNumHeartTest {

    @Test
    public void test1() {
        LNumHeart lNumHeart = new LNumHeart();
        long millis = MyClock.millis();
        lNumHeart.setLUTime(millis - TimeUnit.HOURS.toMillis(3));
        lNumHeart.heartTimer(1000, millis, TimeUnit.HOURS.toMillis(1));
        System.out.println("当前收益次数：" + lNumHeart.getNum());
    }

}
