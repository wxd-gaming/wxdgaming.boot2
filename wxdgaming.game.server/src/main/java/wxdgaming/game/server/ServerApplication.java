package wxdgaming.game.server;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.loader.ClassDirLoader;
import wxdgaming.boot2.core.loader.JavaCoderCompile;
import wxdgaming.boot2.core.reflect.ReflectContext;
import wxdgaming.boot2.starter.RunApplicationMain;
import wxdgaming.boot2.starter.RunApplicationSub;
import wxdgaming.boot2.starter.WxdApplication;
import wxdgaming.boot2.starter.batis.sql.pgsql.MysqlScan;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlScan;
import wxdgaming.boot2.starter.excel.DataExcelScan;
import wxdgaming.boot2.starter.excel.store.DataRepository;
import wxdgaming.boot2.starter.net.SocketScan;
import wxdgaming.boot2.starter.scheduled.ScheduledScan;
import wxdgaming.game.cfg.QPlayerTable;
import wxdgaming.game.cfg.bean.QPlayer;
import wxdgaming.game.login.LoginServiceGuiceModule;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ServerApplication {

    public static void main(String[] args) throws Exception {
        try {
            RunApplicationMain runApplication = WxdApplication.run(
                    CoreScan.class,
                    DataExcelScan.class,
                    ScheduledScan.class,
                    SocketScan.class,
                    PgsqlScan.class,
                    MysqlScan.class,
                    LoginServiceGuiceModule.class,
                    ServerApplication.class
            );
            loadScript();
            runApplication.start();
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
                new JavaCoderCompile().parentClassLoader(ServerApplication.class.getClassLoader())
                        .compilerJava("wxdgaming.game.server-script/src/main/java")
                        .outPutFile("target/bin", true);
                classDirLoader = new ClassDirLoader("target/bin");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        ReflectContext reflectContext = ReflectContext.Builder.of(classDirLoader, "wxdgaming.game.server.script").build();
        RunApplicationSub runApplicationSub = WxdApplication.createRunApplicationSub(reflectContext);
        runApplicationSub.executeMethodWithAnnotated(Init.class);
        log.info("加载脚本模块完成");
    }

}