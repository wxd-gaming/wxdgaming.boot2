package wxdgaming.game.login.bean;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 数据对象
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-09 13:28
 **/
@Getter
@Setter
public class ServerInfoDTO extends ObjectBase {

    private int sid;
    private int port;
    private int httpPort;
    private int onlineSize;

}
