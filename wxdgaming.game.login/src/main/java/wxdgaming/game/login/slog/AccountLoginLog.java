package wxdgaming.game.login.slog;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.game.common.bean.slog.AbstractLog;

/**
 * 角色登录日志
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-12 19:46
 **/
@Getter
@Setter
public class AccountLoginLog extends AbstractLog {

    private String account;
    private String platform;
    private String channel;
    private String ip;

    public AccountLoginLog(String account, String platform, String channel, String ip) {
        this.account = account;
        this.platform = platform;
        this.channel = channel;
        this.ip = ip;
    }

    @Override public String logType() {
        return "accountloginlog";
    }

}
