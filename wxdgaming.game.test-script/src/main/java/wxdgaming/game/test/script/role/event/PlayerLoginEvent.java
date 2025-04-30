package wxdgaming.game.test.script.role.event;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.Order;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.script.event.OnLogin;
import wxdgaming.game.test.script.event.OnLoginBefore;
import wxdgaming.game.test.script.event.OnTask;
import wxdgaming.game.test.script.task.TaskEvent;

/**
 * 角色创建事件
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-27 19:51
 **/
@Slf4j
@Singleton
public class PlayerLoginEvent extends HoldRunApplication {

    @Inject
    public PlayerLoginEvent() {
    }

    /** 创建角色之后赠送初始化道具 */
    @Order(1)
    @OnLoginBefore
    public void onLoginBefore(Player player) {
        log.info("玩家上线:{}, {}", player, player.getSocketSession());
        /*触发任务登录次数*/
        runApplication.executeMethodWithAnnotatedException(OnTask.class, player, TaskEvent.builder().k1("login").targetValue(1).build());
        if (!MyClock.isSameDay(player.getLastLoginTime())) {
            player.setLastLoginTime(MyClock.millis());
            /*触发任务登录天数*/
            runApplication.executeMethodWithAnnotatedException(OnTask.class, player, TaskEvent.builder().k1("loginDay").targetValue(1).build());
        }
    }

    /** 创建角色之后赠送初始化道具 */
    @Order(1)
    @OnLogin
    public void onLogin(Player player) {
        log.info("玩家上线:{}, {}", player, player.getSocketSession());

    }


}
