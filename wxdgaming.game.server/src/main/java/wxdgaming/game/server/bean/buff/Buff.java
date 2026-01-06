package wxdgaming.game.server.bean.buff;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.core.lang.ObjectLong;
import wxdgaming.boot2.core.lang.Tuple2;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.excel.store.DataRepository;
import wxdgaming.game.cfg.QBuffTable;
import wxdgaming.game.cfg.bean.QBuff;
import wxdgaming.game.server.bean.MapNpc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

/**
 * 场景对象身上的buff
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-08 10:56
 **/
@Getter
@Setter
@Accessors(chain = true)
public class Buff extends ObjectLong {

    /** 是谁给我加在身上的 */
    private long sendUid;
    /** 是谁给我加在身上的 */
    @JSONField(serialize = false, deserialize = false)
    private transient MapNpc sender;
    /** 配置id */
    private int buffCfgId;
    /** 等级 */
    private int lv;
    /** 叠加层级 */
    private ArrayList<Tuple2<Long, Long>> timeList = new ArrayList<>();
    private long lastExecuteTime;
    /** 执行次数 */
    private int executeCount;

    @JSONField(serialize = false, deserialize = false)
    public boolean checkStart() {
        Tuple2<Long, Long> longLongTuple2 = timeList.stream().min(Comparator.comparing(Tuple2::getLeft)).get();
        return MyClock.millis() > longLongTuple2.getLeft();
    }

    @JSONField(serialize = false, deserialize = false)
    public boolean checkEnd() {
        Tuple2<Long, Long> longLongTuple2 = timeList.stream().max(Comparator.comparing(Tuple2::getLeft)).get();
        return MyClock.millis() > longLongTuple2.getRight();
    }

    @JSONField(serialize = false, deserialize = false)
    public boolean clearTime(long millis) {
        boolean result = timeList.isEmpty();
        Iterator<Tuple2<Long, Long>> iterator = timeList.iterator();
        while (iterator.hasNext()) {
            Tuple2<Long, Long> next = iterator.next();
            if (millis > next.getRight()) {
                iterator.remove();
                result = true;
            }
        }
        return result;
    }

    public QBuff qBuff() {
        QBuffTable qBuffTable = DataRepository.getIns().dataTable(QBuffTable.class);
        return qBuffTable.getSingleByIndex(buffCfgId, lv);
    }

}
