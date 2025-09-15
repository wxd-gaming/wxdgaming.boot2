package wxdgaming.game.login.bean;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 传输数据
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-15 10:43
 **/
@Getter
@Setter
public class UserDataVo extends ObjectBase {

    private String account;
    private long createTime;
    private int appId;
    private String platform;
    /** 平台返回的 Channel id */
    private String platformChannelId;
    /** 平台返回的userid */
    private String platformUserId;
    private long loginCount;
    /** 是不是白名单 */
    private boolean white;
    private int gmLevel;
}
