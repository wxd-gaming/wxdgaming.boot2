package wxdgaming.game.server.script.fight.impl.hp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.server.bean.MapNpc;
import wxdgaming.game.server.script.fight.AbstractFightAction;

/**
 * 固定值加血
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-09 09:47
 **/
@Slf4j
@Component
public class AddHpFixedImpl extends AbstractFightAction {

    @Override public int type() {
        return 100;
    }

    @Override public void doAction(MapNpc mapNpc, Object... args) {

    }

}
