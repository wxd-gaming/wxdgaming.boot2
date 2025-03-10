package wxdgaming.game.test;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.loader.ClassDirLoader;
import wxdgaming.boot2.core.reflect.ReflectContext;
import wxdgaming.boot2.core.threading.ExecutorUtil;
import wxdgaming.boot2.starter.RunApplicationSub;
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

        ClassDirLoader classDirLoader = new ClassDirLoader("wxdgaming.game.test-script/target/classes");
        ReflectContext reflectContext = ReflectContext.Builder.of(classDirLoader, "wxdgaming.game.test.script").build();
        RunApplicationSub runApplicationSub = WxdApplication.createRunApplicationSub(reflectContext);

        // ExecutorUtil.getInstance().getDefaultExecutor().schedule(
        //         () -> {
        //             runApplicationSub.executeMethodWithAnnotated(Init.class);
        //             log.info("热更新重载");
        //         },
        //         10,
        //         TimeUnit.SECONDS
        // );

        ExecutorUtil.getInstance().getDefaultExecutor().scheduleAtFixedDelay(
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
                    socketSession.write("我是文本消息");
                },
                10000,
                300,
                TimeUnit.MILLISECONDS
        );

    }

}