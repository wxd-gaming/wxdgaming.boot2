package wxdgaming.boot2.starter;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.*;
import wxdgaming.boot2.core.collection.SetOf;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.reflect.ReflectContext;
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
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-13 11:00
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
            Set<String> packageSet = SetOf.asSet(WxdApplication.class.getPackageName(), CoreScan.class.getPackageName());
            packageSet.addAll(Arrays.asList(packages));

            final String[] finalPackages = packageSet.toArray(new String[0]);

            ReflectContext reflectContext = ReflectContext.Builder.of(finalPackages).build();

            Stream<Class<? extends GuiceModuleBase>> moduleStream = Stream.empty();

            moduleStream = Stream.concat(moduleStream, reflectContext.classWithSuper(ServiceGuiceModule.class));
            moduleStream = Stream.concat(moduleStream, reflectContext.classWithSuper(UserGuiceModule.class));

            List<GuiceModuleBase> collect = moduleStream
                    .map(cls -> ReflectContext.newInstance(cls, reflectContext))
                    .collect(Collectors.toList());
            collect.addFirst(new ApplicationGuiceModule(reflectContext));
            collect.add(new SingletonGuiceModule(reflectContext));

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

    public static RunApplicationSub createRunApplicationSub(ReflectContext reflectContext) {
        List<GuiceModuleBase> collect = reflectContext.classWithSuper(UserGuiceModule.class)
                .map(cls -> ReflectContext.newInstance(cls, reflectContext))
                .collect(Collectors.toList());
        /* TODO 这里把子容器注入进去 */
        collect.add(new SingletonGuiceModule(reflectContext, RunApplicationSub.class));

        Injector injector = runApplicationMain.getInjector().createChildInjector(collect);
        RunApplicationSub runApplicationSub = injector.getInstance(RunApplicationSub.class);
        runApplicationSub.init();
        return runApplicationSub;
    }

}
