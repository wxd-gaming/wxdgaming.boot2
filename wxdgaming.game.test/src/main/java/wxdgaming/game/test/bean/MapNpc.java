package wxdgaming.game.test.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.game.test.bean.attr.AttrInfo;
import wxdgaming.game.test.bean.attr.AttrType;

import java.util.HashMap;

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
    /** 生命 */
    private long hp;
    /** 魔法 */
    private long mp;
    /** 体力 */
    private int physical;
    private long fightValue;
    private AttrInfo finalAttrInfo = new AttrInfo();
    /** 分组属性 */
    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    private transient HashMap<Integer, AttrInfo> attrMap = new HashMap<>();
    /** 分组百分比属性 */

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    private transient HashMap<Integer, AttrInfo> attrProMap = new HashMap<>();

    public MapNpc() {
        this.setMapObjectType(MapObjectType.Npc);
    }

    public long maxHp() {
        return this.getFinalAttrInfo().get(AttrType.MAXHP);
    }

    public long maxMp() {
        return this.getFinalAttrInfo().get(AttrType.MAXMP);
    }

}
