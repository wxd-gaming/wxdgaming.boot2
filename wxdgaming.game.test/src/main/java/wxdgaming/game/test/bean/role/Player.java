package wxdgaming.game.test.bean.role;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.batis.ColumnType;
import wxdgaming.boot2.starter.batis.ann.DbColumn;
import wxdgaming.boot2.starter.batis.ann.DbTable;
import wxdgaming.game.test.bean.MapKey;
import wxdgaming.game.test.bean.MapNpc;
import wxdgaming.game.test.bean.Vector3D;

import java.util.HashMap;

/**
 * 角色
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-15 17:13
 **/
@Getter
@Setter
@DbTable
public class Player extends MapNpc {

    /** 是否已经删除 */
    private boolean del;
    private int sid;
    @DbColumn(index = true, length = 64)
    private String account;
    @DbColumn(columnType = ColumnType.Json, length = 2048)
    private HashMap<String, Object> clientData = new HashMap<>();
    /** 上一次进入的地图 */
    @DbColumn(columnType = ColumnType.Json, length = 128)
    private MapKey lastMapKey;
    /** 上一次所在地图坐标， */
    @DbColumn(columnType = ColumnType.Json, length = 128)
    private Vector3D lastPosition = new Vector3D();
    /** 朝向 */
    private int lastDirection;
    private int sex;
    private int job;

    public Player() {
        this.setMapObjectType(MapObjectType.Player);
    }

}
