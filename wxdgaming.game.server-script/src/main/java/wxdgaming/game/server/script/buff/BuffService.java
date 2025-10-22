package wxdgaming.game.server.script.buff;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.ann.InitEvent;
import wxdgaming.boot2.core.lang.Tuple2;
import wxdgaming.boot2.core.lang.bit.BitFlagGroup;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.starter.excel.store.DataRepository;
import wxdgaming.game.cfg.QBuffTable;
import wxdgaming.game.cfg.bean.QBuff;
import wxdgaming.game.server.bean.MapNpc;
import wxdgaming.game.server.bean.attribute.CalculatorType;
import wxdgaming.game.server.bean.buff.Buff;
import wxdgaming.game.server.bean.buff.BuffType;
import wxdgaming.game.server.bean.buff.BuffTypeConst;
import wxdgaming.game.server.bean.reason.ReasonConst;
import wxdgaming.game.server.bean.reason.ReasonDTO;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.event.EventConst;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.script.attribute.NpcAttributeService;
import wxdgaming.game.server.script.attribute.PlayerAttributeService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * buff管理
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-08 10:16
 **/
@Slf4j
@Service
public class BuffService extends HoldApplicationContext {

    final DataCenterService dataCenterService;
    final PlayerAttributeService playerAttributeService;
    final NpcAttributeService npcAttributeService;
    Map<BuffType, AbstractBuffAction> actionMap = Collections.emptyMap();


    public BuffService(DataCenterService dataCenterService, PlayerAttributeService playerAttributeService, NpcAttributeService npcAttributeService) {
        this.dataCenterService = dataCenterService;
        this.playerAttributeService = playerAttributeService;
        this.npcAttributeService = npcAttributeService;
    }

    @EventListener
    public void init(InitEvent initEvent) {
        actionMap = applicationContextProvider.toMap(AbstractBuffAction.class, AbstractBuffAction::buffType);
    }

    @EventListener
    public void onHeartMinuteBuffActionTest(EventConst.MapNpcHeartMinuteEvent event) {

        MapNpc mapNpc = event.mapNpc();

        addBuff(mapNpc, mapNpc, 2, 1, ReasonDTO.of(ReasonConst.GM));
        addBuff(mapNpc, mapNpc, 3, 1, ReasonDTO.of(ReasonConst.GM));

    }

    @EventListener
    public void onHeartBuffAction(EventConst.MapNpcHeartEvent event) {
        MapNpc mapNpc = event.mapNpc();
        long millis = MyClock.millis();
        QBuffTable qBuffTable = DataRepository.getIns().dataTable(QBuffTable.class);
        ArrayList<Buff> buffs = mapNpc.getBuffs();
        Iterator<Buff> iterator = buffs.iterator();
        boolean needExecuteAttrCalculator = false;
        while (iterator.hasNext()) {
            Buff buff = iterator.next();
            QBuff qBuff = qBuffTable.getIdLvTable().get(buff.getBuffCfgId(), buff.getLv());
            if (qBuff == null) {
                log.warn("buff不存在 {}, {} {}", mapNpc, buff.getBuffCfgId(), buff.getLv());
                continue;
            }
            if (buff.getLastExecuteTime() + qBuff.getInterval() > millis) {
                continue;
            }
            executeBuff(mapNpc, buff, qBuff, millis);
            if (buff.clearTime(millis) && buff.getTimeList().isEmpty()) {
                log.debug("buff {}, 结束, {}", mapNpc, buff);
                iterator.remove();
                onRemoveBuff(mapNpc, buff);
                if (qBuff.getBuffType() == BuffTypeConst.ChangeAttr) {
                    needExecuteAttrCalculator = true;
                }
            }
        }
        if (needExecuteAttrCalculator) {
            /*删除了属性buff*/
            executeAttrCalculator(mapNpc);
        }
    }

    /** 执行buff，比如掉血 */
    public void executeBuff(MapNpc mapNpc, Buff buff, QBuff qBuff, long mill) {
        BuffType buffType = qBuff.getBuffType();
        AbstractBuffAction abstractBuffAction = actionMap.get(buffType);
        if (abstractBuffAction == null) {
            abstractBuffAction = actionMap.get(BuffTypeConst.None);
        }
        AssertUtil.isNull(abstractBuffAction, "没有对应的buff处理类%s", buffType);
        abstractBuffAction.doAction(mapNpc, buff, qBuff);
        buff.setLastExecuteTime(MyClock.millis());
        buff.setExecuteCount(buff.getExecuteCount() + 1);
    }

    /** 触发属性计算 */
    public void executeAttrCalculator(MapNpc mapNpc) {
        CalculatorType[] calculatorTypes = {CalculatorType.BUFF};
        ReasonDTO reasonDTO = ReasonDTO.of(ReasonConst.Buff);
        if (mapNpc instanceof Player player) {
            EventConst.PlayerAttributeCalculatorEvent event = new EventConst.PlayerAttributeCalculatorEvent(
                    player,
                    calculatorTypes,
                    reasonDTO
            );
            playerAttributeService.onPlayerAttributeCalculator(event);
        } else {
            EventConst.NpcAttributeCalculatorEvent event = new EventConst.NpcAttributeCalculatorEvent(
                    mapNpc,
                    calculatorTypes,
                    reasonDTO
            );
            npcAttributeService.onNpcAttributeCalculator(event);
        }
    }

    /** 处理状态问题 比如眩晕，狂暴，冰冻 */
    public void executeAddStatus(MapNpc mapNpc, QBuff qBuff) {
        ArrayList<Integer> addStatusList = qBuff.getAddStatusList();
        for (Integer status : addStatusList) {
            BitFlagGroup bitFlagGroup = BitFlagGroup.of(status);
            mapNpc.getStatus().addFlags(bitFlagGroup);
        }
    }

