package wxdgaming.game.global.bean.role;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

/**
 * 在线信息
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-12 13:20
 **/
@Getter
@Setter
public class OnlineInfo {
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
