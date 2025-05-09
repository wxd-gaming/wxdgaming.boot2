package wxdgaming.game.test.script.fight.impl.mp;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.test.bean.MapNpc;
import wxdgaming.game.test.script.fight.FightAction;

/**
 * 百分比扣血
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-09 09:47
 **/
@Slf4j
@Singleton
public class CostMpRatioImpl extends FightAction {

    @Override public int type() {
        return 402;
    }

    @Override public void doAction(MapNpc mapNpc, Object... args) {

    }

}
