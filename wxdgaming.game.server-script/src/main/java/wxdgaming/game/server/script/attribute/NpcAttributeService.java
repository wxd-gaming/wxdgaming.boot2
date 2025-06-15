package wxdgaming.game.server.script.attribute;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.game.server.bean.MapMonster;
import wxdgaming.game.server.bean.MapNpc;
import wxdgaming.game.server.bean.MapObject;
import wxdgaming.game.bean.attr.AttrInfo;
import wxdgaming.game.bean.attr.AttrType;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 属性计算器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-09 19:14
 **/
@Slf4j
@Singleton
public class NpcAttributeService extends HoldRunApplication {

    /** 属性计算器 */
    TreeMap<Integer, AbstractCalculatorAction> calculatorImplMap = new TreeMap<>();

    @Init
    public void init() {

        TreeMap<Integer, AbstractCalculatorAction> tmp = new TreeMap<>();
        runApplication.classWithSuper(AbstractCalculatorAction.class)
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


    public void calculatorAll(MapMonster mapMonster) {
        HashMap<Integer, AttrInfo> attrMap = new HashMap<>();
        HashMap<Integer, AttrInfo> attrProMap = new HashMap<>();
        for (AbstractCalculatorAction calculatorAction : calculatorImplMap.values()) {
            AttrInfo calculate = calculatorAction.calculate(mapMonster);
            attrMap.put(calculatorAction.calculatorType().getCode(), calculate);
            AttrInfo calculatePro = calculatorAction.calculatePro(mapMonster);
            attrProMap.put(calculatorAction.calculatorType().getCode(), calculatePro);
        }

        mapMonster.setAttrMap(attrMap);
        mapMonster.setAttrProMap(attrProMap);

        finalCalculator(mapMonster);
    }

    public void calculator(MapNpc mapNpc, CalculatorType calculatorType) {

        AbstractCalculatorAction calculatorAction = calculatorImplMap.get(calculatorType.getCode());

        AttrInfo calculate = calculatorAction.calculate(mapNpc);
        mapNpc.getAttrMap().put(calculatorAction.calculatorType().getCode(), calculate);
        AttrInfo calculatePro = calculatorAction.calculatePro(mapNpc);
        mapNpc.getAttrProMap().put(calculatorAction.calculatorType().getCode(), calculatePro);

    }

    public void finalCalculator(MapMonster mapMonster) {

        /*累计基础属性*/
        AttrInfo finalAttrInfo = new AttrInfo();
        for (AttrInfo attrInfo : mapMonster.getAttrMap().values()) {
            finalAttrInfo.append(attrInfo);
        }

        /*累计百分比属性*/
        AttrInfo finalAttrInfoPro = new AttrInfo();
        for (AttrInfo attrInfo : mapMonster.getAttrProMap().values()) {
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
        long oldFightValue = mapMonster.getFightValue();
        AttrInfo oldFinalAttrInfo = mapMonster.getFinalAttrInfo();

        long fightValue = 0;
        for (Map.Entry<AttrType, Long> entry : finalAttrInfo.entrySet()) {
            AttrType attrType = entry.getKey();
            Long value = entry.getValue();
            fightValue += attrType.getCode() * value * 100;
        }

        mapMonster.setFightValue(fightValue);
        mapMonster.setFinalAttrInfo(finalAttrInfo);

    }
}
