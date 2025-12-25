package wxdgaming.game.cfg;


import lombok.Getter;
import wxdgaming.boot2.starter.excel.store.DataTable;
import wxdgaming.boot2.starter.excel.store.Index;
import wxdgaming.game.cfg.bean.QBuff;

import java.io.Serializable;
import java.util.Map;


/**
 * excel 构建 buff, src/main/cfg/buff.xlsx, q_buff,
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-15 10:24:13
 **/
@Getter
@Index(value = {"buffId", "lv"}, single = true)
@Index(name = "type", value = {"type"}, single = true)
public class QBuffTable extends DataTable<QBuff> implements Serializable {

    @Override public void initDb() {
        /*todo 实现一些数据分组*/
    }

    @Override public void checkData(Map<Class<?>, DataTable<?>> store) {
        /*todo 实现数据校验 */
        int p = 0;
    }

}