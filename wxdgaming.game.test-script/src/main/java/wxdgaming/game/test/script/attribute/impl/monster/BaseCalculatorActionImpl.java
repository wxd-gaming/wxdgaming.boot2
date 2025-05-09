package wxdgaming.game.test.script.attribute.impl.monster;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.test.bean.MapNpc;
import wxdgaming.game.test.bean.MapObject;
import wxdgaming.game.test.bean.attr.AttrInfo;
import wxdgaming.game.test.cfg.QPlayerTable;
import wxdgaming.game.test.cfg.bean.QPlayer;
import wxdgaming.game.test.script.attribute.CalculatorAction;
import wxdgaming.game.test.script.attribute.CalculatorType;

/**
 * 基础属性
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-09 19:29
 **/
@Slf4j
@Singleton
public class BaseCalculatorActionImpl extends CalculatorAction {

    @Override public MapObject.MapObjectType mapObjectType() {
        return MapObject.MapObjectType.Npc;
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
