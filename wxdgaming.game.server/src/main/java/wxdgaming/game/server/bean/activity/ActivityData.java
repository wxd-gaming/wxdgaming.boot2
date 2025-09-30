package wxdgaming.game.server.bean.activity;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.core.timer.MyClock;

/**
 * 活动数据
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-11 17:33
 **/
@Getter
@Setter
public class ActivityData extends ObjectBase {

    private int activityId;
    private int activityType;
    private long startTime;
    private long endTime;

    public void clear() {
        startTime = 0;
        endTime = 0;
    }

    @Override public String toString() {
        return "ActivityData{activityId=%10d, activityType=%5d, startTime=%s, endTime=%s}"
                .formatted(activityId, activityType, MyClock.formatDate(startTime), MyClock.formatDate(endTime));
    }

}
