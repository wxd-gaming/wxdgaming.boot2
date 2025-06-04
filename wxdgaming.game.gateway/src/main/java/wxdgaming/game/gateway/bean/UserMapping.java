package wxdgaming.game.gateway.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.starter.net.SocketSession;

/**
 * 用户映射关系
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-28 17:03
 **/
@Getter
@Setter
@Accessors(chain = true)
public class UserMapping extends ObjectBase {

    private int chooseServerId;
    private String account;
    private String userId;
    private String token;
    private long chooseRoleId;
    private SocketSession chooseServerSession;
    private SocketSession clientSocketSession;

}
