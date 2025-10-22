package wxdgaming.game.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.core.MainApplicationContextProvider;
import wxdgaming.boot2.core.SpringUtil;
import wxdgaming.boot2.core.loader.ClassDirLoader;
import wxdgaming.boot2.core.loader.JavaCoderCompile;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlScan;
import wxdgaming.boot2.starter.excel.DataExcelScan;
import wxdgaming.boot2.starter.net.SocketScan;
import wxdgaming.boot2.starter.scheduled.ScheduledScan;
import wxdgaming.boot2.starter.validation.ValidationScan;
import wxdgaming.boot2.util.ChildApplicationContextProvider;
import wxdgaming.game.common.bean.login.ConnectLoginProperties;
import wxdgaming.game.common.global.GlobalDataService;
import wxdgaming.game.common.slog.SlogService;
import wxdgaming.logbus.LogBusService;

import java.io.File;

@Slf4j
@SpringBootApplication(scanBasePackageClasses = {
        CoreScan.class,
        DataExcelScan.class,
        ScheduledScan.class,
        SocketScan.class,
        PgsqlScan.class,
        LogBusService.class,
        SlogService.class,
        GlobalDataService.class,
        ValidationScan.class,
        ConnectLoginProperties.class,
        GameServerApplication.class
})
public class GameServerApplication {

    public static void main(String[] args) throws Exception {
        try {
            MainApplicationContextProvider.builder(GameServerApplication.class)
                    .run(args);
            loadScript();
            SpringUtil.mainApplicationContextProvider.startBootstrap();
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
        childApplicationContextProvider.postInitEvent();
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