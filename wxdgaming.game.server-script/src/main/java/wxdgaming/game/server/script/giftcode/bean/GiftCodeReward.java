package wxdgaming.game.server.script.giftcode.bean;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 礼包奖励
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-14 13:49
 **/
@Getter
@Setter
public class GiftCodeReward extends ObjectBase {

    private int cfgId;
    private long num;
    private boolean bind;
    private long expirationTime;

}
