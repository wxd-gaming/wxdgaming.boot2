package wxdgaming.game.server.script;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.event.InitEvent;
import wxdgaming.boot2.core.executor.HeartDriveHandler;
import wxdgaming.game.server.event.EventConst;
import wxdgaming.game.server.module.system.GameService;

/**
 * 心跳脚本
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-12 15:07
 **/
@Slf4j
@Component
public class MainHeartDriveHandlerScript extends HoldApplicationContext implements HeartDriveHandler {

    final GameService gameService;

    public MainHeartDriveHandlerScript(GameService gameService) {
        this.gameService = gameService;
    }

    @EventListener
    @Order(Integer.MAX_VALUE)
    public void init(InitEvent initEvent) {
        gameService.getMainThreadDrive().setDriveHandler(this);
    }

    @Override public void heart(long millis) {
        applicationContextProvider.postEventIgnoreException(new EventConst.ServerHeartEvent());
    }

    @Override public void heartSecond(int second) {
        applicationContextProvider.postEventIgnoreException(new EventConst.ServerHeartSecondEvent(second));
    }

    @Override public void heartMinute(int minute) {
        applicationContextProvider.postEventIgnoreException(new EventConst.ServerHeartMinuteEvent(minute));
    }

    @Override public void heartHour(int hour) {
        applicationContextProvider.postEventIgnoreException(new EventConst.ServerHeartHourEvent(hour));
    }

    @Override public void heartDayEnd(int dayOfYear) {
        applicationContextProvider.postEventIgnoreException(new EventConst.ServerHeartDayEvent(dayOfYear));
    }

    @Override public void heartWeek(long weekFirstDayStartTime) {
        applicationContextProvider.postEventIgnoreException(new EventConst.ServerHeartWeekEvent(weekFirstDayStartTime));
    }

}
