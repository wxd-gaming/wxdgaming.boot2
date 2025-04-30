package wxdgaming.game.test.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * 场景精灵对象
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-15 17:31
 **/
@Getter
@Setter
public class MapNpc extends MapObject {

    private int level;
    private long exp;
    /** 血量 */
    private long hp;
    /** 魔法 */
    private long mp;
    /** 体力 */
    private int physical;

    public MapNpc() {
        this.setMapObjectType(MapObjectType.Npc);
    }
}
