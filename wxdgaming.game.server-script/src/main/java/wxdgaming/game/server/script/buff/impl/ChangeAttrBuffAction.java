package wxdgaming.game.server.script.buff.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.bean.buff.BuffType;
import wxdgaming.game.bean.buff.BuffTypeConst;
import wxdgaming.game.cfg.bean.QBuff;
import wxdgaming.game.server.bean.MapNpc;
import wxdgaming.game.server.bean.buff.Buff;
import wxdgaming.game.server.script.buff.AbstractBuffAction;

/**
 * 处理属性变更
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-15 13:28
 **/
@Slf4j
@Component
public class ChangeAttrBuffAction extends AbstractBuffAction {

    @Override public BuffType buffType() {
        return BuffTypeConst.ChangeAttr;
    }

    @Override public void doAction(MapNpc mapNpc, Buff buff, QBuff qBuff) {

    }

}
