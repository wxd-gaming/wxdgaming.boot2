package wxdgaming.boot2.starter;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.core.GuiceModuleBase;
import wxdgaming.boot2.core.ServiceGuiceModule;
import wxdgaming.boot2.core.collection.SetOf;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.reflect.ReflectProvider;
import wxdgaming.boot2.core.util.DumpUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 启动器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-13 11:00
 **/
@Slf4j
public class WxdApplication {

    static final AtomicBoolean isRunning = new AtomicBoolean();

    @Getter static RunApplicationMain runApplicationMain;

    public static RunApplicationMain run(Class<?>... classes) {
        if (isRunning.get()) {
            throw new RuntimeException("boot2-starter is running");
        }
        try {
            log.info("boot2-starter starting");
            isRunning.set(true);
            BootConfig.getIns().loadConfig();

            String[] packages = Arrays.stream(classes).map(Class::getPackageName).toArray(String[]::new);

            /*组合把必要的启动器加入*/
            Set<String> packageSet = SetOf.asSet(CoreScan.class.getPackageName());
            packageSet.addAll(Arrays.asList(packages));

            final String[] finalPackages = packageSet.toArray(new String[0]);

            ReflectProvider reflectProvider = ReflectProvider.Builder.of(finalPackages).build();

            Stream<Class<? extends GuiceModuleBase>> moduleStream = Stream.empty();

            moduleStream = Stream.concat(moduleStream, reflectProvider.classWithSuper(ServiceGuiceModule.class));

            List<GuiceModuleBase> collect = moduleStream
                    .map(cls -> ReflectProvider.newInstance(cls, reflectProvider))
                    .collect(Collectors.toList());
            collect.add(0, new ConfigurationGuiceModule(reflectProvider, true));
            collect.add(1, new ApplicationGuiceModule(reflectProvider));
            collect.add(new SingletonGuiceModule(reflectProvider));

            Injector injector = Guice.createInjector(Stage.PRODUCTION, collect);
            runApplicationMain = injector.getInstance(RunApplicationMain.class);
            runApplicationMain.init();
            ExecutorFactory.getExecutorServiceLogic().scheduleAtFixedRate(() -> {
                StringBuilder stringAppend = new StringBuilder(1024);
                DumpUtil.freeMemory(stringAppend);
                log.info(stringAppend.toString());
            }, 120, 30, TimeUnit.SECONDS);
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

    public static RunApplicationSub createRunApplicationSub(ReflectProvider reflectProvider) {
        List<GuiceModuleBase> collect = List.of(
                new ConfigurationGuiceModule(reflectProvider, false),
                new SingletonGuiceModule(reflectProvider, RunApplicationSub.class)
        );

        Injector injector = runApplicationMain.getInjector().createChildInjector(collect);
        RunApplicationSub runApplicationSub = injector.getInstance(RunApplicationSub.class);
        runApplicationSub.init();
        return runApplicationSub;
    }

}
