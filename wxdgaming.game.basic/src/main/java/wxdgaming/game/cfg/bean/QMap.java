package wxdgaming.game.cfg.bean;


import lombok.Getter;
import wxdgaming.boot2.starter.excel.store.DataChecked;
import wxdgaming.game.cfg.bean.mapping.QMapMapping;

import java.io.Serializable;


/**
 * excel 构建 怪物表, src/main/cfg/地图信息.xlsx, q_map,
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-05 10:08:09
 **/
@Getter
public class QMap extends QMapMapping implements Serializable, DataChecked {

    @Override public void initAndCheck() throws Exception {
        /*todo 实现数据检测和初始化*/

    }

}
