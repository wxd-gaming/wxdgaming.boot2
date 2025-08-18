package wxdgaming.game.cfg;


import lombok.Getter;
import wxdgaming.boot2.starter.excel.store.DataTable;
import wxdgaming.game.cfg.bean.QMap;

import java.io.Serializable;
import java.util.Map;


/**
 * excel 构建 怪物表, src/main/cfg/地图信息.xlsx, q_map,
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-05 10:08:09
 **/
@Getter
public class QMapTable extends DataTable<QMap> implements Serializable {

    @Override public void initDb() {
        /*todo 实现一些数据分组*/

    }

    @Override public void checkData(Map<Class<?>, DataTable<?>> store) {
        /*todo 实现数据校验 */
    }

}