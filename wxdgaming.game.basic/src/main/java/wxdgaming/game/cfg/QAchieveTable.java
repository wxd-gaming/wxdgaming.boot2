package wxdgaming.game.cfg;


import lombok.Getter;
import wxdgaming.boot2.starter.excel.store.DataTable;
import wxdgaming.game.cfg.bean.QAchieve;

import java.io.Serializable;
import java.util.Map;


/**
 * excel 构建 成就集合, src/cfg/任务成就.xlsx, q_achieve,
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-03 15:28:21
 **/
@Getter
public class QAchieveTable extends DataTable<QAchieve> implements Serializable {

    @Override public void initDb() {
        /*todo 实现一些数据分组*/

    }

    @Override public void checkData(Map<Class<?>, DataTable<?>> store) {
        /*todo 实现数据校验 */
    }

}