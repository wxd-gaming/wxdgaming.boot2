package wxdgaming.boot2.core;

import lombok.Getter;
import lombok.Setter;

/**
 * 启动配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-14 15:55
 **/
@Getter
@Setter
public abstract class BootstrapProperties implements InitPrint {

    boolean debug;
    int gid;
    int sid;
    String name;
    String rpcToken = "9w283rn123r90cszh$#%^%^*&#$csf0892354";

}
