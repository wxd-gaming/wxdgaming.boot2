package wxdgaming.game.server.script.task.slog;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.slog.AbstractRoleSlog;

/**
 * 接受任务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-18 13:55
 */
@Getter
@Setter
public class SubmitTaskSlog extends AbstractRoleSlog {

    private int taskId;
    private String taskName;
    private String reason;

    public SubmitTaskSlog(Player player, int taskId, String taskName, String reason) {
        super(player);
        this.taskId = taskId;
        this.taskName = taskName;
        this.reason = reason;
    }

}
