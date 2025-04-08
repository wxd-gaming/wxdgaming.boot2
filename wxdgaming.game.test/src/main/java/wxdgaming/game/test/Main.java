package wxdgaming.game.test;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.loader.ClassDirLoader;
import wxdgaming.boot2.core.loader.JavaCoderCompile;
import wxdgaming.boot2.core.reflect.ReflectContext;
import wxdgaming.boot2.core.threading.ExecutorUtilImpl;
import wxdgaming.boot2.starter.RunApplicationMain;
import wxdgaming.boot2.starter.RunApplicationSub;
import wxdgaming.boot2.starter.WxdApplication;
import wxdgaming.boot2.starter.batis.sql.pgsql.MysqlScan;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlScan;
import wxdgaming.boot2.starter.net.NetScan;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.client.SocketClient;
import wxdgaming.boot2.starter.net.module.inner.RpcService;
import wxdgaming.boot2.starter.scheduled.ScheduledScan;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Main {

    public static void main(String[] args) throws Exception {
        RunApplicationMain runApplication = WxdApplication.run(
                CoreScan.class,
                ScheduledScan.class,
                NetScan.class,
                PgsqlScan.class,
                MysqlScan.class,
                Main.class
        );

        /* script.jar */
        String classDir = "script.jar";
        ClassDirLoader classDirLoader;
        if (new File(classDir).exists()) {
            try {
                classDirLoader = new ClassDirLoader(classDir);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                new JavaCoderCompile().parentClassLoader(Main.class.getClassLoader())
                        .compilerJava("wxdgaming.game.test-script/src/main/java")
                        .outPutFile("target/bin", true);
                classDirLoader = new ClassDirLoader("target/bin");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        ReflectContext reflectContext = ReflectContext.Builder.of(classDirLoader, "wxdgaming.game.test.script").build();
        RunApplicationSub runApplicationSub = WxdApplication.createRunApplicationSub(reflectContext);
        runApplicationSub.executeMethodWithAnnotated(Init.class);
        log.info("热更新重载");

        runApplication.start();

        ExecutorUtilImpl.getInstance().getDefaultExecutor().scheduleAtFixedDelay(
                () -> {
                    RpcService rpcService = runApplication.getInstance(RpcService.class);
                    SocketClient client = runApplication.getInstance(SocketClient.class);
                    SocketSession socketSession = client.idleNullException();
                    rpcService.request(socketSession, "rpcIndex", RunResult.ok().fluentPut("a", "1"))
                            .subscribe((jsonObject) -> {
                                log.info("rpcIndex response {}", jsonObject);
                            }, throwable -> {
                                log.error("rpcIndex", throwable);
                            });
                    rpcService.request(socketSession, "rpcIndex2", RunResult.ok().fluentPut("a", "2"))
                            .subscribe((jsonObject) -> {
                                log.info("rpcIndex2 response {}", jsonObject);
                            }, throwable -> {
                                log.error("rpcIndex2", throwable);
                            });

                    rpcService.request(socketSession, "script/rpcIndex", RunResult.ok().fluentPut("a", "3"))
                            .subscribe((jsonObject) -> {
                                log.info("script/rpcIndex response {}", jsonObject);
                            }, throwable -> {
                                log.error("script/rpcIndex", throwable);
                            });
                    rpcService.request(socketSession, "script/rpcIndex2", RunResult.ok().fluentPut("a", "4"))
                            .subscribe((jsonObject) -> {
                                log.info("script/rpcIndex2 response {}", jsonObject);
                            }, throwable -> {
                                log.error("script/rpcIndex2", throwable);
                            });

                    socketSession.write("我是文本消息");
                },
                5,
                3,
                TimeUnit.SECONDS
        );

    }

}