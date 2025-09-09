package wxdgaming.boot2.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

/**
 * 启动配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-14 15:55
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BootstrapProperties implements InitPrint {

    boolean debug;
    int gid;
    int sid;
    List<Integer> mergedSidList = Collections.emptyList();
    String name;
    String rpcToken = "9w283rn123r90cszh$#%^%^*&#$csf0892354";

}
