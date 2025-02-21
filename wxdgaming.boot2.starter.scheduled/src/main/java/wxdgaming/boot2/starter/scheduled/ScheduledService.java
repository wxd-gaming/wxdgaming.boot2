package wxdgaming.boot2.starter.scheduled;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.threading.Event;
import wxdgaming.boot2.core.threading.ExecutorUtil;
import wxdgaming.boot2.core.threading.IExecutorServices;
import wxdgaming.boot2.core.threading.Job;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.core.util.JvmUtil;
import wxdgaming.boot2.starter.scheduled.ann.Scheduled;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务调度器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2021-09-24 18:21
 **/
@Slf4j
@Getter
public class ScheduledService {

    protected Job job;
    /*                          类名字                  方法名    实例 */
    protected List<ScheduledInfo> jobList = new ArrayList<>();

    protected final ScheduledConfig config;
    protected IExecutorServices defaultExecutor;

    public ScheduledService(ScheduledConfig config) {
        defaultExecutor = ExecutorUtil.getInstance().newExecutorServices("scheduled-executor", config.getCoreSize(), config.getCoreSize());
        this.config = config;
    }

    @Init
    public void init(RunApplication runApplication) {
        log.debug("------------------------------初始化定时任务调度器------------------------------");
        List<ScheduledInfo> tmpJobList = new ArrayList<>();
        runApplication.getReflectContext().withMethodAnnotated(Scheduled.class)
                .forEach(methodContent -> {
                    ScheduledInfo scheduledInfo = new ScheduledInfo(
                            methodContent.getIns(),
                            methodContent.getMethod(),
                            methodContent.getMethod().getAnnotation(Scheduled.class)
                    );
                    log.debug("Scheduled job {}", methodContent.getMethod());
                    tmpJobList.add(scheduledInfo);
                });
        sort(tmpJobList);
        jobList = tmpJobList;
    }

    @Start
    public void start() {
        ScheduleTrigger scheduleTrigger = new ScheduleTrigger();
        job = ExecutorUtil.getInstance().getDefaultExecutor().scheduleAtFixedDelay(
                "scheduled-timer",
                scheduleTrigger,
                10,
                10,
                TimeUnit.MILLISECONDS
        );
        JvmUtil.addShutdownHook(this::close);
    }

    void close() {
        log.info("------------------------------关闭定时任务调度器------------------------------");
        if (job != null) {
            job.cancel();
            job = null;
        }
    }

    public void sort(List<ScheduledInfo> jobs) {
        jobs.sort(Comparator.comparingLong(ScheduledInfo::getNextRunTime));
    }


    /** 触发器 */
    protected class ScheduleTrigger extends Event {

        public ScheduleTrigger() {
            super("任务调度器", 33, 500);
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
            for (ScheduledInfo scheduledInfo : jobList) {
                if (!scheduledInfo.checkRunTime(millis)) {
                    break;
                }
                if (runJob(scheduledInfo, millis)) {
                    needSort = true;
                }
            }
            if (needSort) {
                sort(jobList);
            }
        }

        public boolean runJob(ScheduledInfo scheduledInfo, long millis) {
            scheduledInfo.lock.lock();
            try {
                if (!scheduledInfo.isScheduleAtFixedRate() && !scheduledInfo.runEnd.get())
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
                scheduledInfo.startExecTime.reset();
                defaultExecutor.submit(scheduledInfo);
                float v = scheduledInfo.startExecTime.diff();
                if (v > logTime) {
                    String msg = "执行：" + scheduledInfo.getName() + ", 耗时：" + v + " ms";
                    log.info(msg);
                }
            }
            return true;
        }

    }
}
