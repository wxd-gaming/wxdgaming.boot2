package wxdgaming.game.server.bean.goods;

import lombok.Builder;
import lombok.Getter;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 道具配置，奖励
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-24 10:34
 **/
@Getter
@Builder(toBuilder = true)
public class ItemCfg extends ObjectBase {

    private int cfgId;
    private long num;
    private boolean bind;
    private long expirationTime;

}
