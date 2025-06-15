package wxdgaming.game.server.script.fight;

import com.google.inject.Inject;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.game.server.bean.MapNpc;

/**
 * 战斗处理
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-09 09:48
 **/
public abstract class AbstractFightAction extends HoldRunApplication {

    @Inject protected FightService fightService;

    public abstract int type();

    public abstract void doAction(MapNpc mapNpc, Object... args);

}
