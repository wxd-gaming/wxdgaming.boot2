package wxdgaming.game.server.script.buff;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import wxdgaming.game.bean.buff.BuffType;
import wxdgaming.game.bean.buff.BuffTypeConst;
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
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-15 13:24
 **/
@Slf4j
public abstract class AbstractBuffAction {

    @Autowired protected BuffService buffService;
    @Autowired protected DataCenterService dataCenterService;
    @Autowired protected FightService fightService;
    @Autowired protected PlayerService playerService;
    @Autowired protected PlayerAttributeService playerAttributeService;
    @Autowired protected NpcAttributeService npcAttributeService;

    public BuffType buffType() {
        return BuffTypeConst.None;
    }

    public void doAction(MapNpc mapNpc, Buff buff, QBuff qBuff) {}

}
