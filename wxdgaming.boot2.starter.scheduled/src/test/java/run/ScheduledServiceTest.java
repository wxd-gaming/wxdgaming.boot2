package run;

import wxdgaming.boot2.core.event.StartEvent;
import wxdgaming.boot2.core.executor.CronExpressionUtil;
import wxdgaming.boot2.starter.scheduled.AbstractCronMethodTrigger;
import wxdgaming.boot2.starter.scheduled.ScheduledConfiguration;
import wxdgaming.boot2.starter.scheduled.ScheduledService;

public class ScheduledServiceTest {

    public static void main(String[] args) {
        ScheduledService scheduledService = new ScheduledService(new ScheduledConfiguration());
        scheduledService.addJob(new AbstractCronMethodTrigger(null, CronExpressionUtil.parse("*/5 * * * * ?")) {

            @Override public boolean isAsync() {
                return true;
            }

            @Override public void onEvent() throws Exception {
                System.out.println(1);
            }
        });
        scheduledService.start(new StartEvent());
    }

}
