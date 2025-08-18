package wxdgaming.game.server;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.loader.ClassDirLoader;
import wxdgaming.boot2.core.loader.JavaCoderCompile;
import wxdgaming.boot2.core.reflect.ReflectProvider;
import wxdgaming.boot2.core.util.JvmUtil;
import wxdgaming.boot2.starter.RunApplicationMain;
import wxdgaming.boot2.starter.RunApplicationSub;
import wxdgaming.boot2.starter.WxdApplication;
import wxdgaming.boot2.starter.batis.sql.pgsql.MysqlScan;
import wxdgaming.boot2.starter.excel.DataExcelScan;
import wxdgaming.boot2.starter.excel.store.DataRepository;
import wxdgaming.boot2.starter.net.SocketScan;
import wxdgaming.boot2.starter.scheduled.ScheduledScan;
import wxdgaming.game.cfg.QPlayerTable;
import wxdgaming.game.cfg.bean.QPlayer;
import wxdgaming.game.basic.login.LoginProperties;
import wxdgaming.game.basic.slog.SlogService;
import wxdgaming.logbus.LogBusService;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Slf4j
public class GameServerApplication {

    static RunApplicationSub runApplicationSub = null;

    public static void main(String[] args) throws Exception {
        try {
            RunApplicationMain runApplication = WxdApplication.run(
                    CoreScan.class,
                    DataExcelScan.class,
                    ScheduledScan.class,
                    SocketScan.class,
                    MysqlScan.class,
                    LogBusService.class,
                    SlogService.class,
                    LoginProperties.class,
                    GameServerApplication.class
            );
            loadScript();
            runApplication.start();
            JvmUtil.addShutdownHook(() -> {
                runApplicationSub.stop();
            });

            QPlayerTable qPlayerTable = DataRepository.getIns().dataTable(QPlayerTable.class);
            QPlayer qPlayer = qPlayerTable.get(1);
            log.info("{}", qPlayer);

            ExecutorFactory.getExecutorServiceBasic().schedule(
                    () -> {
                        // RpcService rpcService = runApplication.getInstance(RpcService.class);
                        // SocketClient client = runApplication.getInstance(SocketClient.class);
                        // SocketSession socketSession = client.idleNullException();
                        // for (int k = 0; k < 5; k++) {
                        //     DiffTime diffTime = new DiffTime();
                        //     int rcount = 10_0000;
                        //     CountDownLatch countDownLatch = new CountDownLatch(rcount);
                        //     for (int i = 0; i < rcount; i++) {
                        //         Mono<JSONObject> request = rpcService.request(socketSession, "rpcIndex", MapOf.newJSONObject().fluentPut("a", "1"));
                        //         request
                        //                 .subscribe((jsonObject) -> {
                        //                     countDownLatch.countDown();
                        //                     log.debug("rpcIndex response {}", jsonObject);
                        //                 }, throwable -> {
                        //                     countDownLatch.countDown();
                        //                     log.error("rpcIndex", throwable);
                        //                 });
                        //     }
                        //     try {
                        //         countDownLatch.await();
                        //     } catch (InterruptedException e) {
                        //         throw new RuntimeException(e);
                        //     }
                        //     float diff = diffTime.diffMs5();
                        //     log.info("总耗时:{}ms", diff);
                        // }
                        // rpcService.request(socketSession, "rpcIndex2", MapOf.newJSONObject().fluentPut("a", "2"))
                        //         .subscribe((jsonObject) -> {
                        //             log.info("rpcIndex2 response {}", jsonObject);
                        //         }, throwable -> {
                        //             log.error("rpcIndex2", throwable);
                        //         });

                        // rpcService.request(socketSession, "script/rpcIndex", MapOf.newJSONObject().fluentPut("a", "3"))
                        //         .subscribe((jsonObject) -> {
                        //             log.info("script/rpcIndex response {}", jsonObject);
                        //         }, throwable -> {
                        //             log.error("script/rpcIndex", throwable);
                        //         });
                        //
                        // JSONObject block = rpcService.request(socketSession, "script/rpcIndex2", MapOf.newJSONObject().fluentPut("a", "4")).block();
                        // log.info("script/rpcIndex2 同步回调结果 {}", block);
                        //
                        // socketSession.write("我是文本消息");
                        // Thread.ofPlatform().start(()-> System.exit(0));
                    },
                    10,
                    TimeUnit.SECONDS
            );

        } catch (Throwable throwable) {
            log.error("启动失败", throwable);
            System.exit(99);
        }
    }

    public static void loadScript() {

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
                new JavaCoderCompile().parentClassLoader(GameServerApplication.class.getClassLoader())
                        .compilerJava("wxdgaming.game.server-script/src/main/java")
                        .outPutFile("target/bin", true);
                classDirLoader = new ClassDirLoader("target/bin");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        ReflectProvider reflectProvider = ReflectProvider.Builder.of(classDirLoader, "wxdgaming.game.server.script").build();

        runApplicationSub = WxdApplication.createRunApplicationSub(reflectProvider);
        runApplicationSub.executeMethodWithAnnotated(Init.class);
        log.info("加载脚本模块完成");
    }

}