package wxdgaming.game.test;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.threading.ExecutorUtil;
import wxdgaming.boot2.starter.WxdApplication;
import wxdgaming.boot2.starter.batis.sql.pgsql.MysqlScan;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlScan;
import wxdgaming.boot2.starter.net.NetScan;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.client.SocketClient;
import wxdgaming.boot2.starter.net.module.inner.RpcService;
import wxdgaming.boot2.starter.scheduled.ScheduledScan;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Main {

    public static void main(String[] args) throws Exception {
        RunApplication run = WxdApplication.run(
                CoreScan.class,
                ScheduledScan.class,
                NetScan.class,
                PgsqlScan.class,
                MysqlScan.class,
                Main.class
        );

        ExecutorUtil.getDefaultExecutor().schedule(
                () -> {
                    run.executeMethodWithAnnotated(Init.class);
                    log.info("热更新重载");
                },
                10,
                TimeUnit.SECONDS
        );

        ExecutorUtil.getDefaultExecutor().schedule(
                () -> {
                    RpcService rpcService = run.getInstance(RpcService.class);
                    SocketClient client = run.getInstance(SocketClient.class);
                    SocketSession socketSession = client.idleNullException();
                    CompletableFuture<JSONObject> future = rpcService.request(socketSession, "rpcIndex", RunResult.ok().fluentPut("a", "b"));
                    future.whenComplete((jsonObject, throwable) -> {
                        if (throwable != null) {
                            log.error("rpcIndex", throwable);
                        } else {
                            log.info("rpcIndex response {}", jsonObject);
                        }
                    });
                    socketSession.writeAndFlush("我是文本消息");
                },
                3,
                TimeUnit.SECONDS
        );

    }

}