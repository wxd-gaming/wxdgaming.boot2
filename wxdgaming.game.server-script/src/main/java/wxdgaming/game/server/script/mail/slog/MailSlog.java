package wxdgaming.game.server.script.mail.slog;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.slog.AbstractRoleSlog;

import java.util.List;

/**
 * 邮件日志
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-26 10:41
 **/
@Setter
@Getter
public class MailSlog extends AbstractRoleSlog {

    private long mailId;
    private String sender;
    private String title;
    private String content;
    private List<String> contentArgs;
    private String items;
    private String logMsg;

    public MailSlog(Player player, long mailId, String sender, String title, String content, List<String> contentArgs, String items, String logMsg) {
        super(player);
        this.mailId = mailId;
        this.sender = sender;
        this.title = title;
        this.content = content;
        this.contentArgs = contentArgs;
        this.items = items;
        this.logMsg = logMsg;
    }


}
