package wxdgaming.game.gateway.module.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.starter.net.SocketSession;

import java.util.List;

/**
 * 服务映射
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-28 10:24
 **/
@Getter
@Setter
@Accessors(chain = true)
public class ServerMapping extends ObjectBase {

    private int gid;
    private int mainSid;
    private List<Integer> sid;
    private SocketSession session;
    private String ip;
    private int port;
    private int webPort;

}
