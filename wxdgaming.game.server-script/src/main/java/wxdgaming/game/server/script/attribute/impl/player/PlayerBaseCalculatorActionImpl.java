package wxdgaming.game.server.script.attribute.impl.player;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.bean.attr.AttrInfo;
import wxdgaming.game.cfg.QPlayerTable;
import wxdgaming.game.cfg.bean.QPlayer;
import wxdgaming.game.server.bean.MapNpc;
import wxdgaming.game.server.bean.MapObject;
import wxdgaming.game.server.script.attribute.AbstractCalculatorAction;
import wxdgaming.game.server.script.attribute.CalculatorType;

/**
 * 基础属性
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-09 19:29
 **/
@Slf4j
@Component
public class PlayerBaseCalculatorActionImpl extends AbstractCalculatorAction {

    @Override public MapObject.MapObjectType mapObjectType() {
        return MapObject.MapObjectType.Player;
    }

    @Override public CalculatorType calculatorType() {
        return CalculatorType.BASE;
    }

    @Override public AttrInfo calculate(MapNpc mapNpc, Object... args) {
        QPlayer qPlayer = dataRepository.dataTable(QPlayerTable.class, mapNpc.getLevel());
        AttrInfo attr = qPlayer.getAttr();
        return new AttrInfo(attr);
    }

    @Override public AttrInfo calculatePro(MapNpc mapNpc, Object... args) {
        QPlayer qPlayer = dataRepository.dataTable(QPlayerTable.class, mapNpc.getLevel());

        AttrInfo attrPro = qPlayer.getAttrPro();

        return new AttrInfo(attrPro);
    }
}
