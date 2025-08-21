package wxdgaming.game.server.script.attribute;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.game.basic.core.Reason;
import wxdgaming.game.basic.core.ReasonDTO;
import wxdgaming.game.bean.attr.AttrInfo;
import wxdgaming.game.bean.attr.AttrType;
import wxdgaming.game.message.role.ResUpdateFightValue;
import wxdgaming.game.server.bean.MapObject;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.event.OnLevelUp;
import wxdgaming.game.server.event.OnLoginBefore;
import wxdgaming.game.server.event.OnPlayerAttributeCalculator;

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
public class PlayerAttributeService extends HoldApplicationContext {

    /** 属性计算器 */
    TreeMap<Integer, AbstractCalculatorAction> calculatorImplMap = new TreeMap<>();

    @Init
    public void init() {

        TreeMap<Integer, AbstractCalculatorAction> tmp = new TreeMap<>();
        applicationContextProvider.classWithSuper(AbstractCalculatorAction.class)
                .forEach(calculatorAction -> {
                    MapObject.MapObjectType mapObjectType = calculatorAction.mapObjectType();
                    if (mapObjectType != null && mapObjectType != MapObject.MapObjectType.Player) {
                        return;
                    }
                    AbstractCalculatorAction old = tmp.put(calculatorAction.calculatorType().getCode(), calculatorAction);
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
        for (AbstractCalculatorAction calculatorAction : calculatorImplMap.values()) {
            AttrInfo calculate = calculatorAction.calculate(player);
            attrMap.put(calculatorAction.calculatorType().getCode(), calculate);
            AttrInfo calculatePro = calculatorAction.calculatePro(player);
            attrProMap.put(calculatorAction.calculatorType().getCode(), calculatePro);
        }

        player.setAttrMap(attrMap);
        player.setAttrProMap(attrProMap);

        finalCalculator(player, true, ReasonDTO.of(Reason.Level));
    }

    public void calculator(Player player, CalculatorType calculatorType) {

        AbstractCalculatorAction calculatorAction = calculatorImplMap.get(calculatorType.getCode());

        AttrInfo calculate = calculatorAction.calculate(player);
        player.getAttrMap().put(calculatorAction.calculatorType().getCode(), calculate);
        AttrInfo calculatePro = calculatorAction.calculatePro(player);
        player.getAttrProMap().put(calculatorAction.calculatorType().getCode(), calculatePro);

    }

    public void finalCalculator(Player player, boolean isLogin, ReasonDTO msg) {

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
        finalCalculator(player, false, ReasonDTO.of(Reason.Level));
    }

    @OnPlayerAttributeCalculator
    public void onPlayerAttributeCalculator(Player player, CalculatorType[] calculatorTypes, ReasonDTO msg) {
        for (CalculatorType calculatorType : calculatorTypes) {
            calculator(player, calculatorType);
        }
        finalCalculator(player, false, msg);
    }

}
