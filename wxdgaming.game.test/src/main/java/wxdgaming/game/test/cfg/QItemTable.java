package wxdgaming.game.test.cfg;


import lombok.Getter;
import wxdgaming.boot2.starter.excel.store.DataTable;
import wxdgaming.game.test.cfg.bean.QItem;

import java.io.Serializable;
import java.util.Map;


/**
 * excel 构建 任务集合, src/cfg/道具.xlsx, q_item,
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-09 15:05:31
 **/
@Getter
public class QItemTable extends DataTable<QItem> implements Serializable {

    @Override public void initDb() {
        /*todo 实现一些数据分组*/

    }

    @Override public void checkData(Map<Class<?>, DataTable<?>> store) {
        /*todo 实现数据校验 */
    }

}