package wxdgaming.game.test.bean;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.bit.BitFlag;
import wxdgaming.boot2.starter.batis.ColumnType;
import wxdgaming.boot2.starter.batis.ann.DbColumn;

/**
 * 场景对象
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-15 17:15
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

    private MapKey mapKey;

    private Vector3D position = new Vector3D();
    /** 朝向 */
    private int direction;

    private int cfgId;
    private String name;
    private MapObjectType mapObjectType;
    private BitFlag status = new BitFlag();

    @Override public String toString() {
        return "%s{uid=%s, name='%s'}".formatted(this.getClass().getSimpleName(), getUid(), name);
    }
}
