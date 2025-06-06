package wxdgaming.game.cfg;


import lombok.Getter;
import wxdgaming.boot2.starter.excel.store.DataTable;
import wxdgaming.game.cfg.bean.QMonster;

import java.io.Serializable;
import java.util.Map;


/**
 * excel 构建 怪物表, src/main/cfg/怪物信息.xlsx, q_monster,
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-06 14:05:07
 **/
@Getter
public class QMonsterTable extends DataTable<QMonster> implements Serializable {

    @Override public void initDb() {
        /*todo 实现一些数据分组*/

    }

    @Override public void checkData(Map<Class<?>, DataTable<?>> store) {
        /*todo 实现数据校验 */
    }

}