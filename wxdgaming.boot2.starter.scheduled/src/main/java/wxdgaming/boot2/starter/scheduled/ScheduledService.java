package wxdgaming.boot2.starter.scheduled;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.event.InitEvent;
import wxdgaming.boot2.core.event.StartEvent;
import wxdgaming.boot2.core.event.StopBeforeEvent;
import wxdgaming.boot2.core.executor.ExecutorEvent;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.executor.ExecutorServicePlatform;
import wxdgaming.boot2.core.runtime.IgnoreRunTimeRecord;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.scheduled.ann.Scheduled;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务调度器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2021-09-24 18:21
 **/
@Slf4j
@Getter
@Service
public class ScheduledService extends HoldApplicationContext {

    protected ScheduledFuture<?> future;
    /*                          类名字                  方法名    实例 */
    protected List<AbstractCronTrigger> jobList = new ArrayList<>();

    protected ExecutorServicePlatform executorServicePlatform;

    public ScheduledService(ExecutorFactory executorFactory, ScheduledConfiguration executorProperties) {
        executorServicePlatform = ExecutorFactory.create("scheduled-executor", executorProperties.getExecutor());
    }

    @EventListener
    public void init(InitEvent initEvent) {
        log.debug("------------------------------初始化定时任务调度器------------------------------");
        List<AbstractCronTrigger> tmpJobList = new ArrayList<>();
        applicationContextProvider.withMethodAnnotatedCache(Scheduled.class)
                .forEach(providerMethod -> {
                    ScheduledInfo scheduledInfo = new ScheduledInfo(
                            providerMethod,
                            providerMethod.getMethod().getAnnotation(Scheduled.class)
                    );
                    log.debug("Scheduled job {}", providerMethod.getMethod());
                    tmpJobList.add(scheduledInfo);
                });
        sort(tmpJobList);
        jobList = tmpJobList;
    }

    public void addJob(AbstractCronTrigger abstractCronTrigger) {
        List<AbstractCronTrigger> tmpJobList = new ArrayList<>(jobList);
        tmpJobList.add(abstractCronTrigger);
        sort(tmpJobList);
        jobList = tmpJobList;
    }

    @EventListener
    @Order(99999998)
    @IgnoreRunTimeRecord
    public void start(StartEvent event) {
        ScheduleTrigger scheduleTrigger = new ScheduleTrigger();
        future = executorServicePlatform.scheduleAtFixedRate(
                scheduleTrigger,
                10,
                10,
                TimeUnit.MILLISECONDS
        );
    }

    @Order(-1)
    @EventListener
    public void stopBefore(StopBeforeEvent event) {
        log.info("线程 Scheduled 调度器 退出");
        if (future != null) {
            future.cancel(true);
            future = null;
        }
    }

    public void sort(List<AbstractCronTrigger> jobs) {
        jobs.sort(Comparator.comparingLong(AbstractCronTrigger::getNextRunTime));
    }


    /** 触发器 */
    protected class ScheduleTrigger extends ExecutorEvent {

        public ScheduleTrigger() {
            super();
        }

        @Override public String getQueueName() {
            return "scheduled-timer";
        }

        @Override public boolean isIgnoreRunTimeRecord() {
            return true;
        }

        @Override public String getStack() {
            return this.getQueueName();
        }

        int curSecond = -1;

        @Override public void onEvent() {
            long millis = MyClock.millis();
            int second = MyClock.getSecond(millis);
            if (curSecond == second) {
                return;
            }
            curSecond = second;
            boolean needSort = false;
            for (AbstractCronTrigger cronTrigger : jobList) {
                if (!cronTrigger.checkRunTime(millis)) {
                    break;
                }
                if (runJob(cronTrigger, millis)) {
                    needSort = true;
                }
            }
            if (needSort) {
                sort(jobList);
            }
        }

        public boolean runJob(AbstractCronTrigger scheduledInfo, long millis) {
            scheduledInfo.monitor.lock();
            try {
                if (!scheduledInfo.scheduleAtFixedRate() && !scheduledInfo.runEnd.get())
                    return false;
                /*标记为正在执行*/
                scheduledInfo.runEnd.set(false);
                scheduledInfo.nextRunTime = scheduledInfo.getCronExpress().validateTimeAfterMillis();
            } finally {
                scheduledInfo.monitor.unlock();
            }

            if (scheduledInfo.isAsync()) {
                /*异步执行*/
                scheduledInfo.submit();
            } else {
                /*同步执行*/
                scheduledInfo.run();
            }
            return true;
        }

    }
}
