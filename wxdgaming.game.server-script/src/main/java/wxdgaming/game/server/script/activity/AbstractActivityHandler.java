package wxdgaming.game.server.script.activity;

import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.game.server.bean.activity.ActivityData;
import wxdgaming.game.server.bean.activity.HeartConst;

import java.util.Collection;

/**
 * 活动处理器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-11 17:35
 **/
public abstract class AbstractActivityHandler<T extends ActivityData> extends HoldApplicationContext {

    public abstract int activityType();

    public abstract Collection<HeartConst> heartConst();

    public abstract T newData();

    public abstract void start(T activityData);

    public abstract void end(T activityData);

    public void heart(T activityData) {}

    public void heartSecond(T activityData) {}

    public void heartMinute(T activityData) {}

    public void heartHour(T activityData) {}

    public void heartDayEnd(T activityData) {}

}
