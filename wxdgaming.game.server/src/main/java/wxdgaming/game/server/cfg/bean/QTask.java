package wxdgaming.game.server.cfg.bean;


import lombok.Getter;
import wxdgaming.boot2.starter.excel.store.DataChecked;
import wxdgaming.game.server.cfg.bean.mapping.QTaskMapping;

import java.io.Serializable;


/**
 * excel 构建 任务集合, src/cfg/任务成就.xlsx, q_task,
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-03 09:22:40
 **/
@Getter
public class QTask extends QTaskMapping implements Serializable, DataChecked {

    @Override public void initAndCheck() throws Exception {
        /*todo 实现数据检测和初始化*/

    }

}
