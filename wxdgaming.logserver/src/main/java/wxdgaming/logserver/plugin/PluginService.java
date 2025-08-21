package wxdgaming.logserver.plugin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.starter.scheduled.ScheduledService;

/**
 * 插件服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-18 17:17
 **/
@Slf4j
@Service
public class PluginService extends HoldApplicationContext {

    final ScheduledService scheduledService;

    public PluginService(ScheduledService scheduledService) {
        this.scheduledService = scheduledService;
    }

    @Start
    public void start() {
        applicationContextProvider.classWithSuper(AbstractPlugin.class)
                .forEach(abstractPlugin -> {
                    log.info("插件: {} 加载成功", abstractPlugin.getClass().getName());
                    PluginExecutor pluginExecutor = new PluginExecutor(this::getApplicationContextProvider, abstractPlugin);
                    this.scheduledService.addJob(pluginExecutor);
                });
    }

}
