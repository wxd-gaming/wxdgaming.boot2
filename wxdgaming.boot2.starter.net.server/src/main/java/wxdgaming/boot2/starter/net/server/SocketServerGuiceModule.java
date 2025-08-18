package wxdgaming.boot2.starter.net.server;


import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.ServiceGuiceModule;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.reflect.ReflectProvider;
import wxdgaming.boot2.starter.net.server.http.HttpServerConfig;

import java.util.function.Supplier;

/**
 * socket 模块
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-13 09:45
 **/
public class SocketServerGuiceModule extends ServiceGuiceModule {

    public static Supplier<Object> DEFAULT_INSTANCE = SocketServerConfig::new;

    public SocketServerGuiceModule(ReflectProvider reflectProvider) {
        super(reflectProvider);
    }

    @Override protected void bind() throws Throwable {
        {
            SocketServerConfig serverConfig = BootConfig.getIns().getNestedValue("socket.server", SocketServerConfig.class, DEFAULT_INSTANCE);
            if (serverConfig != null && serverConfig.getPort() > 0) {
                if (serverConfig.isEnabledWebSocket() && StringUtils.isBlank(serverConfig.getWebSocketPrefix())) {
                    throw new RuntimeException("WebSocket 模块配置错误，WebSocket 模块需要配置 WebSocket 前缀");
                }
                SocketServer server = new SocketServer(serverConfig);
                bindInstance(server);
                bindInstance(SocketServer.class, "socket.server", server);
                bindInstance("socket.server.config", serverConfig);
            }
            HttpServerConfig httpServerConfig = BootConfig.getIns().getNestedValue("socket.server.http", HttpServerConfig.class, HttpServerConfig.INSTANCE);
            bindInstance("socket.server.http.config", httpServerConfig);
        }
        {
            SocketServerConfig serverConfig = BootConfig.getIns().getNestedValue("socket.server-second", SocketServerConfig.class);
            if (serverConfig != null && serverConfig.getPort() > 0) {
                if (serverConfig.isEnabledWebSocket() && StringUtils.isBlank(serverConfig.getWebSocketPrefix())) {
                    throw new RuntimeException("WebSocket 模块配置错误，WebSocket 模块需要配置 WebSocket 前缀");
                }
                SocketServer server = new SocketServer(serverConfig);
                bindInstance(SocketServer.class, "socket.server-second", server);
            }
        }
    }

}
