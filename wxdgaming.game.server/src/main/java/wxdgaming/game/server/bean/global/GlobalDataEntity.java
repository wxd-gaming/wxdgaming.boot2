package wxdgaming.game.server.bean.global;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.starter.batis.Entity;
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
@Accessors(chain = true)
@DbTable(tableName = "global_data")
public class GlobalDataEntity extends Entity {

    @DbColumn(key = true)
    private int id;
    @DbColumn(key = true)
    private int sid;
    private boolean merge;
    @DbColumn(length = Integer.MAX_VALUE)
    private DataBase data;

    public GlobalDataType dataType() {
        return GlobalDataType.ofOrException(id);
    }

}
