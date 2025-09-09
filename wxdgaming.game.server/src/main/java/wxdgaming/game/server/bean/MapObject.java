package wxdgaming.game.server.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.bit.BitFlag;
import wxdgaming.boot2.starter.net.pojo.PojoBase;

/**
 * 场景对象
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-15 17:15
 **/
@Getter
@Setter
public class MapObject extends GameBase {

    public enum MapObjectType {
        Player,
        Npc,
        Monster,
        Item,
    }

    private MapKey mapKey = new MapKey(0, 0, 0);

    private Vector3D position = new Vector3D();
    /** 朝向 */
    private int direction;

    private int cfgId;
    private String name;
    private MapObjectType mapObjectType;
    /** 状态 */
    @JSONField(serialize = false, deserialize = false)
    private BitFlag status = new BitFlag();

    public void write(PojoBase pojoBase) {
    }

    @Override public String toString() {
        return "%s{uid=%s, name='%s'}".formatted(this.getClass().getSimpleName(), getUid(), name);
    }
}
