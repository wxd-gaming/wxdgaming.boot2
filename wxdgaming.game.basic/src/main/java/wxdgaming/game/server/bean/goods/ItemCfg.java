package wxdgaming.game.server.bean.goods;

import lombok.Builder;
import lombok.Getter;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 道具配置，奖励
 * <p> id|数量|绑定|过期时间|职业|等级|性别|权重#id|数量|绑定|过期时间|职业|等级|性别|权重
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

    //---------一下配置是掉落特殊限定条件---------
    private Integer job;
    private Integer lv;
    private Integer sex;
    private Integer weight;

}
