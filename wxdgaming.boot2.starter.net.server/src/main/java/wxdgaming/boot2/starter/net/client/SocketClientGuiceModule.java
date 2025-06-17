package wxdgaming.boot2.starter.net.client;


import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.ServiceGuiceModule;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.reflect.ReflectProvider;

/**
 * socket 模块
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-13 09:45
 **/
public class SocketClientGuiceModule extends ServiceGuiceModule {

    public SocketClientGuiceModule(ReflectProvider reflectProvider) {
        super(reflectProvider);
    }

    @Override protected void bind() throws Throwable {
        {
            SocketClientConfig clientConfig = BootConfig.getIns().getNestedValue("socket.client", SocketClientConfig.class);
            if (clientConfig != null && clientConfig.getPort() > 0) {
                if (clientConfig.isEnabledWebSocket()) {
                    if (StringUtils.isBlank(clientConfig.getWebSocketPrefix())) {
                        throw new RuntimeException("WebSocket 模块配置错误，WebSocket 模块需要配置 WebSocket 前缀");
                    }
                }
                SocketClientImpl socketClient = new SocketClientImpl(clientConfig);
                bindInstance(SocketClient.class, socketClient);
                bindInstance(SocketClientImpl.class, socketClient);
            }
        }
    }

}
