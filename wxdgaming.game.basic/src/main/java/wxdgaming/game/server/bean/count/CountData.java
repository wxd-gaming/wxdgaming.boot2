package wxdgaming.game.server.bean.count;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.core.timer.MyClock;

import java.util.function.Predicate;

/**
 * 计数
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-10 21:04
 **/
@Getter
@Setter
public class CountData extends ObjectBase {

    private long count;
    private long time;

    public long update(long change) {
        count = Math.addExact(count, change);
        return count;
    }

    public void clear() {
        count = 0;
        time = MyClock.millis();
    }

    public void checkClear(Predicate<Long> predicate) {
        boolean test = predicate.test(getTime());
        if (!test) {
            clear();
        }
    }

}
