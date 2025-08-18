package wxdgaming.game.cfg.bean;


import lombok.Getter;
import wxdgaming.boot2.starter.excel.store.DataChecked;
import wxdgaming.boot2.starter.excel.store.DataTable;
import wxdgaming.game.cfg.bean.mapping.QItemMapping;

import java.io.Serializable;
import java.util.Map;


/**
 * excel 构建 任务集合, src/cfg/道具.xlsx, q_item,
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-09 15:05:31
 **/
@Getter
public class QItem extends QItemMapping implements Serializable, DataChecked {

    private String toName;

    @Override public void initAndCheck(Map<Class<?>, DataTable<?>> store) throws Exception {
        /*todo 实现数据检测和初始化*/
        toName = "%s(%s)".formatted(this.getId(), this.getName());
    }

}
