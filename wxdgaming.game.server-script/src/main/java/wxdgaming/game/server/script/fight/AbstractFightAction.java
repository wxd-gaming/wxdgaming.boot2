package wxdgaming.game.server.script.fight;

import org.springframework.beans.factory.annotation.Autowired;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.game.server.bean.MapNpc;

/**
 * 战斗处理
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-09 09:48
 **/
public abstract class AbstractFightAction extends HoldApplicationContext {

    @Autowired protected FightService fightService;

    public abstract int type();

    public abstract void doAction(MapNpc mapNpc, Object... args);

}
