package run;

import wxdgaming.boot2.core.threading.ExecutorConfig;
import wxdgaming.boot2.core.threading.ExecutorUtil;
import wxdgaming.boot2.starter.net.server.SocketConfig;
import wxdgaming.boot2.starter.net.server.SocketServer;

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
        SocketConfig socketConfig = new SocketConfig();
        socketConfig.setEnabledHttp(false);
        SocketServer socketServer = new SocketServer(socketConfig);
        socketServer.start();
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
    }
}
