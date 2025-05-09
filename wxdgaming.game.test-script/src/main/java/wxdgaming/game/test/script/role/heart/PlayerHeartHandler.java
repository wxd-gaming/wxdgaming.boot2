package wxdgaming.game.test.script.role.heart;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.threading.ThreadContext;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.event.*;

/**
 * 角色的心跳执行
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-08 13:26
 **/
@Slf4j
@Singleton
public class PlayerHeartHandler {

    @OnHeart
    public void onHeart(Player player, long mille) {
        // log.info("onHeart:{}", player);
    }

    @OnHeartSecond
    public void onHeartSecond(Player player, int second) {
        log.info("onHeartSecond: {} {} {}", second, player, ThreadContext.context().queueName());
    }

    @OnHeartMinute
    public void onHeartMinute(Player player, int minute) {
        log.info("onHeartMinute:{} {}", player, ThreadContext.context().queueName());
    }

    @OnHeartHour
    public void onHeartHour(Player player, int hour) {
        log.info("onHeartHour:{} {}", player, ThreadContext.context().queueName());
    }

    @OnHeartDay
    public void onHeartDay(Player player, int day) {
        log.info("onHeartDay:{} {}", player, ThreadContext.context().queueName());
    }

}
