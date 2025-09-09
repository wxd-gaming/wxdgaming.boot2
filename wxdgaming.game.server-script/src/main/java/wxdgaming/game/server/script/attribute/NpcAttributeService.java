package wxdgaming.game.server.script.attribute;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.game.server.bean.reason.ReasonConst;
import wxdgaming.game.server.bean.reason.ReasonDTO;
import wxdgaming.game.server.bean.attr.AttrInfo;
import wxdgaming.game.server.bean.attr.AttrType;
import wxdgaming.game.server.bean.MapNpc;
import wxdgaming.game.server.bean.MapObject;
import wxdgaming.game.server.bean.attribute.CalculatorType;
import wxdgaming.game.server.event.EventConst;

import java.util.Map;
import java.util.TreeMap;

/**
 * 属性计算器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-09 19:14
 **/
@Slf4j
@Service
public class NpcAttributeService extends HoldApplicationContext {

    /** 属性计算器 */
    TreeMap<CalculatorType, AbstractCalculatorAction> calculatorImplMap = new TreeMap<>();
    CalculatorType[] calculatorTypes;

    @Init
    public void init() {
        TreeMap<CalculatorType, AbstractCalculatorAction> tmp = new TreeMap<>();
        applicationContextProvider.classWithSuperStream(AbstractCalculatorAction.class)
                .forEach(calculatorAction -> {
                    MapObject.MapObjectType mapObjectType = calculatorAction.mapObjectType();
                    if (mapObjectType == MapObject.MapObjectType.Player) {
                        return;
                    }
                    AbstractCalculatorAction old = tmp.put(calculatorAction.calculatorType(), calculatorAction);
                    AssertUtil.assertTrue(old == null, "重复的属性计算器类型 " + calculatorAction.calculatorType().getCode() + " " + old + ", " + calculatorAction);
                });
        calculatorImplMap = tmp;
        calculatorTypes = calculatorImplMap.keySet().toArray(new CalculatorType[calculatorImplMap.size()]);
    }


    public void calculatorAll(MapNpc mapNpc) {
        EventConst.NpcAttributeCalculatorEvent playerAttributeCalculatorEvent = new EventConst.NpcAttributeCalculatorEvent(
                mapNpc,
                calculatorTypes,
                ReasonDTO.of(ReasonConst.Level)
        );
        finalCalculator(playerAttributeCalculatorEvent);
    }

    public void onNpcAttributeCalculator(EventConst.NpcAttributeCalculatorEvent event) {
        for (CalculatorType calculatorType : event.calculatorTypes()) {
            calculator(calculatorType, event);
        }
        finalCalculator(event);
    }

    public void calculator(CalculatorType calculatorType, EventConst.NpcAttributeCalculatorEvent event) {
        MapNpc mapNpc = event.npc();
        AbstractCalculatorAction calculatorAction = calculatorImplMap.get(calculatorType);

        AttrInfo calculate = calculatorAction.calculate(mapNpc, event);
        mapNpc.getAttrMap().put(calculatorAction.calculatorType().getCode(), calculate);
        AttrInfo calculatePro = calculatorAction.calculatePro(mapNpc, event);
        mapNpc.getAttrProMap().put(calculatorAction.calculatorType().getCode(), calculatePro);

    }

    public void finalCalculator(EventConst.NpcAttributeCalculatorEvent event) {
        MapNpc mapNpc = event.npc();
        /*累计基础属性*/
        AttrInfo finalAttrInfo = new AttrInfo();
        for (AttrInfo attrInfo : mapNpc.getAttrMap().values()) {
            finalAttrInfo.append(attrInfo);
        }

        /*累计百分比属性*/
        AttrInfo finalAttrInfoPro = new AttrInfo();
        for (AttrInfo attrInfo : mapNpc.getAttrProMap().values()) {
            finalAttrInfoPro.append(attrInfo);
        }
        for (Map.Entry<AttrType, Long> entry : finalAttrInfoPro.entrySet()) {
            AttrType attrType = entry.getKey();
            Long value = entry.getValue();
            Long baseValue = finalAttrInfo.get(attrType);
            baseValue = baseValue + (baseValue * (value) / 10000);
            /*按照百分比提成属性*/
            finalAttrInfo.put(attrType, baseValue);
        }

        /*历史战斗力*/
        long oldFightValue = mapNpc.getFightValue();
        AttrInfo oldFinalAttrInfo = mapNpc.getFinalAttrInfo();

        long fightValue = 0;
        for (Map.Entry<AttrType, Long> entry : finalAttrInfo.entrySet()) {
            AttrType attrType = entry.getKey();
            Long value = entry.getValue();
            fightValue += attrType.getCode() * value * 100;
        }

        mapNpc.setFightValue(fightValue);
        mapNpc.setFinalAttrInfo(finalAttrInfo);

    }
}
