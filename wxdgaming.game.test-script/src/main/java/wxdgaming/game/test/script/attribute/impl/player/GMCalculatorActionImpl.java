package wxdgaming.game.test.script.attribute.impl.player;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.test.bean.MapNpc;
import wxdgaming.game.test.bean.MapObject;
import wxdgaming.game.test.bean.attr.AttrInfo;
import wxdgaming.game.test.script.attribute.CalculatorAction;
import wxdgaming.game.test.script.attribute.CalculatorType;

/**
 * 一些特殊属性的计算
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-09 19:29
 **/
@Slf4j
@Singleton
public class GMCalculatorActionImpl extends CalculatorAction {

    @Override public MapObject.MapObjectType mapObjectType() {
        return MapObject.MapObjectType.Player;
    }

    @Override public CalculatorType calculatorType() {
        return CalculatorType.GM;
    }

    @Override public AttrInfo calculate(MapNpc mapNpc, Object... args) {
        AttrInfo attrInfo = new AttrInfo(mapNpc.getGmAttrInfo());
        attrInfo.append(mapNpc.getTmpAttrInfo());
        return attrInfo;
    }

    @Override public AttrInfo calculatePro(MapNpc mapNpc, Object... args) {
        AttrInfo attrInfo = new AttrInfo(mapNpc.getGmAttrProInfo());
        attrInfo.append(mapNpc.getTmpAttrProInfo());
        return attrInfo;
    }

}
