package wxdgaming.game.server.script.activity;

import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.game.server.bean.activity.ActivityData;

/**
 * 活动处理器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-11 17:35
 **/
public abstract class AbstractActivityHandler<T extends ActivityData> extends HoldApplicationContext {

    public abstract int activityType();

    public abstract T newData();

    public abstract void heart(T activityData);

    public abstract void heartMinute(T activityData);

    public abstract void heartHour(T activityData);

    public abstract void heartDayEnd(T activityData);

    public abstract void end(T activityData);

}
