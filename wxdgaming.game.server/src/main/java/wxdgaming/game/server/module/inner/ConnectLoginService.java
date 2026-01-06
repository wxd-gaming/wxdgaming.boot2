package wxdgaming.game.server.module.inner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.game.common.bean.login.ConnectLoginProperties;

/**
 * 内部服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-03 09:46
 **/
@Slf4j
@Service
public class ConnectLoginService implements InitPrint {

    final ConnectLoginProperties connectLoginProperties;

    public ConnectLoginService(ConnectLoginProperties connectLoginProperties) {

        this.connectLoginProperties = connectLoginProperties;
    }

}
