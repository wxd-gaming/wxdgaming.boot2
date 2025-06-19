package wxdgaming.game.server.script.role;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.lang.condition.Condition;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.game.core.ReasonArgs;
import wxdgaming.game.message.role.ResLogin;
import wxdgaming.game.message.role.ResUpdateExp;
import wxdgaming.game.message.role.ResUpdateLevel;
import wxdgaming.game.message.role.RoleBean;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.event.OnLevelUp;
import wxdgaming.game.server.event.OnTask;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.script.inner.InnerService;

import java.util.HashSet;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-22 11:44
 **/
@Slf4j
@Singleton
public class PlayerService extends HoldRunApplication {

    final InnerService innerService;
    final DataCenterService dataCenterService;

    @Inject
    public PlayerService(InnerService innerService, DataCenterService dataCenterService) {
        this.innerService = innerService;
        this.dataCenterService = dataCenterService;
    }


    public void sendPlayerList(SocketSession socketSession, long clientSessionId, Integer sid, String account) {
        HashSet<Long> longs = dataCenterService.getAccount2RidsMap().get(sid, account);
        ResLogin resLogin = new ResLogin();
        if (longs != null) {
            for (Long rid : longs) {
                Player player = dataCenterService.getPlayer(rid);
                RoleBean roleBean = new RoleBean().setRid(rid).setName(player.getName()).setLevel(player.getLevel());
                resLogin.getRoles().add(roleBean);
            }
        }
        resLogin.setSid(sid);
        resLogin.setAccount(account);
        resLogin.setUserId(account);
        innerService.forwardMessage(socketSession, clientSessionId, resLogin, reqForwardMessage -> {
            reqForwardMessage.getKvBeansMap().put("account", account);
        });
        log.info("clientSessionId={}, sid={}, account={} 发送角色列表:{}", clientSessionId, sid, account, resLogin);
    }

    public void addExp(Player player, long exp, ReasonArgs reasonArgs) {
        log.info("{} 当前经验：{} 增加经验:{}, {}", player, player.getExp(), exp, reasonArgs);
        long tmp = player.getExp() + exp;
        while (tmp >= 100L * player.getLevel()) {
            /*假设升级需要100*/
            tmp -= 100L * player.getLevel();
            addLevel(player, 1, reasonArgs.copyFrom("经验升级"));
        }
        setExp(player, tmp, reasonArgs);
    }

    public void setExp(Player player, long exp, ReasonArgs reasonArgs) {
        player.setExp(exp);
        ResUpdateExp resUpdateLevel = new ResUpdateExp()
                .setExp(player.getExp())
                .setReason(reasonArgs.getReason().name());
        player.write(resUpdateLevel);
    }


    public void addLevel(Player player, int lv, ReasonArgs reasonArgs) {
        int oldLevel = player.getLevel();
        player.setLevel(player.getLevel() + lv);
        log.info("{} 等级变更: oldLv={} change={} newLv={}, {}", player, oldLevel, lv, player.getLevel(), reasonArgs);

        ResUpdateLevel resUpdateLevel = new ResUpdateLevel()
                .setLevel(player.getLevel())
                .setReason(reasonArgs.getReason().name());

        player.write(resUpdateLevel);
        /*触发升级, 比如功能开放监听需要*/
        runApplication.executeMethodWithAnnotatedException(OnLevelUp.class, player, lv);
        /*触发当前等级*/
        runApplication.executeMethodWithAnnotatedException(OnTask.class, player, new Condition("level", player.getLevel()));
        /*触发提升等级*/
        runApplication.executeMethodWithAnnotatedException(OnTask.class, player, new Condition("levelup", lv));
    }

}
