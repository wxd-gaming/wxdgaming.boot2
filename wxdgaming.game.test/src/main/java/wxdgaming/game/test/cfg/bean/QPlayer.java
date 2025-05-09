package wxdgaming.game.test.cfg.bean;


import lombok.Getter;
import wxdgaming.boot2.starter.excel.store.DataChecked;
import wxdgaming.game.test.cfg.bean.mapping.QPlayerMapping;

import java.io.Serializable;


/**
 * excel 构建 怪物表, src/cfg/玩家信息.xlsx, q_player,
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-09 14:13:57
 **/
@Getter
public class QPlayer extends QPlayerMapping implements Serializable, DataChecked {

    @Override public void initAndCheck() throws Exception {
        /*todo 实现数据检测和初始化*/

    }

}
