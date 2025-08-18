package wxdgaming.logserver.module.system;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.InitPrint;

/**
 * 系统服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-09 18:02
 **/
@Slf4j
@Singleton
public class SystemService implements InitPrint {

    @Inject
    public SystemService() {
    }

}
