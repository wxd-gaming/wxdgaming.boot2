package wxdgaming.game.server.script.attribute.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.json.FastJsonUtil;
import wxdgaming.boot2.starter.event.Event;
import wxdgaming.game.bean.attr.AttrInfo;
import wxdgaming.game.bean.buff.BuffTypeConst;
import wxdgaming.game.cfg.bean.QBuff;
import wxdgaming.game.server.bean.MapNpc;
import wxdgaming.game.server.bean.MapObject;
import wxdgaming.game.server.bean.buff.Buff;
import wxdgaming.game.server.script.attribute.AbstractCalculatorAction;
import wxdgaming.game.server.bean.attribute.CalculatorType;

import java.util.ArrayList;

/**
 * Buff属性的计算
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-09 19:29
 **/
@Slf4j
@Component
public class BuffCalculatorActionImpl extends AbstractCalculatorAction {

    @Override public MapObject.MapObjectType mapObjectType() {
        return null;
    }

    @Override public CalculatorType calculatorType() {
        return CalculatorType.BUFF;
    }

    @Override public AttrInfo calculate(MapNpc mapNpc, Event event) {
        AttrInfo attrInfo = new AttrInfo();
        ArrayList<Buff> buffs = mapNpc.getBuffs();
        for (int i = 0; i < buffs.size(); i++) {
            Buff buff = buffs.get(i);
            QBuff qBuff = buff.qBuff();
            if (qBuff.getBuffType() == BuffTypeConst.ChangeAttr) {
                AttrInfo objectByFunction = qBuff.getParamString1().get(json -> FastJsonUtil.parse(json, AttrInfo.class));
                attrInfo.append(objectByFunction);
            }
        }
        return attrInfo;
    }

    @Override public AttrInfo calculatePro(MapNpc mapNpc, Event event) {
        AttrInfo attrInfo = new AttrInfo();
        ArrayList<Buff> buffs = mapNpc.getBuffs();
        for (int i = 0; i < buffs.size(); i++) {
            Buff buff = buffs.get(i);
            QBuff qBuff = buff.qBuff();
            if (qBuff.getBuffType() == BuffTypeConst.ChangeAttr) {
                AttrInfo objectByFunction = qBuff.getParamString2().get(json -> FastJsonUtil.parse(json, AttrInfo.class));
                attrInfo.append(objectByFunction);
            }
        }
        return attrInfo;
    }

}
