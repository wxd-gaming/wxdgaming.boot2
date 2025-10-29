package wxdgaming.game.login.entity;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.batis.EntityLongUID;
import wxdgaming.boot2.starter.batis.ann.DbTable;
import wxdgaming.game.common.bean.ban.BanType;
import wxdgaming.game.common.bean.ban.BanVO;

/**
 * 封禁数据
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-29 15:23
 **/
@Getter
@Setter
@DbTable
public class BanEntity extends EntityLongUID {

    BanType banType;
    /** 根据类型确定key，比如账号，或者角色id */
    String key;
    /** 到期时间 */
    long expireTime;
    /** 备注 */
    String comment;

    public BanVO buildVO() {
        return this.toJSONObject().toJavaObject(BanVO.class);
    }

}
