package wxdgaming.boot2.starter.net.server;


import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.ServiceModule;
import wxdgaming.boot2.core.reflect.ReflectContext;

/**
 * socket 模块
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-13 09:45
 **/
public class SocketModule extends ServiceModule {

    public SocketModule(ReflectContext reflectContext) {
        super(reflectContext);
    }

    @Override protected void bind() throws Throwable {
        SocketServerConfig serverConfig = BootConfig.getIns().getObject("socket.server", SocketServerConfig.class);
        if (serverConfig != null) {
            SocketServer server = new SocketServer(serverConfig);
            bindSingleton(SocketServer.class, server);


        }
    }

}
