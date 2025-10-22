package wxdgaming.game.server.script.fight;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.event.InitEvent;
import wxdgaming.game.server.bean.reason.ReasonDTO;
import wxdgaming.game.server.bean.MapNpc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 战斗
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-08 10:16
 **/
@Slf4j
@Service
public class FightService extends HoldApplicationContext {

    Map<Integer, AbstractFightAction> actionImplMap = new HashMap<>();

    @EventListener
    public void init(InitEvent initEvent) {
        actionImplMap = applicationContextProvider.toMap(AbstractFightAction.class, AbstractFightAction::type);
    }

    public void changeHp(MapNpc mapNpc, long change, ReasonDTO reasonDTO) {
        long oldHp = mapNpc.getHp();
        long maxHp = mapNpc.maxHp();
        if (oldHp >= maxHp) {
            return;
        }
        mapNpc.setHp(oldHp + change);
        if (mapNpc.getHp() > maxHp) {
            mapNpc.setHp(maxHp);
        }
        if (mapNpc.getHp() < 0) {
            mapNpc.setHp(0);
        }
        log.info("{} 改变血量 {} -> {} -> {}, maxMp={}, {}", mapNpc, oldHp, change, mapNpc.getHp(), maxHp, reasonDTO);
    }

    public void changeMp(MapNpc player, long change, ReasonDTO reasonDTO) {
        long oldMp = player.getMp();
        long maxMp = player.maxMp();
        if (oldMp >= maxMp) {
            return;
        }
        player.setMp(oldMp + change);
        if (player.getMp() > maxMp) {
            player.setMp(maxMp);
        }
        if (player.getMp() < 0) {
            player.setMp(0);
        }
        log.info("{} 改变魔量 {} -> {} -> {}, maxMp={}, {}", player, oldMp, change, player.getMp(), maxMp, reasonDTO);
    }

    public List<MapNpc> selectAttack(MapNpc player) {
        return null;
    }

}
