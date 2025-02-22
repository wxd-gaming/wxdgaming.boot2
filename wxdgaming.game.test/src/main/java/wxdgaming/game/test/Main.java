package wxdgaming.game.test;

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

import java.util.concurrent.TimeUnit;

@Slf4j
public class Main {

    public static void main(String[] args) throws Exception {
        RunApplication runApplication = WxdApplication.run(
                CoreScan.class,
                ScheduledScan.class,
                NetScan.class,
                PgsqlScan.class,
                MysqlScan.class,
                Main.class
        );

        ExecutorUtil.getInstance().getDefaultExecutor().schedule(
                () -> {
                    runApplication.executeMethodWithAnnotated(Init.class);
                    log.info("热更新重载");
                },
                10,
                TimeUnit.SECONDS
        );

        ExecutorUtil.getInstance().getDefaultExecutor().schedule(
                () -> {
                    RpcService rpcService = runApplication.getInstance(RpcService.class);
                    SocketClient client = runApplication.getInstance(SocketClient.class);
                    SocketSession socketSession = client.idleNullException();
                    rpcService.request(socketSession, "rpcIndex", RunResult.ok().fluentPut("a", "b"))
                            .whenComplete((jsonObject, throwable) -> {
                                if (throwable != null) {
                                    log.error("rpcIndex", throwable);
                                } else {
                                    log.info("rpcIndex response {}", jsonObject);
                                }
                            });
                    rpcService.request(socketSession, "rpcIndex2", RunResult.ok().fluentPut("a", "b"))
                            .whenComplete((jsonObject, throwable) -> {
                                if (throwable != null) {
                                    log.error("rpcIndex2", throwable);
                                } else {
                                    log.info("rpcIndex2 response {}", jsonObject);
                                }
                            });
                    socketSession.writeAndFlush("我是文本消息");
                },
                3,
                TimeUnit.SECONDS
        );

    }

}