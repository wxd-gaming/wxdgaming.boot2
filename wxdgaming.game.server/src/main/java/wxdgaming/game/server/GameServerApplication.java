package wxdgaming.game.server;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import reactor.core.publisher.Mono;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.core.MainApplicationContextProvider;
import wxdgaming.boot2.core.SpringUtil;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.lang.DiffTimeRecord;
import wxdgaming.boot2.core.loader.ClassDirLoader;
import wxdgaming.boot2.core.loader.JavaCoderCompile;
import wxdgaming.boot2.core.util.JvmUtil;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlScan;
import wxdgaming.boot2.starter.excel.DataExcelScan;
import wxdgaming.boot2.starter.excel.store.DataRepository;
import wxdgaming.boot2.starter.net.SocketScan;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.client.SocketClient;
import wxdgaming.boot2.starter.net.module.rpc.RpcService;
import wxdgaming.boot2.starter.scheduled.ScheduledScan;
import wxdgaming.boot2.util.ChildApplicationContextProvider;
import wxdgaming.game.basic.login.LoginProperties;
import wxdgaming.game.basic.slog.SlogService;
import wxdgaming.game.cfg.QPlayerTable;
import wxdgaming.game.cfg.bean.QPlayer;
import wxdgaming.logbus.LogBusService;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootApplication(scanBasePackageClasses = {
        CoreScan.class,
        DataExcelScan.class,
        ScheduledScan.class,
        SocketScan.class,
        PgsqlScan.class,
        LogBusService.class,
        SlogService.class,
        LoginProperties.class,
        GameServerApplication.class
})
public class GameServerApplication {

    public static void main(String[] args) throws Exception {
        try {
            ConfigurableApplicationContext applicationContext = MainApplicationContextProvider.builder(GameServerApplication.class)
                    .run(args);
            loadScript();
            SpringUtil.mainApplicationContextProvider.startBootstrap();
            JvmUtil.addShutdownHook(() -> {
                SpringUtil.childApplicationContextProvider.executeMethodWithAnnotatedStop();
            });

            QPlayerTable qPlayerTable = DataRepository.getIns().dataTable(QPlayerTable.class);
            QPlayer qPlayer = qPlayerTable.get(1);
            log.info("{}", qPlayer);

            ExecutorFactory.getExecutorServiceBasic().schedule(
                    () -> {
                        RpcService rpcService = applicationContext.getBean(RpcService.class);
                        SocketClient client = applicationContext.getBean(SocketClient.class);
                        SocketSession socketSession = client.idleNullException();
                        for (int k = 0; k < 5; k++) {
                            DiffTimeRecord diffTime = new DiffTimeRecord();
                            int rcount = 1;
                            CountDownLatch countDownLatch = new CountDownLatch(rcount);
                            for (int i = 0; i < rcount; i++) {
                                Mono<JSONObject> request = rpcService.request(socketSession, "rpcIndex", MapOf.newJSONObject().fluentPut("a", "1"));
                                request
                                        .subscribe((jsonObject) -> {
                                            countDownLatch.countDown();
                                            log.debug("rpcIndex response {}", jsonObject);
                                        }, throwable -> {
                                            countDownLatch.countDown();
                                            log.error("rpcIndex", throwable);
                                        });
                            }
                            try {
                                countDownLatch.await();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            float diff = diffTime.interval().interval4();
                            log.info("总耗时:{}ms", diff);
                        }
                        rpcService.request(socketSession, "rpcIndex2", MapOf.newJSONObject().fluentPut("a", "2"))
                                .subscribe((jsonObject) -> {
                                    log.info("rpcIndex2 response {}", jsonObject);
                                }, throwable -> {
                                    log.error("rpcIndex2", throwable);
                                });

                        rpcService.request(socketSession, "script/rpcIndex", MapOf.newJSONObject().fluentPut("a", "3"))
                                .subscribe((jsonObject) -> {
                                    log.info("script/rpcIndex response {}", jsonObject);
                                }, throwable -> {
                                    log.error("script/rpcIndex", throwable);
                                });

                        JSONObject block = rpcService.request(socketSession, "script/rpcIndex2", MapOf.newJSONObject().fluentPut("a", "4")).block();
                        log.info("script/rpcIndex2 同步回调结果 {}", block);

                        socketSession.write("我是文本消息");
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

        ChildApplicationContextProvider childApplicationContextProvider = SpringUtil.newChild((ConfigurableApplicationContext) SpringUtil.mainApplicationContextProvider.getApplicationContext(), ScriptScan.class, classDirLoader);
        childApplicationContextProvider.executeMethodWithAnnotatedInit();
        SpringUtil.newChildAfter(
                (ConfigurableApplicationContext) SpringUtil.mainApplicationContextProvider.getApplicationContext(),
                (ConfigurableApplicationContext) childApplicationContextProvider.getApplicationContext(),
                ScriptScan.class,
                classDirLoader
        );
        if (SpringUtil.childApplicationContextProvider != null) {
            ((AnnotationConfigServletWebApplicationContext) SpringUtil.childApplicationContextProvider.getApplicationContext()).close();
        }
        SpringUtil.childApplicationContextProvider = childApplicationContextProvider;
        log.info("加载脚本模块完成");
    }

    @ComponentScan("wxdgaming.game.server.script")
    public static class ScriptScan {}

}