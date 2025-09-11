package wxdgaming.game.cfg;


import lombok.Getter;
import wxdgaming.boot2.starter.excel.store.DataTable;
import wxdgaming.game.cfg.bean.QActivity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * excel 构建 活动, src/main/cfg/活动.xlsx, q_activity,
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-05 10:08:09
 **/
@Getter
public class QActivityTable extends DataTable<QActivity> implements Serializable {

    private Map<Integer, Map<Integer, QActivity>> activityType2IdMap = new TreeMap<>();

    @Override public void initDb() {
        /*todo 实现一些数据分组*/
        List<QActivity> dataList = getDataList();
        for (QActivity qActivity : dataList) {
            Map<Integer, QActivity> idMap = activityType2IdMap.computeIfAbsent(qActivity.getType(), k -> new TreeMap<>());
            idMap.put(qActivity.getId(), qActivity);
        }

    }

    @Override public void checkData(Map<Class<?>, DataTable<?>> store) {
        /*todo 实现数据校验 */
    }

}