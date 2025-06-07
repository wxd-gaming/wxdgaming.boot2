package wxdgaming.game.server.bean.role;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.starter.net.pojo.PojoBase;
import wxdgaming.game.message.global.AttrBean;
import wxdgaming.game.message.global.ResUpdateAttr;
import wxdgaming.game.server.bean.*;
import wxdgaming.game.bean.attr.AttrType;
import wxdgaming.game.server.bean.bag.BagPack;
import wxdgaming.game.server.bean.mail.MailPack;
import wxdgaming.game.server.bean.task.TaskPack;
import wxdgaming.game.server.bean.vip.VipInfo;

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
    private String platform;
    private String platformUserId;
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
    private transient ClientSessionMapping clientSessionMapping;

    public Player() {
        this.setMapObjectType(MapObjectType.Player);
    }

    public void executor(Runnable task) {
        boolean add = eventList.add(task);
        AssertUtil.assertTrue(add, "事件队列已满，添加失败");
    }

    public boolean checkOnline() {
        return getClientSessionMapping() != null && getStatus().hasFlag(StatusConst.Online);
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
        getClientSessionMapping().forwardMessage(pojoBase);
    }

    public void writeAndFlush(PojoBase pojoBase) {
        if (!checkOnline()) {
            return;
        }
        getClientSessionMapping().forwardMessage(pojoBase);
    }

}
