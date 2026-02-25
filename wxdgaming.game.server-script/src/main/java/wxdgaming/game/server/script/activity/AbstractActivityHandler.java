package wxdgaming.game.server.script.activity;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.executor.HeartConst;
import wxdgaming.game.server.bean.activity.ActivityData;
import wxdgaming.game.server.event.EventConst;

import java.util.Collection;

/**
 * 活动处理器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-11 17:35
 **/
@Getter
public abstract class AbstractActivityHandler<T extends ActivityData> extends HoldApplicationContext {

    @Autowired protected ActivityService activityService;

    public abstract int activityType();

    /** 当前活动需要的心跳类型 */
    public abstract Collection<HeartConst> heartConst();

    public abstract T newData();

    /** 活动开启时调用 */
    public abstract void start(T activityData);

    /** 活动结束时调用 */
    public abstract void end(T activityData);

    @EventListener
    public final void onRecharge(EventConst.PlayerRechargeEvent event) {
        doRecharge(event);
    }

    public void doRecharge(EventConst.PlayerRechargeEvent event) {}

    /** 心跳处理器大约33ms */
    public void heart(T activityData) {}

    /**每秒*/
    public void heartSecond(T activityData) {}

    public void heartMinute(T activityData) {}

    public void heartHour(T activityData) {}

    public void heartDayEnd(T activityData) {}

    public void heartWeek(T activityData) {}

}
