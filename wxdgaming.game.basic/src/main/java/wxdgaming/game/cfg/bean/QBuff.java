package wxdgaming.game.cfg.bean;


import lombok.Getter;
import wxdgaming.boot2.starter.excel.store.DataChecked;
import wxdgaming.boot2.starter.excel.store.DataTable;
import wxdgaming.game.bean.buff.BuffType;
import wxdgaming.game.bean.buff.BuffTypeConst;
import wxdgaming.game.cfg.bean.mapping.QBuffMapping;

import java.io.Serializable;
import java.util.Map;


/**
 * excel 构建 buff, src/main/cfg/buff.xlsx, q_buff,
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-15 10:24:13
 **/
@Getter
public class QBuff extends QBuffMapping implements Serializable, DataChecked {

    protected BuffType buffType;

    @Override public void initAndCheck(Map<Class<?>, DataTable<?>> store) throws Exception {
        /*todo 实现数据检测和初始化*/
        buffType = BuffTypeConst.ofOrException(this.getType());

    }

}
