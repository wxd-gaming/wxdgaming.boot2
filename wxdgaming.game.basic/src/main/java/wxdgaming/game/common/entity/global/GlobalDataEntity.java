package wxdgaming.game.common.entity.global;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.batis.ColumnType;
import wxdgaming.boot2.starter.batis.EntityIntegerUID;
import wxdgaming.boot2.starter.batis.ann.DbColumn;
import wxdgaming.boot2.starter.batis.ann.DbTable;
import wxdgaming.game.common.bean.global.AbstractGlobalData;

/**
 * 全局数据
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-08 14:13
 **/
@Getter
@Setter
@DbTable
public class GlobalDataEntity extends EntityIntegerUID {

    @DbColumn(index = true)
    private int sid;
    @DbColumn(index = true)
    private int type;
    @DbColumn(comment = "数据注释")
    private String comment;
    @DbColumn(index = true)
    private boolean merger;
    @DbColumn(columnType = ColumnType.String, length = 16 * 1024 * 1024)
    private AbstractGlobalData data;

    public <R extends AbstractGlobalData> R globalData() {
        return (R) data;
    }

}
