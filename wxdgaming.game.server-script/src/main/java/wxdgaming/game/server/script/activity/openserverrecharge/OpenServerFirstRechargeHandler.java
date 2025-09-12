package wxdgaming.game.server.script.activity.openserverrecharge;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.game.server.bean.activity.ActivityData;
import wxdgaming.game.server.bean.activity.HeartConst;
import wxdgaming.game.server.script.activity.AbstractActivityHandler;

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
        return 1;
    }

    @Override public ActivityData newData() {
        return new ActivityData();
    }

    @Override public void start(ActivityData activityData) {
        activityData.getHeartConstSet().add(HeartConst.Heart);
    }

    @Override public void heart(ActivityData activityData) {
        log.info("OpenServerFirstRechargeHandler.heart");
    }

    @Override public void heartMinute(ActivityData activityData) {
        log.info("OpenServerFirstRechargeHandler.heartMinute");
    }

    @Override public void heartHour(ActivityData activityData) {

    }

    @Override public void heartDayEnd(ActivityData activityData) {

    }

    @Override public void end(ActivityData activityData) {

    }
}
