package wxdgaming.logserver.plugin;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.loader.ClassDirLoader;
import wxdgaming.boot2.core.loader.JavaCoderCompile;
import wxdgaming.boot2.core.reflect.ReflectProvider;
import wxdgaming.boot2.starter.scheduled.ScheduledService;

/**
 * 插件服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-18 17:17
 **/
@Slf4j
@Singleton
public class PluginService extends HoldRunApplication {

    final ScheduledService scheduledService;

    @Inject
    public PluginService(ScheduledService scheduledService) {
        this.scheduledService = scheduledService;
    }

    @Start
    public void start() {
        JavaCoderCompile javaCoderCompile = new JavaCoderCompile()
                .parentClassLoader(PluginService.class.getClassLoader())
                .compilerJava("wxdgaming.logserver/src/main/plugins");
        ClassDirLoader classDirLoader = javaCoderCompile.classLoader();
        ReflectProvider reflectProvider = ReflectProvider.Builder.of(classDirLoader, "plugin").build();
        reflectProvider.classWithSuper(AbstractPlugin.class).forEach(abstractPluginClass -> {
            AbstractPlugin abstractPlugin = ReflectProvider.newInstance(abstractPluginClass);
            log.info("插件: {} 加载成功", abstractPlugin.getClass().getName());
            PluginExecutor pluginExecutor = new PluginExecutor(this::getRunApplication, abstractPlugin);
            this.scheduledService.addJob(pluginExecutor);
        });
    }

}
