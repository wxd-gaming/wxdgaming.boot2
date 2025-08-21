package wxdgaming.game.server.script.role.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.executor.ThreadContext;
import wxdgaming.boot2.core.lang.condition.Condition;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.game.basic.core.Reason;
import wxdgaming.game.basic.core.ReasonDTO;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.event.*;
import wxdgaming.game.server.script.fight.FightService;

import java.util.concurrent.BlockingQueue;

/**
 * 角色的心跳执行
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-08 13:26
 **/
@Slf4j
@Component
public class PlayerHeartHandler extends HoldApplicationContext {

    final FightService fightService;

    public PlayerHeartHandler(FightService fightService) {
        this.fightService = fightService;
    }

    @OnHeart
    public void onHeart(Player player, long mille) {
        // log.info("onHeart:{}", player);
        BlockingQueue<Runnable> eventList = player.getEventList();
        for (int i = 0; i < 30; i++) {
            /*相当于每帧最多处理30个事件*/
            if (eventList.isEmpty()) break;
            Runnable runnable = eventList.poll();
            try {
                runnable.run();
            } catch (Exception e) {
                log.error("onHeart:{}", e.getMessage(), e);
            }
        }
    }

    final ReasonDTO reasonDTO = ReasonDTO.of(Reason.Heart, "心跳回血");

    @OnHeartSecond
    public void onHeartSecond(Player player, int second) {
        if (second % 10 == 0) {
            if (player.getHp() < player.maxHp()) {
                fightService.changeHp(player, 10, reasonDTO);
            }
        }
        long millis = MyClock.millis();
        {
            /*记录在线时长*/
            long diff = millis - player.getOnlineInfo().getLastUpdateOnlineTime();
            player.getOnlineInfo().setOnlineMills(player.getOnlineInfo().getOnlineMills() + diff);
            player.getOnlineInfo().setOnlineTotalMills(player.getOnlineInfo().getOnlineTotalMills() + diff);
            player.getOnlineInfo().setLastUpdateOnlineTime(millis);
            applicationContextProvider.executeMethodWithAnnotatedException(OnTask.class, player, new Condition("onlineTime", diff));
        }

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
