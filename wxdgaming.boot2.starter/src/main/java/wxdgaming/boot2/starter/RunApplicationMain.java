package wxdgaming.boot2.starter;

import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.ann.Shutdown;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.io.FileReadUtil;
import wxdgaming.boot2.core.util.GlobalUtil;
import wxdgaming.boot2.core.util.JvmUtil;

/**
 * 运行类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-14 16:55
 **/
@Slf4j
public final class RunApplicationMain extends RunApplication {

    @Inject
    public RunApplicationMain(Injector injector) {
        super(injector);
    }

    @Override public void init() {
        super.init();
        executeMethodWithAnnotated(Init.class);
    }

    public void start() {

        try {
            executeMethodWithAnnotated(Start.class);

            JvmUtil.addShutdownHook(() -> {
                System.out.println("--------------------------shutdown---------------------------");
                GlobalUtil.Exiting.set(true);
                executeMethodWithAnnotatedException(Shutdown.class);
                classWithSuper(AutoCloseable.class)
                        .forEach(closeable -> {
                            try {
                                closeable.close();
                            } catch (Exception e) {
                                log.error("关闭异常: {}", closeable.getClass(), e);
                            }
                        });
            });

            StringBuilder stringAppend = new StringBuilder(1024);

            String printString = FileReadUtil.readString("print.txt");

            int len = 60;

            stringAppend.append("\n\n")
                    .append(printString)
                    .append("\n")
                    .append("    -[ " + StringUtils.padRight("debug = " + BootConfig.getIns().isDebug() + " | " + JvmUtil.processIDString(), len, ' ') + " ]-\n")
                    .append("    -[ " + StringUtils.padRight(BootConfig.getIns().sid() + " | " + BootConfig.getIns().sname(), len, ' ') + " ]-\n")
                    .append("    -[ " + StringUtils.padRight(JvmUtil.timeZone(), len, ' ') + " ]-\n");
            stringAppend.append("\n");
            log.info("=== start end ===");
            //            log.warn(stringAppend.toString());
        } catch (Throwable e) {
            log.error("start error", e);
            System.exit(1);
        }
    }

}
