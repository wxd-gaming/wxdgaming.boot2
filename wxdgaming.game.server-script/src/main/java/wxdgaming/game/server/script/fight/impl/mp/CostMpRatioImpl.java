package wxdgaming.game.server.script.fight.impl.mp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.server.bean.MapNpc;
import wxdgaming.game.server.script.fight.AbstractFightAction;

/**
 * 百分比扣血
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-09 09:47
 **/
@Slf4j
@Component
public class CostMpRatioImpl extends AbstractFightAction {

    @Override public int type() {
        return 402;
    }

    @Override public void doAction(MapNpc mapNpc, Object... args) {

    }

}
