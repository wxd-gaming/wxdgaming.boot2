package wxdgaming.game.test.script.fight;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.game.test.bean.MapNpc;
import wxdgaming.game.test.bean.role.Player;

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

    HashMap<Integer, FightAction> actionImplMap = new HashMap<>();

    @Init
    public void init() {
        HashMap<Integer, FightAction> map = new HashMap<>();
        runApplication.classWithSuper(FightAction.class)
                .forEach(impl -> {
                    FightAction old = map.put(impl.type(), impl);
                    AssertUtil.assertTrue(old == null, "重复的战斗动作类型" + impl.type());
                });
        actionImplMap = map;
    }

    public void changeHp(Player player, long change) {

    }

    public List<MapNpc> selectAttack(MapNpc player) {
        return null;
    }

}
