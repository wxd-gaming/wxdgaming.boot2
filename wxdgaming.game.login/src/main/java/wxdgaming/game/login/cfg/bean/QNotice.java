package wxdgaming.game.login.cfg.bean;


import lombok.Getter;
import wxdgaming.boot2.starter.excel.store.DataChecked;
import wxdgaming.boot2.starter.excel.store.DataTable;
import wxdgaming.game.login.cfg.bean.mapping.QNoticeMapping;

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
public class QNotice extends QNoticeMapping implements Serializable, DataChecked {

    @Override public void initAndCheck(Map<Class<?>, DataTable<?>> store) throws Exception {
        /*todo 实现数据检测和初始化*/

    }

}
