package wxdgaming.game.server.script.buff;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.lang.Tuple2;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.excel.store.DataRepository;
import wxdgaming.game.cfg.QBuffTable;
import wxdgaming.game.cfg.bean.QBuff;
import wxdgaming.game.core.ReasonArgs;
import wxdgaming.game.server.bean.MapNpc;
import wxdgaming.game.server.bean.buff.Buff;
import wxdgaming.game.server.event.OnHeart;
import wxdgaming.game.server.module.data.DataCenterService;

import java.util.ArrayList;

/**
 * buff管理
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-08 10:16
 **/
@Slf4j
@Singleton
public class BuffService {

    final DataCenterService dataCenterService;

    @Inject
    public BuffService(DataCenterService dataCenterService) {
        this.dataCenterService = dataCenterService;
    }

    @OnHeart
    public void onHeartBuffAction(MapNpc mapNpc, long mill) {
        QBuffTable qBuffTable = DataRepository.getIns().dataTable(QBuffTable.class);
        ArrayList<Buff> buffs = mapNpc.getBuffs();
        for (Buff buff : buffs) {

            QBuff qBuff = qBuffTable.getGroupLvTable().get(buff.getBuffCfgId(), buff.getLv());

        }
    }

    public void executeBuff(MapNpc mapNpc, Buff buff) {

    }

    public void addBuff(MapNpc mapNpc, int buffCfgId, int lv, ReasonArgs reasonArgs) {
        QBuffTable qBuffTable = DataRepository.getIns().dataTable(QBuffTable.class);
        QBuff qBuff = qBuffTable.getGroupLvTable().get(buffCfgId, lv);
        addBuff(mapNpc, qBuff, reasonArgs);
    }

    public void addBuff(MapNpc mapNpc, int buffId, ReasonArgs reasonArgs) {
        QBuff qBuff = DataRepository.getIns().dataTable(QBuffTable.class, buffId);
        addBuff(mapNpc, qBuff, reasonArgs);
    }


    public void addBuff(MapNpc mapNpc, QBuff qbuff, ReasonArgs reasonArgs) {
        ArrayList<Buff> buffs = mapNpc.getBuffs();
        Buff oldBuff = null;
        for (Buff buff : buffs) {
            if (buff.getBuffCfgId() == qbuff.getId()) {
                oldBuff = buff;
                break;
            }
        }
        int addType = qbuff.getAddType();
        if (addType == 1 && oldBuff != null) {
            log.debug("添加buff {}, 已有相同的buff {} 忽略 {}", mapNpc, oldBuff, reasonArgs);
            return;
        }

        if (addType == 2 && oldBuff != null) {
            Tuple2<Long, Long> tuple2 = new Tuple2<>(MyClock.millis(), MyClock.millis() + qbuff.getDuration());
            oldBuff.getTimeList().add(tuple2);
            log.debug("添加buff {}, 已有相同的buff {} 叠加 {}, {}", mapNpc, oldBuff, tuple2, reasonArgs);
            return;
        }

        if (oldBuff != null) {
            buffs.remove(oldBuff);
            log.debug("添加buff {}, 已有相同的buff {} 移除 {}", mapNpc, oldBuff, reasonArgs);
        }

        Buff newBuff = new Buff();
        newBuff.setUid(dataCenterService.getBuffHexid().newId());
        newBuff.setBuffCfgId(qbuff.getGroup());
        newBuff.setLv(qbuff.getLv());
        Tuple2<Long, Long> tuple2 = new Tuple2<>(MyClock.millis(), MyClock.millis() + qbuff.getDuration());
        newBuff.getTimeList().add(tuple2);
        newBuff.setLastExecuteTime(tuple2.getLeft());
        newBuff.setInterval(qbuff.getInterval());
        newBuff.setExecuteCount(0);

        if (qbuff.isAddExecutor()) {
            executeBuff(mapNpc, newBuff);
        }

        if (qbuff.getDuration() < 100) {
            log.debug("添加buff {}, {}, buff持续时间过小，视为一次性buff ,{}", mapNpc, newBuff, reasonArgs);
            return;
        }

        buffs.add(newBuff);

        log.debug("添加buff {}, {}, {}", mapNpc, newBuff, reasonArgs);
    }

    public void removeBuff(MapNpc mapNpc, int buffCfgId) {

    }

    public void removeBuff(MapNpc mapNpc, Buff buff) {
        mapNpc.getBuffs().remove(buff);
    }
}
