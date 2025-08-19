package wxdgaming.logserver.plugin;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.Start;
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
        runApplication.classWithSuper(AbstractPlugin.class)
                .forEach(abstractPlugin -> {
                    log.info("插件: {} 加载成功", abstractPlugin.getClass().getName());
                    PluginExecutor pluginExecutor = new PluginExecutor(this::getRunApplication, abstractPlugin);
                    this.scheduledService.addJob(pluginExecutor);
                });
    }

}
