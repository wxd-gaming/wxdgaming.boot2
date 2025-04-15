package wxdgaming.game.test.bean.entity;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.batis.ColumnType;
import wxdgaming.boot2.starter.batis.ann.DbColumn;
import wxdgaming.game.test.bean.MapKey;
import wxdgaming.game.test.bean.MapNpc;
import wxdgaming.game.test.bean.Vector3D;

/**
 * 角色
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-15 17:13
 **/
@Getter
@Setter
public class Player extends MapNpc {

    /** 上一次进入的地图 */
    @DbColumn(columnType = ColumnType.Json, length = 64)
    private MapKey lastMapKey;
    /** 上一次所在地图坐标， */
    private Vector3D lastPosition = new Vector3D();
    /** 朝向 */
    private int lastDirection;

}
