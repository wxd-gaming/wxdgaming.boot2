package wxdgaming.game.login.cfg;


import lombok.Getter;
import wxdgaming.boot2.starter.excel.store.DataTable;
import wxdgaming.game.login.cfg.bean.QNotice;

import java.io.Serializable;
import java.util.Map;


/**
 * excel 构建 公告
gog, src/main/cfg/公告.xlsx, q_notice,
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-23 11:36:05
 **/
@Getter
public class QNoticeTable extends DataTable<QNotice> implements Serializable {

    @Override public void initDb() {
        /*todo 实现一些数据分组*/

    }

    @Override public void checkData(Map<Class<?>, DataTable<?>> store) {
        /*todo 实现数据校验 */
    }

}