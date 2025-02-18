package wxdgaming.boot2.starter.net.server;


import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.ServiceModule;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.reflect.ReflectContext;

/**
 * socket 模块
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-13 09:45
 **/
public class SocketServerModule extends ServiceModule {

    public SocketServerModule(ReflectContext reflectContext) {
        super(reflectContext);
    }

    @Override protected void bind() throws Throwable {
        {
            SocketServerConfig serverConfig = BootConfig.getIns().getNestedValue("socket.server", SocketServerConfig.class);
            if (serverConfig != null && serverConfig.getPort() > 0) {
                if (serverConfig.isEnabledWebSocket() && StringUtils.isBlank(serverConfig.getWebSocketPrefix())) {
                    throw new RuntimeException("WebSocket 模块配置错误，WebSocket 模块需要配置 WebSocket 前缀");
                }
                SocketServerImpl server = new SocketServerImpl(serverConfig);
                bindInstance(server);
            }
        }
        {
            SocketServerConfig serverConfig = BootConfig.getIns().getNestedValue("socket.server-second", SocketServerConfig.class);
            if (serverConfig != null && serverConfig.getPort() > 0) {
                if (serverConfig.isEnabledWebSocket() && StringUtils.isBlank(serverConfig.getWebSocketPrefix())) {
                    throw new RuntimeException("WebSocket 模块配置错误，WebSocket 模块需要配置 WebSocket 前缀");
                }
                SocketServerImpl2 server = new SocketServerImpl2(serverConfig);
                bindInstance(server);
            }
        }
    }

}
