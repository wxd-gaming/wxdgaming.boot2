package wxdgaming.game.test.script.role;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.io.Objects;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.module.data.DataCenterService;
import wxdgaming.game.test.event.OnLevelUp;
import wxdgaming.game.test.event.OnTask;
import wxdgaming.game.test.script.role.message.ResLogin;
import wxdgaming.game.test.script.role.message.ResUpdateExp;
import wxdgaming.game.test.script.role.message.ResUpdateLevel;
import wxdgaming.game.test.script.role.message.RoleBean;
import wxdgaming.game.test.script.task.TaskEvent;

import java.util.HashSet;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-22 11:44
 **/
@Slf4j
@Singleton
public class PlayerService extends HoldRunApplication {

    final DataCenterService dataCenterService;

    @Inject
    public PlayerService(DataCenterService dataCenterService) {
        this.dataCenterService = dataCenterService;
    }


    public void sendPlayerList(SocketSession socketSession, Integer sid, String account) {
        HashSet<Long> longs = dataCenterService.getAccount2RidsMap().get(sid, account);
        ResLogin resLogin = new ResLogin();
        if (longs != null) {
            for (Long rid : longs) {
                Player player = dataCenterService.player(rid);
                RoleBean roleBean = new RoleBean().setRid(rid).setName(player.getName()).setLevel(player.getLevel());
                resLogin.getRoles().add(roleBean);
            }
        }
        socketSession.write(resLogin);
        log.info("{} {} 发送角色列表:{}", sid, account, resLogin);
    }

    public void addExp(Player player, long exp, Object... args) {
        log.info("{} 当前经验：{} 增加经验:{}, 来源：{}", player, player.getExp(), exp, Objects.toString(args));
        long tmp = player.getExp() + exp;
        while (tmp >= 100L * player.getLevel()) {
            /*假设升级需要100*/
            tmp -= 100L * player.getLevel();
            addLevel(player, 1, args, "经验升级");
        }
        player.setExp(tmp);
        ResUpdateExp resUpdateLevel = new ResUpdateExp().setExp(player.getExp());
        player.write(resUpdateLevel);
    }

    public void addLevel(Player player, int lv, Object... args) {
        log.info("{} 当前等级:{} 增加等级:{}, 来源：{}", player, player.getLevel(), lv, Objects.toString(args));
        player.setLevel(player.getLevel() + lv);
        ResUpdateLevel resUpdateLevel = new ResUpdateLevel().setLevel(player.getLevel());
        player.write(resUpdateLevel);
        /*触发升级, 比如功能开放监听需要*/
        runApplication.executeMethodWithAnnotatedException(OnLevelUp.class, player, lv);
        /*触发当前等级*/
        runApplication.executeMethodWithAnnotatedException(OnTask.class, player, TaskEvent.builder().k1("level").targetValue(player.getLevel()).build());
        /*触发提升等级*/
        runApplication.executeMethodWithAnnotatedException(OnTask.class, player, TaskEvent.builder().k1("levelup").targetValue(lv).build());
    }

}
