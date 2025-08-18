package wxdgaming.game.server.script.role.event;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.Order;
import wxdgaming.boot2.core.executor.ThreadContext;
import wxdgaming.boot2.core.lang.bit.BitFlag;
import wxdgaming.boot2.core.lang.condition.Condition;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.game.server.bean.StatusConst;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.event.OnLogin;
import wxdgaming.game.server.event.OnLoginBefore;
import wxdgaming.game.server.event.OnTask;

/**
 * 角色创建事件
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-27 19:51
 **/
@Slf4j
@Singleton
public class PlayerLoginHandler extends HoldRunApplication {

    @Inject
    public PlayerLoginHandler() {
    }

    /** 创建角色之后赠送初始化道具 */
    @Order(1)
    @OnLoginBefore
    public void onLoginBefore(Player player) {
        log.info("玩家上线:{} {}", ThreadContext.context().queueName(), player);
        player.setStatus(new BitFlag());
        /*触发任务登录次数*/
        runApplication.executeMethodWithAnnotatedException(OnTask.class, player, new Condition("login", 1));
        if (!MyClock.isSameDay(player.getOnlineInfo().getLastLoginDayTime())) {
            player.getOnlineInfo().setLastLoginDayTime(MyClock.millis());
            /*触发任务登录天数*/
            runApplication.executeMethodWithAnnotatedException(OnTask.class, player, new Condition("loginDay", 1));
        }
    }

    /** 创建角色之后赠送初始化道具 */
    @Order(1)
    @OnLogin
    public void onLogin(Player player) {
        log.info("玩家上线:{} {}", ThreadContext.context().queueName(), player);
        player.getStatus().addFlags(StatusConst.Online);
        player.getOnlineInfo().setLastLoginTime(MyClock.millis());
    }


}
