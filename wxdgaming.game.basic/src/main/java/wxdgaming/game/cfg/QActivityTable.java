package wxdgaming.game.cfg;


import lombok.Getter;
import wxdgaming.boot2.starter.excel.store.DataTable;
import wxdgaming.game.cfg.bean.QActivity;

import java.io.Serializable;
import java.util.Map;


/**
 * excel 构建 活动, src/main/cfg/活动.xlsx, q_activity,
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-05 10:08:09
 **/
@Getter
public class QActivityTable extends DataTable<QActivity> implements Serializable {

    @Override public void initDb() {
        /*todo 实现一些数据分组*/

    }

    @Override public void checkData(Map<Class<?>, DataTable<?>> store) {
        /*todo 实现数据校验 */
    }

}