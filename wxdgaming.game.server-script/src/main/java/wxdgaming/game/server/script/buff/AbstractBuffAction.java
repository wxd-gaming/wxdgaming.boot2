package wxdgaming.game.server.script.buff;

import com.google.inject.Inject;
import wxdgaming.game.bean.buff.BuffType;
import wxdgaming.game.cfg.bean.QBuff;
import wxdgaming.game.server.bean.MapNpc;
import wxdgaming.game.server.bean.buff.Buff;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.script.attribute.NpcAttributeService;
import wxdgaming.game.server.script.attribute.PlayerAttributeService;
import wxdgaming.game.server.script.fight.FightService;
import wxdgaming.game.server.script.role.PlayerService;

/**
 * buff执行抽象
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-15 13:24
 **/
public abstract class AbstractBuffAction {

    @Inject protected BuffService buffService;
    @Inject protected DataCenterService dataCenterService;
    @Inject protected FightService fightService;
    @Inject protected PlayerService playerService;
    @Inject protected PlayerAttributeService playerAttributeService;
    @Inject protected NpcAttributeService npcAttributeService;

    public abstract BuffType buffType();

    public abstract void doAction(MapNpc mapNpc, Buff buff, QBuff qBuff);

}
