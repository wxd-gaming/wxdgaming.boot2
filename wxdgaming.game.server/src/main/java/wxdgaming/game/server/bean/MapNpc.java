package wxdgaming.game.server.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.game.bean.attr.AttrInfo;
import wxdgaming.game.bean.attr.AttrType;
import wxdgaming.game.message.global.AttrBean;
import wxdgaming.game.message.global.ResUpdateAttr;
import wxdgaming.game.server.bean.buff.Buff;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 场景精灵对象
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-15 17:31
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
    private ArrayList<Buff> buffs = new ArrayList<>();
    private long fightValue;
    private AttrInfo finalAttrInfo = new AttrInfo();
    /** gm的固定属性 */
    private AttrInfo gmAttrInfo = new AttrInfo();
    /** gm的百分比提升属性 */
    private AttrInfo gmAttrProInfo = new AttrInfo();
    /** 临时属性 */
    @JSONField(serialize = false, deserialize = false)
    private transient AttrInfo tmpAttrInfo = new AttrInfo();
    /** 临时百分比提升属性 */
    @JSONField(serialize = false, deserialize = false)
    private transient AttrInfo tmpAttrProInfo = new AttrInfo();
    /** 分组属性 */
    @JSONField(serialize = false, deserialize = false)
    private transient HashMap<Integer, AttrInfo> attrMap = new HashMap<>();
    /** 分组百分比属性 */
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

    /** 推送生命变化 */
    public void sendHp() {
        ResUpdateAttr resUpdateAttr = new ResUpdateAttr();
        resUpdateAttr.setUid(this.getUid());
        resUpdateAttr.getAttrs().add(new AttrBean().setAttrId(AttrType.HP.getCode()).setValue(getHp()));
        resUpdateAttr.getAttrs().add(new AttrBean().setAttrId(AttrType.MAXHP.getCode()).setValue(maxHp()));
        write(resUpdateAttr);
    }

    /** 推送魔法变化 */
    public void sendMp() {
        ResUpdateAttr resUpdateAttr = new ResUpdateAttr();
        resUpdateAttr.setUid(this.getUid());
        resUpdateAttr.getAttrs().add(new AttrBean().setAttrId(AttrType.MP.getCode()).setValue(getMp()));
        resUpdateAttr.getAttrs().add(new AttrBean().setAttrId(AttrType.MAXMP.getCode()).setValue(maxMp()));
        write(resUpdateAttr);
    }


}
