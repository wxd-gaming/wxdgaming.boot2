package wxdgaming.game.server.cfg.bean;


import lombok.Getter;
import wxdgaming.boot2.starter.excel.store.DataChecked;
import wxdgaming.game.server.cfg.bean.mapping.QAchieveMapping;

import java.io.Serializable;


/**
 * excel 构建 成就集合, src/cfg/任务成就.xlsx, q_achieve,
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-02 11:17:36
 **/
@Getter
public class QAchieve extends QAchieveMapping implements Serializable, DataChecked {

    @Override public void initAndCheck() throws Exception {
        /*todo 实现数据检测和初始化*/

    }

}
