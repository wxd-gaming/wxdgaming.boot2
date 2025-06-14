package wxdgaming.game.cfg;


import lombok.Getter;
import wxdgaming.boot2.core.collection.Table;
import wxdgaming.boot2.starter.excel.store.DataTable;
import wxdgaming.game.cfg.bean.QBuff;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * excel 构建 buff, src/main/cfg/buff.xlsx, q_buff,
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-15 10:24:13
 **/
@Getter
public class QBuffTable extends DataTable<QBuff> implements Serializable {

    /** R:{@link QBuff#getGroup()}, C:{@link QBuff#getLv()}, value: {@link QBuff}} */
    Table<Integer, Integer, QBuff> groupLvTable;

    @Override public void initDb() {
        /*todo 实现一些数据分组*/
        Table<Integer, Integer, QBuff> tmpTable = new Table<>();
        List<QBuff> dataList = getDataList();
        for (QBuff qBuff : dataList) {
            tmpTable.put(qBuff.getGroup(), qBuff.getLv(), qBuff);
        }
        this.groupLvTable = tmpTable;
    }

    @Override public void checkData(Map<Class<?>, DataTable<?>> store) {
        /*todo 实现数据校验 */
    }

}