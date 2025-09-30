package wxdgaming.game.server.script;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.executor.HeartDriveHandler;
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

    @Init
    @Order(Integer.MAX_VALUE)
    public void init() {
        gameService.getMainThreadDrive().setDriveHandler(this);
    }

    @Override public void heart(long millis) {

    }

    @Override public void heartSecond(int second) {

    }

    @Override public void heartMinute(int minute) {

    }

    @Override public void heartHour(int hour) {

    }

    @Override public void heartDayEnd(int dayOfYear) {

    }

    @Override public void heartWeek(long weekFirstDayStartTime) {

    }

}
