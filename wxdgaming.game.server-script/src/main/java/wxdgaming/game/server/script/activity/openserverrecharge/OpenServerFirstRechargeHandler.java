package wxdgaming.game.server.script.activity.openserverrecharge;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.executor.HeartConst;
import wxdgaming.game.server.bean.activity.ActivityData;
import wxdgaming.game.server.event.EventConst;
import wxdgaming.game.server.script.activity.AbstractActivityHandler;
import wxdgaming.game.server.script.activity.ActivityConst;

import java.util.Collection;
import java.util.List;

/**
 * 开服首充
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-11 17:39
 **/
@Slf4j
@Service
public class OpenServerFirstRechargeHandler extends AbstractActivityHandler<ActivityData> {

    public OpenServerFirstRechargeHandler() {

    }

    @Override public int activityType() {
        return ActivityConst.OPEN_SERVER_RECHARGE;
    }

    @Override public Collection<HeartConst> heartConst() {
        return List.of(HeartConst.Heart, HeartConst.Second, HeartConst.Minute);
    }

    @Override public ActivityData newData() {
        return new ActivityData();
    }

    @Override public void start(ActivityData activityData) {
    }

    @Override public void end(ActivityData activityData) {

    }

    @Override public void heart(ActivityData activityData) {

    }

    @Override public void heartSecond(ActivityData activityData) {
        //        log.info("OpenServerFirstRechargeHandler.heartSecond");
    }

    @Override public void heartMinute(ActivityData activityData) {
        log.info("OpenServerFirstRechargeHandler.heartMinute");
    }

    @Override public void heartHour(ActivityData activityData) {

    }

    @Override public void heartDayEnd(ActivityData activityData) {

    }

    @Override public void heartWeek(ActivityData activityData) {
        super.heartWeek(activityData);
    }
}
