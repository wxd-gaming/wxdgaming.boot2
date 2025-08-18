package wxdgaming.game.login.slog;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.game.basic.slog.AbstractLog;

/**
 * 账号日志
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-18 11:53
 **/
@Getter
@Setter
public class AccountRegisterLog extends AbstractLog {

    private String account;
    private String platform;
    private String channel;
    private String ip;

    public AccountRegisterLog(String account, String platform, String channel, String ip) {
        this.account = account;
        this.platform = platform;
        this.channel = channel;
        this.ip = ip;
    }

    @Override public String logType() {
        return "accountregisterlog";
    }

}
