package wxdgaming.game.server.script.role;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.lang.condition.Condition;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.game.common.slog.SlogService;
import wxdgaming.game.message.role.ResLogin;
import wxdgaming.game.message.role.ResUpdateExp;
import wxdgaming.game.message.role.ResUpdateLevel;
import wxdgaming.game.message.role.RoleBean;
import wxdgaming.game.server.bean.reason.ReasonDTO;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.event.EventConst;
import wxdgaming.game.server.event.OnTask;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.script.role.slog.RoleLvSlog;

import java.util.HashSet;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-22 11:44
 **/
@Slf4j
@Service
public class PlayerService extends HoldApplicationContext {

    final DataCenterService dataCenterService;
    final SlogService slogService;

    public PlayerService(DataCenterService dataCenterService, SlogService slogService) {
        this.dataCenterService = dataCenterService;
        this.slogService = slogService;
    }


    public void sendPlayerList(SocketSession socketSession, Integer sid, String account) {
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
        socketSession.write(resLogin);
        log.info("clientSession={}, sid={}, account={} 发送角色列表:{}", socketSession, sid, account, resLogin);
    }

    public void addExp(Player player, long exp, ReasonDTO reasonDTO) {
        log.info("{} 当前经验：{} 增加经验:{}, {}", player, player.getExp(), exp, reasonDTO);
        long tmp = player.getExp() + exp;
        while (tmp >= 100L * player.getLevel()) {
            /*假设升级需要100*/
            tmp -= 100L * player.getLevel();
            addLevel(player, 1, reasonDTO.copyFrom("经验升级"));
        }
        setExp(player, tmp, reasonDTO);
    }

    public void setExp(Player player, long exp, ReasonDTO reasonDTO) {
        player.setExp(exp);
        ResUpdateExp resUpdateLevel = new ResUpdateExp()
                .setExp(player.getExp())
                .setReason(reasonDTO.getReasonConst().name());
        player.write(resUpdateLevel);
    }


    public void addLevel(Player player, int changeLv, ReasonDTO reasonDTO) {
        int oldLevel = player.getLevel();
        player.setLevel(player.getLevel() + changeLv);
        log.info("{} 等级变更: oldLv={} change={} newLv={}, {}", player, oldLevel, changeLv, player.getLevel(), reasonDTO);

        RoleLvSlog roleLvLog = new RoleLvSlog(player, reasonDTO.getReasonText());
        slogService.pushLog(roleLvLog);

        ResUpdateLevel resUpdateLevel = new ResUpdateLevel()
                .setLevel(player.getLevel())
                .setReason(reasonDTO.getReasonConst().name());

        player.write(resUpdateLevel);
        /*触发升级, 比如功能开放监听需要*/
        applicationContextProvider.postEventIgnoreException(new EventConst.LevelUpEvent(player, changeLv));
        /*触发当前等级*/
        applicationContextProvider.executeMethodWithAnnotatedException(OnTask.class, player, new Condition("level", player.getLevel()));
        /*触发提升等级*/
        applicationContextProvider.executeMethodWithAnnotatedException(OnTask.class, player, new Condition("levelup", changeLv));
    }

}
