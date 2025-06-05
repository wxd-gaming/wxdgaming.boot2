package wxdgaming.game.cfg.bean;


import lombok.Getter;
import wxdgaming.boot2.starter.excel.store.DataChecked;
import wxdgaming.game.cfg.bean.mapping.QItemMapping;

import java.io.Serializable;


/**
 * excel 构建 任务集合, src/cfg/道具.xlsx, q_item,
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-09 15:05:31
 **/
@Getter
public class QItem extends QItemMapping implements Serializable, DataChecked {

    @Override public void initAndCheck() throws Exception {
        /*todo 实现数据检测和初始化*/

    }

}
