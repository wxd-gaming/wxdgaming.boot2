package wxdgaming.game.cfg.bean;


import lombok.Getter;
import wxdgaming.boot2.starter.excel.store.DataChecked;
import wxdgaming.boot2.starter.excel.store.DataTable;
import wxdgaming.game.cfg.bean.mapping.QAchieveMapping;

import java.io.Serializable;
import java.util.Map;


/**
 * excel 构建 成就集合, src/cfg/任务成就.xlsx, q_achieve,
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-03 15:28:21
 **/
@Getter
public class QAchieve extends QAchieveMapping implements Serializable, DataChecked {

    @Override public void initAndCheck(Map<Class<?>, DataTable<?>> store) throws Exception {
        /*todo 实现数据检测和初始化*/

    }

}
