package wxdgaming.game.server.script.fight;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.game.server.bean.MapNpc;
import wxdgaming.game.server.bean.role.Player;

import java.util.HashMap;
import java.util.List;

/**
 * 战斗
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-08 10:16
 **/
@Slf4j
@Singleton
public class FightService extends HoldRunApplication {

    HashMap<Integer, AbstractFightAction> actionImplMap = new HashMap<>();

    @Init
    public void init() {
        HashMap<Integer, AbstractFightAction> map = new HashMap<>();
        runApplication.classWithSuper(AbstractFightAction.class)
                .forEach(impl -> {
                    AbstractFightAction old = map.put(impl.type(), impl);
                    AssertUtil.assertTrue(old == null, "重复的战斗动作类型" + impl.type());
                });
        actionImplMap = map;
    }

    public void changeHp(Player player, long change, String msg) {
        long oldHp = player.getHp();
        player.setHp(oldHp + change);
        if (player.getHp() > player.maxHp()) {
            player.setHp(player.maxHp());
        }
        log.info("{} 改变血量 {} -> {} -> {}, 原因: {}", player, oldHp, change, player.getHp(), msg);
        player.sendHp();
    }

    public void changeMp(Player player, long change, String msg) {
        long oldMp = player.getMp();
        player.setMp(oldMp + change);
        if (player.getMp() > player.maxMp()) {
            player.setMp(player.maxMp());
        }
        log.info("{} 改变魔量 {} -> {} -> {}, 原因: {}", player, oldMp, change, player.getMp(), msg);
        player.sendHp();
    }

    public List<MapNpc> selectAttack(MapNpc player) {
        return null;
    }

}
