package wxdgaming.game.login.cfg.bean;


import lombok.Getter;
import wxdgaming.boot2.starter.excel.store.DataChecked;
import wxdgaming.boot2.starter.excel.store.DataTable;
import wxdgaming.game.login.cfg.bean.mapping.QCdkeyMapping;

import java.io.Serializable;
import java.util.Map;


/**
 * excel 构建 任务集合, src/main/cfg/激活码.xlsx, q_cdkey,
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-01 11:13:06
 **/
@Getter
public class QCdkey extends QCdkeyMapping implements Serializable, DataChecked {

    @Override public void initAndCheck(Map<Class<?>, DataTable<?>> store) throws Exception {
        /*todo 实现数据检测和初始化*/

    }

}
