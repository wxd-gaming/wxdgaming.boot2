package wxdgaming.game.server.script.buff;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.lang.Tuple2;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.starter.excel.store.DataRepository;
import wxdgaming.game.bean.buff.BuffType;
import wxdgaming.game.cfg.QBuffTable;
import wxdgaming.game.cfg.bean.QBuff;
import wxdgaming.game.core.Reason;
import wxdgaming.game.core.ReasonArgs;
import wxdgaming.game.server.bean.MapNpc;
import wxdgaming.game.server.bean.buff.Buff;
import wxdgaming.game.server.event.OnHeart;
import wxdgaming.game.server.event.OnHeartMinute;
import wxdgaming.game.server.module.data.DataCenterService;

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
    HashMap<BuffType, AbstractBuffAction> actionMap = new HashMap<>();

    @Inject
    public BuffService(DataCenterService dataCenterService) {
        this.dataCenterService = dataCenterService;
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
        while (iterator.hasNext()) {
            Buff buff = iterator.next();
            QBuff qBuff = qBuffTable.getGroupLvTable().get(buff.getBuffCfgId(), buff.getLv());
            if (qBuff == null) {
                log.warn("buff不存在 {}, {} {}", mapNpc, buff.getBuffCfgId(), buff.getLv());
                continue;
            }
            if (buff.getLastExecuteTime() + qBuff.getInterval() > mill) {
                continue;
            }
            executeBuff(mapNpc, buff, qBuff, mill);
            if (buff.checkTime(mill) && buff.getTimeList().isEmpty()) {
                log.debug("buff {}, 结束, {}", mapNpc, buff);
                iterator.remove();
            }
        }
    }

    public void executeBuff(MapNpc mapNpc, Buff buff, QBuff qBuff, long mill) {
        BuffType buffType = qBuff.getBuffType();
        AbstractBuffAction abstractBuffAction = actionMap.get(buffType);
        AssertUtil.assertNull(abstractBuffAction, "没有对应的buff处理类" + buffType);
        abstractBuffAction.doAction(mapNpc, buff, qBuff);
        buff.setLastExecuteTime(MyClock.millis());
        buff.setExecuteCount(buff.getExecuteCount() + 1);
    }

    public void addBuff(MapNpc sender, MapNpc targetMapNpc, int buffCfgId, int lv, ReasonArgs reasonArgs) {
        QBuffTable qBuffTable = DataRepository.getIns().dataTable(QBuffTable.class);
        QBuff qBuff = qBuffTable.getGroupLvTable().get(buffCfgId, lv);
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
            if (buff.getBuffCfgId() == qbuff.getGroup()) {
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
            log.debug("添加buff {}, 已有相同的buff {} 移除 {}", targetMapNpc, oldBuff, reasonArgs);
        }

        Buff newBuff = new Buff();
        newBuff.setUid(dataCenterService.getBuffHexid().newId());
        newBuff.setSendUid(sender.getUid());
        newBuff.setSender(sender);
        newBuff.setBuffCfgId(qbuff.getGroup());
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

        log.debug("添加buff {}, {}, {}", targetMapNpc, newBuff, reasonArgs);
    }

    public void removeBuff(MapNpc mapNpc, int buffCfgId) {

    }

    public void removeBuff(MapNpc mapNpc, Buff buff) {
        mapNpc.getBuffs().remove(buff);
    }
}
