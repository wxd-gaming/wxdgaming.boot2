package wxdgaming.game.cfg.bean;


import lombok.Getter;
import wxdgaming.boot2.starter.excel.store.DataChecked;
import wxdgaming.boot2.starter.excel.store.DataTable;
import wxdgaming.game.cfg.bean.mapping.QMonsterMapping;

import java.io.Serializable;
import java.util.Map;


/**
 * excel 构建 怪物表, src/main/cfg/怪物信息.xlsx, q_monster,
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-06 14:05:07
 **/
@Getter
public class QMonster extends QMonsterMapping implements Serializable, DataChecked {

    @Override public void initAndCheck(Map<Class<?>, DataTable<?>> store) throws Exception {
        /*todo 实现数据检测和初始化*/

    }

}
