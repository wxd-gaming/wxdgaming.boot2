package wxdgaming.game.test.script.cdkey.bean;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

import java.util.ArrayList;

/**
 * cd key
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-29 10:57
 */
@Getter
@Setter
public class CDKeyEntity extends ObjectBase {

    private int uid;
    /** 备注 */
    private String comment;
    /** 使用类型，1个人，2本服，3全服 */
    private int useType;
    /** 全局使用次数 */
    private int useCount;
    /** 奖励道具 道具id|数量|绑定状态| */
    private ArrayList<CDKeyReward> rewards = new ArrayList<>();

    @Getter
    @Setter
    public static class CDKeyReward extends ObjectBase {

        /** 道具id */
        private int itemId;
        /** 道具数量 */
        private long count;
        /** 0非绑定, 1绑定 */
        private int bind;
        /** 过期时间 */
        private long expireTime;

    }

}
