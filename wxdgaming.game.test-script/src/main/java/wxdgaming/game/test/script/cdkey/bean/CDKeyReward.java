package wxdgaming.game.test.script.cdkey.bean;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 礼包奖励
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-14 13:49
 **/
@Getter
@Setter
public class CDKeyReward extends ObjectBase {
    /** 道具id */
    private int itemId;
    /** 道具数量 */
    private long count;
    /** 0非绑定, 1绑定 */
    private int bind;
    /** 过期时间 */
    private long expireTime;
}