    /** 处理状态问题 比如眩晕，狂暴，冰冻 */
    public void executeRemoveStatus(MapNpc mapNpc, QBuff qBuff) {
        ArrayList<Integer> addStatusList = qBuff.getAddStatusList();
        for (Integer status : addStatusList) {
            BitFlagGroup bitFlagGroup = BitFlagGroup.of(status);
            mapNpc.getStatus().removeFlags(bitFlagGroup);
        }
    }

    public void addBuff(MapNpc sender, MapNpc targetMapNpc, int buffCfgId, int lv, ReasonDTO reasonDTO) {
        QBuffTable qBuffTable = DataRepository.getIns().dataTable(QBuffTable.class);
        QBuff qBuff = qBuffTable.getIdLvTable().get(buffCfgId, lv);
        addBuff(sender, targetMapNpc, qBuff, reasonDTO);
    }

    public void addBuff(MapNpc sender, MapNpc targetMapNpc, int buffId, ReasonDTO reasonDTO) {
        QBuff qBuff = DataRepository.getIns().dataTable(QBuffTable.class, buffId);
        addBuff(sender, targetMapNpc, qBuff, reasonDTO);
    }


    public void addBuff(MapNpc sender, MapNpc targetMapNpc, QBuff qbuff, ReasonDTO reasonDTO) {
        ArrayList<Buff> buffs = targetMapNpc.getBuffs();
        Buff oldBuff = null;
        for (Buff buff : buffs) {
            if (buff.getBuffCfgId() == qbuff.getBuffId()) {
                oldBuff = buff;
                break;
            }
        }
        int addType = qbuff.getAddType();
        if (addType == 1 && oldBuff != null) {
            log.debug("添加buff {}, 已有相同的buff {} 忽略 {}", targetMapNpc, oldBuff, reasonDTO);
            return;
        }

        if (addType == 2 && oldBuff != null) {
            Tuple2<Long, Long> tuple2 = new Tuple2<>(MyClock.millis(), MyClock.millis() + qbuff.getDuration());
            oldBuff.getTimeList().add(tuple2);
            log.debug("添加buff {}, 已有相同的buff {} 叠加 {}, {}", targetMapNpc, oldBuff, tuple2, reasonDTO);
            return;
        }

        if (oldBuff != null) {
            buffs.remove(oldBuff);
            onRemoveBuff(targetMapNpc, oldBuff);
            log.debug("添加buff {}, 已有相同的buff {} -> {} 移除 {}", targetMapNpc, qbuff.getId(), oldBuff, reasonDTO);
        }

        if (qbuff.getClearBuffIdList() != null) {
            /*清除指定buff，比如掉血，眩晕buff*/
            for (Integer clearBuffId : qbuff.getClearBuffIdList()) {
                Iterator<Buff> iterator = buffs.iterator();
                while (iterator.hasNext()) {
                    Buff buff = iterator.next();
                    if (buff.getBuffCfgId() == clearBuffId) {
                        iterator.remove();
                        onRemoveBuff(targetMapNpc, buff);
                        log.debug("添加buff {}, 移除已有的buffId {} -> {} 添加 {}", targetMapNpc, qbuff.getId(), buff, reasonDTO);
                    }
                }
            }
        }

        if (qbuff.getClearGroupList() != null) {
            /*删除分组的buff，比如删除减益buff*/
            for (Integer clearGroup : qbuff.getClearGroupList()) {
                Iterator<Buff> iterator = buffs.iterator();
                while (iterator.hasNext()) {
                    Buff buff = iterator.next();
                    if (buff.qBuff().getBuffGroup() == clearGroup) {
                        iterator.remove();
                        onRemoveBuff(targetMapNpc, buff);
                        log.debug("添加buff {}, 移除已有的buffGroup {} -> {} 添加 {}", targetMapNpc, qbuff.getId(), buff, reasonDTO);
                    }
                }
            }
        }

        Buff newBuff = new Buff();
        newBuff.setUid(dataCenterService.getBuffHexid().newId());
        newBuff.setSendUid(sender.getUid());
        newBuff.setSender(sender);
        newBuff.setBuffCfgId(qbuff.getBuffId());
        newBuff.setLv(qbuff.getLv());
        Tuple2<Long, Long> tuple2 = new Tuple2<>(MyClock.millis(), MyClock.millis() + qbuff.getDuration());
        newBuff.getTimeList().add(tuple2);
        newBuff.setLastExecuteTime(tuple2.getLeft());
        newBuff.setExecuteCount(0);

        if (qbuff.isAddExecutor()) {
            /*获得buff理解执行*/
            executeBuff(targetMapNpc, newBuff, qbuff, newBuff.getLastExecuteTime());
        }

        if (qbuff.getDuration() < 100) {
            log.debug("添加buff {}, {}, buff持续时间过小，视为一次性buff ,{}", targetMapNpc, newBuff, reasonDTO);
            return;
        }

        buffs.add(newBuff);
        onAddBuff(targetMapNpc, newBuff);
        if (qbuff.getBuffType() == BuffTypeConst.ChangeAttr) {
            executeAttrCalculator(targetMapNpc);
        }
        log.debug("添加buff {}, {}, {}", targetMapNpc, newBuff, reasonDTO);
    }

    public void onAddBuff(MapNpc mapNpc, Buff buff) {
        executeAddStatus(mapNpc, buff.qBuff());
    }

    public void onRemoveBuff(MapNpc mapNpc, Buff buff) {
        executeRemoveStatus(mapNpc, buff.qBuff());
    }

}
