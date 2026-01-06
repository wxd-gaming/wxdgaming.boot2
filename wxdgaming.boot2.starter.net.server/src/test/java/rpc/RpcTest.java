package rpc;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.RpcRequest;
import wxdgaming.boot2.starter.net.client.SocketClient;
import wxdgaming.boot2.starter.net.module.rpc.RpcService;
import wxdgaming.boot2.starter.net.server.SocketServer;

/**
 * q
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-04 15:58
 **/
@Component
@RequestMapping
public class RpcTest {

    final SocketClient socketClient;
    final SocketServer socketServer;
    final RpcService rpcService;

    public RpcTest(SocketClient socketClient, SocketServer socketServer, RpcService rpcService) {
        this.socketClient = socketClient;
        this.socketServer = socketServer;
        this.rpcService = rpcService;
    }

    @RpcRequest
    public String hello(@RequestParam("k1") String k1) {
        return "hello " + k1;
    }

    public void r1() throws InterruptedException {
        Thread.sleep(2000);
        SocketSession idle = socketClient.idle();
        rpcService.request(idle, "rpctest/hello", new JSONObject().fluentPut("k1", "wxdgaming")).subscribe(System.out::println);
    }

}
