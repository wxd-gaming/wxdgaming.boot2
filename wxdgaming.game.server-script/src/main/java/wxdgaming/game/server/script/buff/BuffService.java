package wxdgaming.game.server.script.buff;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.lang.Tuple2;
import wxdgaming.boot2.core.lang.bit.BitFlagGroup;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.starter.excel.store.DataRepository;
import wxdgaming.game.bean.buff.BuffType;
import wxdgaming.game.bean.buff.BuffTypeConst;
import wxdgaming.game.cfg.QBuffTable;
import wxdgaming.game.cfg.bean.QBuff;
import wxdgaming.game.core.Reason;
import wxdgaming.game.core.ReasonArgs;
import wxdgaming.game.server.bean.MapNpc;
import wxdgaming.game.server.bean.buff.Buff;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.event.OnHeart;
import wxdgaming.game.server.event.OnHeartMinute;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.script.attribute.CalculatorType;
import wxdgaming.game.server.script.attribute.NpcAttributeService;
import wxdgaming.game.server.script.attribute.PlayerAttributeService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * buff管理
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-08 10:16
 **/
@Slf4j
@Singleton
public class BuffService extends HoldRunApplication {

    final DataCenterService dataCenterService;
    final PlayerAttributeService playerAttributeService;
    final NpcAttributeService npcAttributeService;
    HashMap<BuffType, AbstractBuffAction> actionMap = new HashMap<>();


    @Inject
    public BuffService(DataCenterService dataCenterService, PlayerAttributeService playerAttributeService, NpcAttributeService npcAttributeService) {
        this.dataCenterService = dataCenterService;
        this.playerAttributeService = playerAttributeService;
        this.npcAttributeService = npcAttributeService;
    }

    @Init
    public void init() {
        HashMap<BuffType, AbstractBuffAction> map = new HashMap<>();
        Stream<AbstractBuffAction> abstractBuffActionStream = runApplication.classWithSuper(AbstractBuffAction.class);
        abstractBuffActionStream.forEach(impl -> {
            AbstractBuffAction old = map.put(impl.buffType(), impl);
            AssertUtil.assertTrue(old == null, "重复的buff类型" + impl.buffType());
        });
        actionMap = map;
    }

    @OnHeartMinute
    public void onHeartMinuteBuffActionTest(MapNpc mapNpc, long mill) {

        addBuff(mapNpc, mapNpc, 2, 1, ReasonArgs.of(Reason.GM));
        addBuff(mapNpc, mapNpc, 3, 1, ReasonArgs.of(Reason.GM));
    }

    @OnHeart
    public void onHeartBuffAction(MapNpc mapNpc, long mill) {
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
            if (buff.getLastExecuteTime() + qBuff.getInterval() > mill) {
                continue;
            }
            executeBuff(mapNpc, buff, qBuff, mill);
            if (buff.clearTime(mill) && buff.getTimeList().isEmpty()) {
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
        AssertUtil.assertNull(abstractBuffAction, "没有对应的buff处理类%s", buffType);
        abstractBuffAction.doAction(mapNpc, buff, qBuff);
        buff.setLastExecuteTime(MyClock.millis());
        buff.setExecuteCount(buff.getExecuteCount() + 1);
    }

    /** 触发属性计算 */
    public void executeAttrCalculator(MapNpc mapNpc) {
        CalculatorType[] calculatorTypes = {CalculatorType.BUFF};
        ReasonArgs reasonArgs = ReasonArgs.of(Reason.Buff);
        if (mapNpc instanceof Player player) {
            playerAttributeService.onPlayerAttributeCalculator(player, calculatorTypes, reasonArgs);
        } else {
            npcAttributeService.onNpcAttributeCalculator(mapNpc, calculatorTypes, reasonArgs);
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

    public void addBuff(MapNpc sender, MapNpc targetMapNpc, int buffCfgId, int lv, ReasonArgs reasonArgs) {
        QBuffTable qBuffTable = DataRepository.getIns().dataTable(QBuffTable.class);
        QBuff qBuff = qBuffTable.getIdLvTable().get(buffCfgId, lv);
        addBuff(sender, targetMapNpc, qBuff, reasonArgs);
    }

    public void addBuff(MapNpc sender, MapNpc targetMapNpc, int buffId, ReasonArgs reasonArgs) {
        QBuff qBuff = DataRepository.getIns().dataTable(QBuffTable.class, buffId);
        addBuff(sender, targetMapNpc, qBuff, reasonArgs);
    }


    public void addBuff(MapNpc sender, MapNpc targetMapNpc, QBuff qbuff, ReasonArgs reasonArgs) {
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
            log.debug("添加buff {}, 已有相同的buff {} 忽略 {}", targetMapNpc, oldBuff, reasonArgs);
            return;
        }

        if (addType == 2 && oldBuff != null) {
            Tuple2<Long, Long> tuple2 = new Tuple2<>(MyClock.millis(), MyClock.millis() + qbuff.getDuration());
            oldBuff.getTimeList().add(tuple2);
            log.debug("添加buff {}, 已有相同的buff {} 叠加 {}, {}", targetMapNpc, oldBuff, tuple2, reasonArgs);
            return;
        }

        if (oldBuff != null) {
            buffs.remove(oldBuff);
            onRemoveBuff(targetMapNpc, oldBuff);
            log.debug("添加buff {}, 已有相同的buff {} -> {} 移除 {}", targetMapNpc, qbuff.getId(), oldBuff, reasonArgs);
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
                        log.debug("添加buff {}, 移除已有的buffId {} -> {} 添加 {}", targetMapNpc, qbuff.getId(), buff, reasonArgs);
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
                        log.debug("添加buff {}, 移除已有的buffGroup {} -> {} 添加 {}", targetMapNpc, qbuff.getId(), buff, reasonArgs);
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
            log.debug("添加buff {}, {}, buff持续时间过小，视为一次性buff ,{}", targetMapNpc, newBuff, reasonArgs);
            return;
        }

        buffs.add(newBuff);
        onAddBuff(targetMapNpc, newBuff);
        if (qbuff.getBuffType() == BuffTypeConst.ChangeAttr) {
            executeAttrCalculator(targetMapNpc);
        }
        log.debug("添加buff {}, {}, {}", targetMapNpc, newBuff, reasonArgs);
    }

    public void onAddBuff(MapNpc mapNpc, Buff buff) {
        executeAddStatus(mapNpc, buff.qBuff());
    }

    public void onRemoveBuff(MapNpc mapNpc, Buff buff) {
        executeRemoveStatus(mapNpc, buff.qBuff());
    }

}
