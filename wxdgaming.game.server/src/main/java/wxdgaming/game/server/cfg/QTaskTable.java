package wxdgaming.game.server.cfg;


import lombok.Getter;
import wxdgaming.boot2.starter.excel.store.DataTable;
import wxdgaming.game.server.cfg.bean.QTask;

import java.io.Serializable;
import java.util.Map;


/**
 * excel 构建 任务集合, src/cfg/任务成就.xlsx, q_task,
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-02 11:17:36
 **/
@Getter
public class QTaskTable extends DataTable<QTask> implements Serializable {

    @Override public void initDb() {
        /*todo 实现一些数据分组*/

    }

    @Override public void checkData(Map<Class<?>, DataTable<?>> store) {
        /*todo 实现数据校验 */
    }

}