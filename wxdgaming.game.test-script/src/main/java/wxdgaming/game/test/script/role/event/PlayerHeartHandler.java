package wxdgaming.game.test.script.role.event;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.threading.ThreadContext;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.event.*;
import wxdgaming.game.test.script.fight.FightService;
import wxdgaming.game.test.script.task.TaskEvent;

import java.util.concurrent.BlockingQueue;

/**
 * 角色的心跳执行
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-08 13:26
 **/
@Slf4j
@Singleton
public class PlayerHeartHandler extends HoldRunApplication {

    final FightService fightService;

    @Inject
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

    @OnHeartSecond
    public void onHeartSecond(Player player, int second) {
        // log.info("onHeartSecond: {} {} {}", second, player, ThreadContext.context().queueName());
        if (second % 10 == 0) {
            if (player.getHp() < player.maxHp()) {
                fightService.changeHp(player, 10, "心跳回血");
            }
        }
        long millis = MyClock.millis();
        {
            /*记录在线时长*/
            long diff = millis - player.getLastUpdateOnlineTime();
            player.setOnlineMills(player.getOnlineMills() + diff);
            player.setOnlineTotalMills(player.getOnlineTotalMills() + diff);
            player.setLastUpdateOnlineTime(millis);
            runApplication.executeMethodWithAnnotatedException(OnTask.class, player, TaskEvent.builder().k1("onlineTime").targetValue(diff).build());
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
