package wxdgaming.game.server.script.fight.impl.mp;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.server.bean.MapNpc;
import wxdgaming.game.server.script.fight.AbstractFightAction;

/**
 * 固定值扣血
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-09 09:47
 **/
@Slf4j
@Singleton
public class CostMpFixedImpl extends AbstractFightAction {

    @Override public int type() {
        return 400;
    }

    @Override public void doAction(MapNpc mapNpc, Object... args) {

    }

}
