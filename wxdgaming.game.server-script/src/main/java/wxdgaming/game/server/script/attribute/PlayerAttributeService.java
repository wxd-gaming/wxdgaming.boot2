package wxdgaming.game.server.script.attribute;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.ann.InitEvent;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.game.server.bean.reason.ReasonConst;
import wxdgaming.game.server.bean.reason.ReasonDTO;
import wxdgaming.game.server.bean.attr.AttrInfo;
import wxdgaming.game.server.bean.attr.AttrType;
import wxdgaming.game.message.role.ResUpdateFightValue;
import wxdgaming.game.server.bean.MapObject;
import wxdgaming.game.server.bean.attribute.CalculatorType;
import wxdgaming.game.server.bean.role.Player;
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
public class PlayerAttributeService extends HoldApplicationContext {

    /** 属性计算器 */
    TreeMap<CalculatorType, AbstractCalculatorAction> calculatorImplMap = new TreeMap<>();
    CalculatorType[] calculatorTypes;

    public void init(InitEvent initEvent) {

        TreeMap<CalculatorType, AbstractCalculatorAction> tmp = new TreeMap<>();
        applicationContextProvider.classWithSuperStream(AbstractCalculatorAction.class)
                .forEach(calculatorAction -> {
                    MapObject.MapObjectType mapObjectType = calculatorAction.mapObjectType();
                    if (mapObjectType != null && mapObjectType != MapObject.MapObjectType.Player) {
                        return;
                    }
                    AbstractCalculatorAction old = tmp.put(calculatorAction.calculatorType(), calculatorAction);
                    AssertUtil.isTrue(old == null, "重复的属性计算器类型 " + calculatorAction.calculatorType() + " " + old + ", " + calculatorAction);
                });
        calculatorImplMap = tmp;
        calculatorTypes = calculatorImplMap.keySet().toArray(new CalculatorType[calculatorImplMap.size()]);
    }

    public void calculator(CalculatorType calculatorType, EventConst.PlayerAttributeCalculatorEvent event) {
        Player player = event.player();
        AbstractCalculatorAction calculatorAction = calculatorImplMap.get(calculatorType);

        AttrInfo calculate = calculatorAction.calculate(player, event);
        player.getAttrMap().put(calculatorAction.calculatorType().getCode(), calculate);
        AttrInfo calculatePro = calculatorAction.calculatePro(player, event);
        player.getAttrProMap().put(calculatorAction.calculatorType().getCode(), calculatePro);

    }

    public void finalCalculator(EventConst.PlayerAttributeCalculatorEvent event) {

        /*累计基础属性*/
        AttrInfo finalAttrInfo = new AttrInfo();
        for (AttrInfo attrInfo : event.player().getAttrMap().values()) {
            finalAttrInfo.append(attrInfo);
        }

        /*累计百分比属性*/
        AttrInfo finalAttrInfoPro = new AttrInfo();
        for (AttrInfo attrInfo : event.player().getAttrProMap().values()) {
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
        long oldFightValue = event.player().getFightValue();
        AttrInfo oldFinalAttrInfo = event.player().getFinalAttrInfo();

        long fightValue = 0;
        for (Map.Entry<AttrType, Long> entry : finalAttrInfo.entrySet()) {
            AttrType attrType = entry.getKey();
            Long value = entry.getValue();
            fightValue += attrType.getCode() * value * 100;
        }

        event.player().setFightValue(fightValue);
        event.player().setFinalAttrInfo(finalAttrInfo);

        if (oldFightValue != fightValue) {
            log.info("{} 战斗力变化 {} -> {}, 触发: {}", event.player(), oldFightValue, fightValue, event.reasonDTO());
            if (event.reasonDTO().getReasonConst() != ReasonConst.Login) {
                ResUpdateFightValue resUpdateFightValue = new ResUpdateFightValue();
                resUpdateFightValue.setFightValue(fightValue);
                event.player().write(resUpdateFightValue);
            }
        }

        if (!oldFinalAttrInfo.get(AttrType.MAXHP).equals(finalAttrInfo.get(AttrType.MAXHP))) {
            long maxHp = finalAttrInfo.get(AttrType.MAXHP);
            if (event.player().getHp() > maxHp) {
                event.player().setHp(maxHp);
                log.info("{} 生命值超出上限，自动修正为 {}, 触发: {}", event.player(), maxHp, event.reasonDTO());
            }
            event.player().sendHp();
        }

        if (!oldFinalAttrInfo.get(AttrType.MAXMP).equals(finalAttrInfo.get(AttrType.MAXMP))) {
            long maxMp = finalAttrInfo.get(AttrType.MAXMP);
            if (event.player().getMp() > maxMp) {
                event.player().setMp(maxMp);
                log.info("{} 魔法值超出上限，自动修正为 {}, 触发: {}", event.player(), maxMp, event.reasonDTO());
            }
            event.player().sendMp();
        }

    }

    @Order(Integer.MAX_VALUE)
    public void onLoginBefore(EventConst.LoginBeforePlayerEvent event) {
        Player player = event.player();
        EventConst.PlayerAttributeCalculatorEvent playerAttributeCalculatorEvent = new EventConst.PlayerAttributeCalculatorEvent(
                player,
                calculatorTypes,
                ReasonDTO.of(ReasonConst.Login)
        );

        onPlayerAttributeCalculator(playerAttributeCalculatorEvent);
    }

    final CalculatorType[] calculatorBASE = {CalculatorType.BASE};

    /** 提升等级后触发属性计算 */
    @Order(Integer.MAX_VALUE)
    public void onLevelUp(EventConst.LevelUpEvent event) {
        EventConst.PlayerAttributeCalculatorEvent playerAttributeCalculatorEvent = new EventConst.PlayerAttributeCalculatorEvent(
                event.player(),
                calculatorBASE,
                ReasonDTO.of(ReasonConst.Level)
        );
        onPlayerAttributeCalculator(playerAttributeCalculatorEvent);
    }

    /** 触发属性计算事件监听 */
    @Order(Integer.MAX_VALUE)
    public void onPlayerAttributeCalculator(EventConst.PlayerAttributeCalculatorEvent event) {
        for (CalculatorType calculatorType : event.calculatorTypes()) {
            calculator(calculatorType, event);
        }
        finalCalculator(event);
    }

}
