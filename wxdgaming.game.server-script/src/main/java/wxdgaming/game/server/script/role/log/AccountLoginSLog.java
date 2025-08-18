package wxdgaming.game.server.script.role.log;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.game.slog.AbstractSLog;

/**
 * 角色登录日志
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-12 19:46
 **/
@Getter
@Setter
public class AccountLoginSLog extends AbstractSLog {

    private String openId;
    private String account;
    private String platform;
    private String channel;
    private String ip;
    private String clientData;

    public AccountLoginSLog(String openId, String account, String platform, String channel, String ip, String clientData) {
        this.openId = openId;
        this.account = account;
        this.platform = platform;
        this.channel = channel;
        this.ip = ip;
        this.clientData = clientData;
    }

    @Override public String logType() {
        return "accountloginlog";
    }

}
