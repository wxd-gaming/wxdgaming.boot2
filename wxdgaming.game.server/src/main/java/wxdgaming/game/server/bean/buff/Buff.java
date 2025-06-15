package wxdgaming.game.server.bean.buff;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.core.lang.ObjectLong;
import wxdgaming.boot2.core.lang.Tuple2;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.excel.store.DataRepository;
import wxdgaming.game.cfg.QBuffTable;
import wxdgaming.game.cfg.bean.QBuff;

import java.util.ArrayList;

/**
 * 场景对象身上的buff
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-08 10:56
 **/
@Getter
@Setter
@Accessors(chain = true)
public class Buff extends ObjectLong {

    /** 配置id */
    private int buffCfgId;
    /** 等级 */
    private int lv;
    /** 叠加层级 */
    private ArrayList<Tuple2<Long, Long>> timeList = new ArrayList<>();
    private long lastExecuteTime;
    /** 执行间隔时间 */
    private int interval;
    /** 执行次数 */
    private int executeCount;

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public boolean checkStart() {
        return MyClock.millis() > timeList.getFirst().getLeft();
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public boolean checkEnd() {
        return MyClock.millis() > getTimeList().getFirst().getRight();
    }

    public QBuff qBuff() {
        QBuffTable qBuffTable = DataRepository.getIns().dataTable(QBuffTable.class);
        return qBuffTable.getGroupLvTable().get(buffCfgId, lv);
    }

}
