package wxdgaming.boot2.starter;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.*;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.ann.shutdown;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.collection.SetOf;
import wxdgaming.boot2.core.io.FileReadUtil;
import wxdgaming.boot2.core.reflect.ReflectContext;
import wxdgaming.boot2.core.util.JvmUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 启动器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-13 11:00
 **/
@Slf4j
public class WxdApplication {

    static final AtomicBoolean isRunning = new AtomicBoolean();

    @Getter static RunApplicationMain runApplicationMain;

    public static RunApplication run(Class<?>... classes) {
        if (isRunning.get()) {
            throw new RuntimeException("boot2-starter is running");
        }
        try {
            log.info("boot2-starter starting");
            isRunning.set(true);
            BootConfig.getIns().loadConfig();

            String[] packages = Arrays.stream(classes).map(Class::getPackageName).toArray(String[]::new);

            /*组合把必要的启动器加入*/
            Set<String> packageSet = SetOf.asSet(WxdApplication.class.getPackageName(), CoreScan.class.getPackageName());
            packageSet.addAll(Arrays.asList(packages));

            final String[] finalPackages = packageSet.toArray(new String[0]);

            ReflectContext reflectContext = ReflectContext.Builder.of(finalPackages).build();

            Stream<Class<? extends BaseModule>> moduleStream = Stream.empty();

            moduleStream = Stream.concat(moduleStream, reflectContext.classWithSuper(ServiceModule.class));
            moduleStream = Stream.concat(moduleStream, reflectContext.classWithSuper(UserModule.class));

            List<BaseModule> collect = moduleStream
                    .map(cls -> ReflectContext.newInstance(cls, reflectContext))
                    .collect(Collectors.toList());
            collect.addFirst(new ApplicationModule(reflectContext));
            collect.add(new SingletonModule(reflectContext));

            Injector injector = Guice.createInjector(Stage.PRODUCTION, collect);
            runApplicationMain = injector.getInstance(RunApplicationMain.class);

            runApplicationMain.init();

            runApplicationMain.executeMethodWithAnnotated(Init.class);
            runApplicationMain.executeMethodWithAnnotated(Start.class);

            JvmUtil.addShutdownHook(() -> {
                System.out.println("--------------------------shutdown---------------------------");
                runApplicationMain.executeMethodWithAnnotated(shutdown.class);
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
            log.warn(stringAppend.toString());

            return runApplicationMain;
        } catch (Throwable throwable) {
            log.error("", throwable);
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
            Runtime.getRuntime().halt(99);
        }
        return null;
    }

    public static RunApplicationSub createRunApplicationSub(ReflectContext reflectContext) {
        List<BaseModule> collect = reflectContext.classWithSuper(UserModule.class)
                .map(cls -> ReflectContext.newInstance(cls, reflectContext))
                .collect(Collectors.toList());
        /* TODO 这里把子容器注入进去 */
        collect.add(new SingletonModule(reflectContext, RunApplicationSub.class));

        Injector injector = runApplicationMain.getInjector().createChildInjector(collect);
        RunApplicationSub runApplicationSub = injector.getInstance(RunApplicationSub.class);
        runApplicationSub.init();
        return runApplicationSub;
    }

}
