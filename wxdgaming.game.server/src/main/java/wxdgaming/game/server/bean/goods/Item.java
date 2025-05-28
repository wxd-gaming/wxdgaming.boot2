package wxdgaming.game.server.bean.goods;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.batis.EntityLongUID;

/**
 * 道具
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-21 19:44
 **/
@Getter
@Setter
public class Item extends EntityLongUID {

    private int cfgId;
    private boolean bind;
    private long count;
    private long createTime;
    /** 过期时间 */
    private long expirationTime;
    private JSONObject otherData = new JSONObject();

}
