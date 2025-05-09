package wxdgaming.game.test.script.fight;

import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.game.test.bean.MapNpc;

/**
 * 战斗处理
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-09 09:48
 **/
public abstract class FightAction extends HoldRunApplication {

    protected FightService fightService;

    @Init
    public void initBean(FightService fightService) {
        this.fightService = fightService;
    }

    public abstract int type();

    public abstract void doAction(MapNpc mapNpc, Object... args);

}
