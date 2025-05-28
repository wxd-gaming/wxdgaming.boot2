package wxdgaming.game.robot.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.starter.net.SocketSession;

/**
 * 机器人
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-27 13:50
 **/
@Getter
@Setter
@Accessors(chain = true)
public class Robot {

    private long rid;
    private String account;
    private int sid;
    private String name;
    private int level;
    private long exp;

    private SocketSession socketSession;
    private boolean sendLogin;
    private boolean loginEnd;

    @Override public String toString() {
        return "Robot{rid=%d, account='%s', name='%s'}".formatted(rid, account, name);
    }
}
