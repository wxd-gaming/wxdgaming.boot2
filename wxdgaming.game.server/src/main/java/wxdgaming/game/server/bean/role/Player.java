package wxdgaming.game.server.bean.role;

import com.alibaba.fastjson.annotation.JSONField;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.starter.net.pojo.PojoBase;
import wxdgaming.game.bean.Vector3D;
import wxdgaming.game.bean.mail.MailPack;
import wxdgaming.game.bean.vip.VipInfo;
import wxdgaming.game.global.bean.role.OnlineInfo;
import wxdgaming.game.global.bean.role.PlayerSnap;
import wxdgaming.game.server.bean.ClientSessionMapping;
import wxdgaming.game.server.bean.MapKey;
import wxdgaming.game.server.bean.MapNpc;
import wxdgaming.game.server.bean.StatusConst;
import wxdgaming.game.server.bean.bag.BagPack;
import wxdgaming.game.server.bean.equip.EquipPack;
import wxdgaming.game.server.bean.task.TaskPack;

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
    private int appId;
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
    private EquipPack equipPack = new EquipPack();
    @JSONField(serialize = false, deserialize = false)
    private transient BlockingQueue<Runnable> eventList = new ArrayBlockingQueue<>(1024);
    @JSONField(serialize = false, deserialize = false)
    private transient ClientSessionMapping clientSessionMapping;

    public Player() {
        this.setMapObjectType(MapObjectType.Player);
    }

    public PlayerSnap toPlayerSnap() {
        PlayerSnap playerSnap = new PlayerSnap();
        buildPlayerSnap(playerSnap);
        return playerSnap;
    }

    public void buildPlayerSnap(PlayerSnap playerSnap) {

        playerSnap.setUid(getUid());
        playerSnap.setSid(getSid());
        playerSnap.setAccount(getAccount());
        playerSnap.setName(getName());
        playerSnap.setLevel(getLevel());
        if (getMapKey() != null) {
            playerSnap.setMapId(getMapKey().getMapId());
            playerSnap.setMapCfgId(getMapKey().getMapCfgId());
            playerSnap.setMapLine(getMapKey().getLine());
        }
        playerSnap.setVector3D(getPosition());
        playerSnap.setLastDirection(getLastDirection());
        playerSnap.setSex(getSex());
        playerSnap.setJob(getJob());

    }

    public void executor(Runnable task) {
        boolean add = eventList.add(task);
        AssertUtil.assertTrue(add, "事件队列已满，添加失败");
    }

    public boolean checkOnline() {
        return getClientSessionMapping() != null && getStatus().hasFlag(StatusConst.Online);
    }

    public void write(PojoBase pojoBase) {
        if (!checkOnline()) {
            return;
        }
        getClientSessionMapping().forwardMessage(this, pojoBase);
    }
}
