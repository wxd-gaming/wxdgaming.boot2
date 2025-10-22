package wxdgaming.game.server.script.role.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.executor.ThreadContext;
import wxdgaming.boot2.core.lang.condition.Condition;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.game.server.bean.MapNpc;
import wxdgaming.game.server.bean.reason.ReasonConst;
import wxdgaming.game.server.bean.reason.ReasonDTO;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.entity.role.OnlineInfo;
import wxdgaming.game.server.event.EventConst;
import wxdgaming.game.server.event.OnTask;
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

    @EventListener
    public void onHeart(EventConst.MapNpcHeartEvent event) {
        MapNpc mapNpc = event.mapNpc();
        Player player = (Player) mapNpc;
        // log.info("onHeart:{}", mapNpc);
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

    final ReasonDTO reasonDTO = ReasonDTO.of(ReasonConst.Heart, "心跳回血");

    @EventListener
    public void onHeartSecond(EventConst.MapNpcHeartSecondEvent event) {
        int second = event.second();
        MapNpc mapNpc = event.mapNpc();
        Player player = (Player) mapNpc;
        if (second % 10 == 0) {
            if (player.getHp() < player.maxHp()) {
                fightService.changeHp(player, 10, reasonDTO);
            }
        }
        long millis = MyClock.millis();
        {
            /*记录在线时长*/
            OnlineInfo onlineInfo = player.getOnlineInfo();
            long diff = millis - onlineInfo.getLastUpdateOnlineTime();
            onlineInfo.setOnlineMills(onlineInfo.getOnlineMills() + diff);
            onlineInfo.setOnlineTotalMills(onlineInfo.getOnlineTotalMills() + diff);
            onlineInfo.setLastUpdateOnlineTime(millis);
            applicationContextProvider.executeMethodWithAnnotatedException(OnTask.class, player, new Condition("onlineTime", diff));
        }

    }

    @EventListener
    public void onHeartMinute(EventConst.MapNpcHeartMinuteEvent event) {
        Player player = event.player();
        log.info("onHeartMinute:{} {}", player, ThreadContext.context().queueName());
    }

    @EventListener
    public void onHeartHour(EventConst.MapNpcHeartHourEvent event) {
        Player player = event.player();
        log.info("onHeartHour:{} {}", player, ThreadContext.context().queueName());
    }

    @EventListener
    public void onHeartDay(EventConst.MapNpcHeartDayEvent event) {
        Player player = event.player();
        log.info("onHeartDay:{} {}", player, ThreadContext.context().queueName());
    }

}
