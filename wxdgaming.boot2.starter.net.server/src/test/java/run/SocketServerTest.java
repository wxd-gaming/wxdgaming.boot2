package run;

import wxdgaming.boot2.core.threading.ExecutorConfig;
import wxdgaming.boot2.core.threading.ExecutorUtil;
import wxdgaming.boot2.starter.net.server.SocketServer;
import wxdgaming.boot2.starter.net.server.SocketServerConfig;
import wxdgaming.boot2.starter.net.server.http.HttpListenerFactory;
import wxdgaming.boot2.starter.net.server.pojo.ProtoListenerFactory;
import wxdgaming.boot2.starter.net.server.rpc.RpcListenerFactory;

import java.util.Scanner;

/**
 * 测试
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-13 16:18
 **/
public class SocketServerTest {

    public static void main(String[] args) {
        ExecutorUtil.init(new ExecutorConfig());
        SocketServerConfig socketServerConfig = new SocketServerConfig();
        socketServerConfig.setEnabledHttp(false);
        SocketServer socketServer = new SocketServer(socketServerConfig);
        socketServer.start(new ProtoListenerFactory(), new RpcListenerFactory(), new HttpListenerFactory());
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
    }

}
