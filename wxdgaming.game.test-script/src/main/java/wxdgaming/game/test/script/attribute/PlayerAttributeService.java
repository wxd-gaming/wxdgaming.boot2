package wxdgaming.game.test.script.attribute;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.game.test.bean.MapObject;
import wxdgaming.game.test.bean.attr.AttrInfo;
import wxdgaming.game.test.bean.attr.AttrType;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.event.OnLevelUp;
import wxdgaming.game.test.event.OnLoginBefore;
import wxdgaming.game.test.event.OnPlayerAttributeCalculator;
import wxdgaming.game.test.script.role.message.ResUpdateFightValue;

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
public class PlayerAttributeService extends HoldRunApplication {

    /** 属性计算器 */
    TreeMap<Integer, CalculatorAction> calculatorImplMap = new TreeMap<>();

    @Init
    public void init() {

        TreeMap<Integer, CalculatorAction> tmp = new TreeMap<>();
        runApplication.classWithSuper(CalculatorAction.class)
                .forEach(calculatorAction -> {
                    MapObject.MapObjectType mapObjectType = calculatorAction.mapObjectType();
                    if (mapObjectType != MapObject.MapObjectType.Player) {
                        return;
                    }
                    CalculatorAction old = tmp.put(calculatorAction.calculatorType().getCode(), calculatorAction);
                    AssertUtil.assertTrue(old == null, "重复的属性计算器类型 " + calculatorAction.calculatorType().getCode() + " " + old + ", " + calculatorAction);
                });
        calculatorImplMap = tmp;
    }

    @OnLoginBefore
    public void onLoginBefore(Player player) {
        calculatorAll(player);
    }

    public void calculatorAll(Player player) {
        HashMap<Integer, AttrInfo> attrMap = new HashMap<>();
        HashMap<Integer, AttrInfo> attrProMap = new HashMap<>();
        for (CalculatorAction calculatorAction : calculatorImplMap.values()) {
            AttrInfo calculate = calculatorAction.calculate(player);
            attrMap.put(calculatorAction.calculatorType().getCode(), calculate);
            AttrInfo calculatePro = calculatorAction.calculatePro(player);
            attrProMap.put(calculatorAction.calculatorType().getCode(), calculatePro);
        }

        player.setAttrMap(attrMap);
        player.setAttrProMap(attrProMap);

        finalCalculator(player, true, "上线");
    }

    public void calculator(Player player, CalculatorType calculatorType) {

        CalculatorAction calculatorAction = calculatorImplMap.get(calculatorType.getCode());

        AttrInfo calculate = calculatorAction.calculate(player);
        player.getAttrMap().put(calculatorAction.calculatorType().getCode(), calculate);
        AttrInfo calculatePro = calculatorAction.calculatePro(player);
        player.getAttrProMap().put(calculatorAction.calculatorType().getCode(), calculatePro);

    }

    public void finalCalculator(Player player, boolean isLogin, String msg) {

        /*累计基础属性*/
        AttrInfo finalAttrInfo = new AttrInfo();
        for (AttrInfo attrInfo : player.getAttrMap().values()) {
            finalAttrInfo.append(attrInfo);
        }

        /*累计百分比属性*/
        AttrInfo finalAttrInfoPro = new AttrInfo();
        for (AttrInfo attrInfo : player.getAttrProMap().values()) {
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
        long oldFightValue = player.getFightValue();
        AttrInfo oldFinalAttrInfo = player.getFinalAttrInfo();

        long fightValue = 0;
        for (Map.Entry<AttrType, Long> entry : finalAttrInfo.entrySet()) {
            AttrType attrType = entry.getKey();
            Long value = entry.getValue();
            fightValue += attrType.getCode() * value * 100;
        }

        player.setFightValue(fightValue);
        player.setFinalAttrInfo(finalAttrInfo);

        if (oldFightValue != fightValue) {
            log.info("{} 战斗力变化 {} -> {}, 触发: {}", player, oldFightValue, fightValue, msg);
            if (!isLogin) {
                ResUpdateFightValue resUpdateFightValue = new ResUpdateFightValue();
                resUpdateFightValue.setFightValue(fightValue);
                player.write(resUpdateFightValue);
            }
        }

        if (!oldFinalAttrInfo.get(AttrType.MAXHP).equals(finalAttrInfo.get(AttrType.MAXHP))) {
            long maxHp = finalAttrInfo.get(AttrType.MAXHP);
            if (player.getHp() > maxHp) {
                player.setHp(maxHp);
                log.info("{} 生命值超出上限，自动修正为 {}, 触发: {}", player, maxHp, msg);
            }
            player.sendHp();
        }

        if (!oldFinalAttrInfo.get(AttrType.MAXMP).equals(finalAttrInfo.get(AttrType.MAXMP))) {
            long maxMp = finalAttrInfo.get(AttrType.MAXMP);
            if (player.getMp() > maxMp) {
                player.setMp(maxMp);
                log.info("{} 魔法值超出上限，自动修正为 {}, 触发: {}", player, maxMp, msg);
            }
            player.sendMp();
        }

    }

    @OnLevelUp
    public void onLevel(Player player) {
        calculator(player, CalculatorType.BASE);
        finalCalculator(player, false, "等级提升");
    }

    @OnPlayerAttributeCalculator
    public void onPlayerAttributeCalculator(Player player, CalculatorType[] calculatorTypes, String msg) {
        for (CalculatorType calculatorType : calculatorTypes) {
            calculator(player, calculatorType);
        }
        finalCalculator(player, false, msg);
    }

}
