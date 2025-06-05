package wxdgaming.game.cfg;


import lombok.Getter;
import wxdgaming.boot2.starter.excel.store.DataTable;
import wxdgaming.game.cfg.bean.QPlayer;

import java.io.Serializable;
import java.util.Map;


/**
 * excel 构建 怪物表, src/cfg/玩家信息.xlsx, q_player,
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-09 14:13:57
 **/
@Getter
public class QPlayerTable extends DataTable<QPlayer> implements Serializable {

    @Override public void initDb() {
        /*todo 实现一些数据分组*/

    }

    @Override public void checkData(Map<Class<?>, DataTable<?>> store) {
        /*todo 实现数据校验 */
    }

}