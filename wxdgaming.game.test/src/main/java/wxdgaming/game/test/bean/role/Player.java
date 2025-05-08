package wxdgaming.game.test.bean.role;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.pojo.PojoBase;
import wxdgaming.game.test.bean.MapKey;
import wxdgaming.game.test.bean.MapNpc;
import wxdgaming.game.test.bean.Vector3D;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 角色
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-15 17:13
 **/
@Getter
@Setter
public class Player extends MapNpc {

    /** 是否已经删除 */
    private boolean del;
    private int sid;
    private String account;
    private HashMap<String, Object> clientData = new HashMap<>();
    /** 上一次进入的地图 */
    private MapKey lastMapKey;
    /** 上一次所在地图坐标， */
    private Vector3D lastPosition = new Vector3D();
    private long lastLoginTime;
    /** 朝向 */
    private int lastDirection;
    private int sex;
    private int job;
    private Int2IntOpenHashMap useCDKeyMap = new Int2IntOpenHashMap();

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    private transient ArrayList<Runnable> eventList = new ArrayList<>();
    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    private transient SocketSession socketSession;

    public Player() {
        this.setMapObjectType(MapObjectType.Player);
    }

    public boolean checkOnline() {
        return getSocketSession() != null && getSocketSession().isOpen();
    }

    public void write(PojoBase pojoBase) {
        getSocketSession().write(pojoBase);
    }

    public void writeAndFlush(PojoBase pojoBase) {
        getSocketSession().writeAndFlush(pojoBase);
    }

}
