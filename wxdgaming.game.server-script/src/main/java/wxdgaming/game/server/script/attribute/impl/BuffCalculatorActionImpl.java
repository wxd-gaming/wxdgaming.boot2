package wxdgaming.game.server.script.attribute.impl;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.server.bean.MapNpc;
import wxdgaming.game.server.bean.MapObject;
import wxdgaming.game.server.bean.attr.AttrInfo;
import wxdgaming.game.server.bean.buff.Buff;
import wxdgaming.game.server.script.attribute.CalculatorAction;
import wxdgaming.game.server.script.attribute.CalculatorType;

/**
 * Buff属性的计算
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-09 19:29
 **/
@Slf4j
@Singleton
public class BuffCalculatorActionImpl extends CalculatorAction {

    @Override public MapObject.MapObjectType mapObjectType() {
        return null;
    }

    @Override public CalculatorType calculatorType() {
        return CalculatorType.BUFF;
    }

    @Override public AttrInfo calculate(MapNpc mapNpc, Object... args) {
        AttrInfo attrInfo = new AttrInfo();
        for (Buff buff : mapNpc.getBuffs()) {

        }
        return attrInfo;
    }

    @Override public AttrInfo calculatePro(MapNpc mapNpc, Object... args) {
        AttrInfo attrInfo = new AttrInfo();
        for (Buff buff : mapNpc.getBuffs()) {

        }
        return attrInfo;
    }

}
