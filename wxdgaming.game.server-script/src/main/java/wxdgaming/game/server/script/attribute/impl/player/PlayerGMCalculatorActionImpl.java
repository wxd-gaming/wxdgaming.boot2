package wxdgaming.game.server.script.attribute.impl.player;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.bean.attr.AttrInfo;
import wxdgaming.game.server.bean.MapNpc;
import wxdgaming.game.server.bean.MapObject;
import wxdgaming.game.server.script.attribute.AbstractCalculatorAction;
import wxdgaming.game.server.script.attribute.CalculatorType;

/**
 * 一些特殊属性的计算
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-09 19:29
 **/
@Slf4j
@Component
public class PlayerGMCalculatorActionImpl extends AbstractCalculatorAction {

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
