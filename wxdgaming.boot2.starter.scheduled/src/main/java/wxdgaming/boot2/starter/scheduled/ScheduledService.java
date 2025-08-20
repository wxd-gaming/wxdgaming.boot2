package wxdgaming.boot2.starter.scheduled;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.ann.Order;
import wxdgaming.boot2.core.ann.Stop;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.executor.ExecutorEvent;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.executor.ExecutorServicePlatform;
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
@Singleton
public class ScheduledService {

    protected ScheduledFuture<?> future;
    /*                          类名字                  方法名    实例 */
    protected List<AbstractCronTrigger> jobList = new ArrayList<>();

    protected ExecutorServicePlatform executorServicePlatform;

    @Inject
    public ScheduledService(ExecutorFactory executorFactory, ScheduledProperties executorProperties) {
        executorServicePlatform = ExecutorFactory.create("scheduled-executor", executorProperties.getExecutor());
    }

    @Init
    public void init(RunApplication runApplication) {
        log.debug("------------------------------初始化定时任务调度器------------------------------");
        List<AbstractCronTrigger> tmpJobList = new ArrayList<>();
        runApplication.getGuiceBeanProvider().withMethodAnnotated(Scheduled.class)
                .forEach(methodContent -> {
                    ScheduledInfo scheduledInfo = new ScheduledInfo(
                            methodContent.getBean(),
                            methodContent.getMethod(),
                            methodContent.getMethod().getAnnotation(Scheduled.class)
                    );
                    log.debug("Scheduled job {}", methodContent.getMethod());
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

    @Start
    @Order(99999998)
    public void start() {
        ScheduleTrigger scheduleTrigger = new ScheduleTrigger();
        future = executorServicePlatform.scheduleAtFixedRate(
                scheduleTrigger,
                10,
                10,
                TimeUnit.MILLISECONDS
        );
    }

    @Stop
    @Order(1000)
    public void stop() {
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

        @Override public String queueName() {
            return "scheduled-timer";
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
            scheduledInfo.lock.lock();
            try {
                if (!scheduledInfo.scheduleAtFixedRate() && !scheduledInfo.runEnd.get())
                    return false;
                /*标记为正在执行*/
                scheduledInfo.runEnd.set(false);
                scheduledInfo.nextRunTime = scheduledInfo.getCronExpress().validateTimeAfterMillis();
            } finally {
                scheduledInfo.lock.unlock();
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
