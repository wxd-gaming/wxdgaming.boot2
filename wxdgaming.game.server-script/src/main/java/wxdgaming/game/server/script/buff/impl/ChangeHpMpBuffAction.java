package wxdgaming.game.server.script.buff.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.lang.ConfigString;
import wxdgaming.game.basic.core.Reason;
import wxdgaming.game.basic.core.ReasonDTO;
import wxdgaming.game.bean.attr.AttrInfo;
import wxdgaming.game.bean.attr.AttrType;
import wxdgaming.game.bean.buff.BuffType;
import wxdgaming.game.bean.buff.BuffTypeConst;
import wxdgaming.game.cfg.bean.QBuff;
import wxdgaming.game.server.bean.MapNpc;
import wxdgaming.game.server.bean.buff.Buff;
import wxdgaming.game.server.script.buff.AbstractBuffAction;

/**
 * 处理血量变更
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-15 13:28
 **/
@Slf4j
@Component
public class ChangeHpMpBuffAction extends AbstractBuffAction {

    @Override public BuffType buffType() {
        return BuffTypeConst.ChangeHpMp;
    }

    @Override public void doAction(MapNpc mapNpc, Buff buff, QBuff qBuff) {
        ConfigString paramString1 = qBuff.getParamString1();
        AttrInfo objectByFunction = paramString1.get(AttrInfo.JsonParse);
        ReasonDTO reasonDTO = ReasonDTO.of(Reason.Buff, "buff", qBuff.getId(), "sendUid", buff.getSendUid());
        if (qBuff.getParamInt1() == 0/*TODO 固定值*/) {
            Long hp = objectByFunction.get(AttrType.HP);
            if (hp != null) {
                fightService.changeHp(mapNpc, hp, reasonDTO);
            }
            Long mp = objectByFunction.get(AttrType.MP);
            if (mp != null) {
                fightService.changeMp(mapNpc, mp, reasonDTO);
            }
        } else if (qBuff.getParamInt1() == 1/*TODO 最大血量百分比*/) {
            Long hpPro = objectByFunction.get(AttrType.HP);
            if (hpPro != null) {
                long maxHp = mapNpc.maxHp();
                long hp = maxHp * hpPro / 10000;
                fightService.changeHp(mapNpc, hp, reasonDTO);
            }
            Long mpPro = objectByFunction.get(AttrType.MP);
            if (mpPro != null) {
                long maxMp = mapNpc.maxMp();
                long mp = maxMp * mpPro / 10000;
                fightService.changeMp(mapNpc, mp, reasonDTO);
            }
        }
    }

}
