package wxdgaming.boot2.starter.net.module.rpc;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.ann.Order;

/**
 * rpc 监听 绑定工厂
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-13 16:36
 **/
@Slf4j
@Getter
@Singleton
public class RpcListenerFactory {

    /** 相当于用 read and copy write方式作为线程安全性 */
    RpcListenerContent rpcListenerContent = null;

    @Init
    @Order(9)
    public void init(RunApplication runApplication) {
        rpcListenerContent = new RpcListenerContent(runApplication);
    }

}
