package wxdgaming.game.server.script.attribute;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.game.basic.core.ReasonDTO;
import wxdgaming.game.bean.attr.AttrInfo;
import wxdgaming.game.bean.attr.AttrType;
import wxdgaming.game.server.bean.MapNpc;
import wxdgaming.game.server.bean.MapObject;

import java.util.HashMap;
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
    TreeMap<Integer, AbstractCalculatorAction> calculatorImplMap = new TreeMap<>();

    @Init
    public void init() {

        TreeMap<Integer, AbstractCalculatorAction> tmp = new TreeMap<>();
        applicationContextProvider.classWithSuper(AbstractCalculatorAction.class)
                .forEach(calculatorAction -> {
                    MapObject.MapObjectType mapObjectType = calculatorAction.mapObjectType();
                    if (mapObjectType == MapObject.MapObjectType.Player) {
                        return;
                    }
                    AbstractCalculatorAction old = tmp.put(calculatorAction.calculatorType().getCode(), calculatorAction);
                    AssertUtil.assertTrue(old == null, "重复的属性计算器类型 " + calculatorAction.calculatorType().getCode() + " " + old + ", " + calculatorAction);
                });
        calculatorImplMap = tmp;
    }


    public void calculatorAll(MapNpc mapNpc) {
        HashMap<Integer, AttrInfo> attrMap = new HashMap<>();
        HashMap<Integer, AttrInfo> attrProMap = new HashMap<>();
        for (AbstractCalculatorAction calculatorAction : calculatorImplMap.values()) {
            AttrInfo calculate = calculatorAction.calculate(mapNpc);
            attrMap.put(calculatorAction.calculatorType().getCode(), calculate);
            AttrInfo calculatePro = calculatorAction.calculatePro(mapNpc);
            attrProMap.put(calculatorAction.calculatorType().getCode(), calculatePro);
        }

        mapNpc.setAttrMap(attrMap);
        mapNpc.setAttrProMap(attrProMap);

        finalCalculator(mapNpc);
    }

    public void onNpcAttributeCalculator(MapNpc mapNpc, CalculatorType[] calculatorTypes, ReasonDTO msg) {
        for (CalculatorType calculatorType : calculatorTypes) {
            calculator(mapNpc, calculatorType, msg);
        }
        finalCalculator(mapNpc);
    }

    public void calculator(MapNpc mapNpc, CalculatorType calculatorType, ReasonDTO msg) {

        AbstractCalculatorAction calculatorAction = calculatorImplMap.get(calculatorType.getCode());

        AttrInfo calculate = calculatorAction.calculate(mapNpc);
        mapNpc.getAttrMap().put(calculatorAction.calculatorType().getCode(), calculate);
        AttrInfo calculatePro = calculatorAction.calculatePro(mapNpc);
        mapNpc.getAttrProMap().put(calculatorAction.calculatorType().getCode(), calculatePro);

    }

    public void finalCalculator(MapNpc mapNpc) {

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
