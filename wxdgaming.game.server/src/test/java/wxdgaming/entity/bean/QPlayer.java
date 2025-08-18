package wxdgaming.entity.bean;


import lombok.Getter;
import wxdgaming.boot2.starter.excel.store.DataChecked;
import wxdgaming.boot2.starter.excel.store.DataTable;
import wxdgaming.entity.bean.mapping.QPlayerMapping;

import java.io.Serializable;
import java.util.Map;


/**
 * excel 构建 怪物表, src/cfg/玩家信息.xlsx, q_player,
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-09 10:46:28
 **/
@Getter
public class QPlayer extends QPlayerMapping implements Serializable, DataChecked {

    @Override public void initAndCheck(Map<Class<?>, DataTable<?>> store) throws Exception {
        /*todo 实现数据检测和初始化*/

    }

}
