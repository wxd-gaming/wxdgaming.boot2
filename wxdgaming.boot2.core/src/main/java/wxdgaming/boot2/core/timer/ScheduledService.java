package wxdgaming.boot2.core.timer;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.threading.Event;
import wxdgaming.boot2.core.threading.ExecutorUtil;
import wxdgaming.boot2.core.threading.Job;
import wxdgaming.boot2.core.timer.ann.Scheduled;
import wxdgaming.boot2.core.util.JvmUtil;

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
@Singleton
public class ScheduledService extends Event {

    protected Job job;

    /*                          类名字                  方法名    实例 */
    protected List<ScheduledInfo> jobList = new ArrayList<>();

    public ScheduledService() {
        super("任务调度器", 33, 500);
    }

    @Init
    public void init(RunApplication runApplication) {
        log.debug("------------------------------初始化定时任务调度器------------------------------");
        List<ScheduledInfo> tmpJobList = new ArrayList<>(jobList);
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
        job = ExecutorUtil.getDefaultExecutor().scheduleAtFixedDelay(
                "scheduled-timer",
                this,
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
            if (scheduledInfo.runJob(millis)) {
                needSort = true;
            }
        }
        if (needSort) {
            sort(jobList);
        }
    }

}
