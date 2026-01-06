package wxdgaming.game.server.entity.role;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 在线信息
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-12 13:20
 **/
@Getter
@Setter
public class OnlineInfo extends ObjectBase {
    private long loginCount;
    private long lastLoginDayTime;
    private long lastLoginTime;
    private long lastLogoutTime;
    /** 本次在线秒数 */
    private transient long onlineMills = 0;
    /** 总计在线秒数 */
    private long onlineTotalMills = 0;
    /** 最后一次刷新总计在线秒数时间 */
    @JSONField(serialize = false, deserialize = false)
    private transient long lastUpdateOnlineTime = 0;
}
