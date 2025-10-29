package wxdgaming.game.common.bean.ban;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-29 15:33
 **/
@Getter
@Setter
public class BanVO extends ObjectBase {

    BanType banType;
    /** 根据类型确定key，比如账号，或者角色id */
    String key;
    /** 到期时间 */
    long expireTime;
    /** 备注 */
    String comment;

}
