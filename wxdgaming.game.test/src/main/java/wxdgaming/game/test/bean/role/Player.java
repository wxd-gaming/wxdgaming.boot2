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
import wxdgaming.game.test.bean.attr.AttrType;
import wxdgaming.game.test.bean.goods.BagPack;
import wxdgaming.game.test.bean.mail.MailPack;
import wxdgaming.game.test.bean.task.TaskPack;
import wxdgaming.game.test.bean.vip.VipInfo;
import wxdgaming.game.test.script.global.message.AttrBean;
import wxdgaming.game.test.script.global.message.ResUpdateAttr;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

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
    /** 朝向 */
    private int lastDirection;
    private int sex;
    private int job;
    private Int2IntOpenHashMap useCDKeyMap = new Int2IntOpenHashMap();
    private OnlineInfo onlineInfo = new OnlineInfo();
    private VipInfo vipInfo = new VipInfo();
    private BagPack bagPack = new BagPack();
    private TaskPack taskPack = new TaskPack();
    private MailPack mailPack = new MailPack();
    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    private transient BlockingQueue<Runnable> eventList = new ArrayBlockingQueue<>(1024);
    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    private transient SocketSession socketSession;

    public Player() {
        this.setMapObjectType(MapObjectType.Player);
    }

    public boolean checkOnline() {
        return getSocketSession() != null && getSocketSession().isOpen();
    }

    /** 推送生命变化 */
    public void sendHp() {
        if (!checkOnline()) {
            return;
        }
        ResUpdateAttr resUpdateAttr = new ResUpdateAttr();
        resUpdateAttr.getAttrs().add(new AttrBean().setAttrId(AttrType.HP.getCode()).setValue(getHp()));
        resUpdateAttr.getAttrs().add(new AttrBean().setAttrId(AttrType.MAXHP.getCode()).setValue(maxHp()));
        write(resUpdateAttr);
    }

    /** 推送魔法变化 */
    public void sendMp() {
        if (!checkOnline()) {
            return;
        }
        ResUpdateAttr resUpdateAttr = new ResUpdateAttr();
        resUpdateAttr.getAttrs().add(new AttrBean().setAttrId(AttrType.MP.getCode()).setValue(getMp()));
        resUpdateAttr.getAttrs().add(new AttrBean().setAttrId(AttrType.MAXMP.getCode()).setValue(maxMp()));
        write(resUpdateAttr);
    }

    public void write(PojoBase pojoBase) {
        if (!checkOnline()) {
            return;
        }
        getSocketSession().write(pojoBase);
    }

    public void writeAndFlush(PojoBase pojoBase) {
        if (!checkOnline()) {
            return;
        }
        getSocketSession().writeAndFlush(pojoBase);
    }

}
