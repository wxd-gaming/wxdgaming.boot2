package wxdgaming.game.test.bean.global;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.starter.batis.ann.DbColumn;
import wxdgaming.boot2.starter.batis.ann.DbTable;

/**
 * 全局数据
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-29 13:18
 **/
@Getter
@Setter
@DbTable
public class GlobalDataEntity extends ObjectBase {

    @DbColumn(key = true)
    private int id;
    @DbColumn(key = true)
    private int sid;
    private boolean merge;
    private GlobalData data;

}
