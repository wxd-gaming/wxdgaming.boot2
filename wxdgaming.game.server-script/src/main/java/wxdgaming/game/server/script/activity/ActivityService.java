package wxdgaming.game.server.script.activity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.executor.HeartDriveHandler;
import wxdgaming.boot2.core.timer.CronDuration;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.excel.store.DataRepository;
import wxdgaming.game.cfg.QActivityTable;
import wxdgaming.game.cfg.bean.QActivity;
import wxdgaming.game.common.global.GlobalDataService;
import wxdgaming.game.server.bean.activity.ActivityData;
import wxdgaming.game.server.bean.activity.HeartConst;
import wxdgaming.game.server.bean.global.GlobalDataConst;
import wxdgaming.game.server.bean.global.impl.ServerActivityData;
import wxdgaming.game.server.module.system.GameService;
import wxdgaming.game.server.script.validation.ValidationService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 活动
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-11 17:13
 **/
@Slf4j
@Service
public class ActivityService extends HoldApplicationContext implements HeartDriveHandler {

    final GameService gameService;
    final GlobalDataService globalDataService;
    final DataRepository dataRepository;
    final ValidationService validationService;
    Map<Integer, AbstractActivityHandler> activityHandlerMap;
    Map<HeartConst, List<ActivityData>> heartHandlerMap;

    public ActivityService(GameService gameService, GlobalDataService globalDataService, DataRepository dataRepository, ValidationService validationService) {
        this.gameService = gameService;
        this.globalDataService = globalDataService;
        this.dataRepository = dataRepository;
        this.validationService = validationService;
    }

    @Init
    @Order(Integer.MAX_VALUE)
    public void init() {
        activityHandlerMap = getApplicationContextProvider().toMap(AbstractActivityHandler.class, AbstractActivityHandler::activityType);
        check();
        gameService.getActivityThreadDrive().setDriveHandler(this);
    }

    public void check() {
        ServerActivityData serverActivityData = globalDataService.get(GlobalDataConst.ActivityData);
        ConcurrentHashMap<Integer, ActivityData> activityDataMap = serverActivityData.getActivityDataMap();
        /*type类型和id的映射*/
        HashMap<Integer, Integer> activityType2IdMap = new HashMap<>();
        for (Map.Entry<Integer, ActivityData> entry : activityDataMap.entrySet()) {
            activityType2IdMap.put(entry.getValue().getActivityType(), entry.getValue().getActivityId());
        }
        /*上一轮活动已经结束，判断下一轮是否开*/
        long nowMillis = MyClock.millis();
        QActivityTable qActivityTable = dataRepository.dataTable(QActivityTable.class);
        Map<Integer, Map<Integer, QActivity>> activityType2IdCfgMap = qActivityTable.getActivityType2IdMap();
        for (Map.Entry<Integer, Map<Integer, QActivity>> mapEntry : activityType2IdCfgMap.entrySet()) {
            Integer type = mapEntry.getKey();
            /*当前id*/
            int selfActivityId = activityType2IdMap.getOrDefault(type, 0);
            ActivityData selfActivityData = activityDataMap.get(type);
            if (selfActivityData != null) {
                boolean over = selfActivityData.getEndTime() < nowMillis;
                if (!over) {
                    QActivity qActivity = qActivityTable.get(selfActivityData.getActivityId());
                    log.info("{}",
                            "活动继续：activityType=%5s, activityId=%10s, activityName=%15s, startTime=%s, endTime=%s"
                                    .formatted(
                                            selfActivityData.getActivityType(), selfActivityData.getActivityId(), qActivity.getName(),
                                            MyClock.formatDate(selfActivityData.getStartTime()),
                                            MyClock.formatDate(selfActivityData.getEndTime())
                                    )
                    );
                    /*TODO 当前活动未结束，跳过*/
                    continue;
                }
            }

            for (Map.Entry<Integer, QActivity> integerQActivityEntry : mapEntry.getValue().entrySet()) {
                QActivity qActivity = integerQActivityEntry.getValue();
                if (qActivity.getId() <= selfActivityId) {
                    /*TODO 活动滚动下一轮，所以id是递增的*/
                    continue;
                }
                if (validationService.validateAll(null, qActivity.getValidation(), false)) {
                    AbstractActivityHandler<ActivityData> abstractActivityHandler = activityHandlerMap.get(qActivity.getType());
                    if (abstractActivityHandler != null) {
                        ActivityData activityData = abstractActivityHandler.newData();
                        activityData.setActivityId(qActivity.getId());
                        activityData.setActivityType(qActivity.getType());
                        /* 查找一个可以的时间，会先向过去时间查询，如果时间戳符合范围条件，如果不合法会向未来时间查询 */
                        CronDuration cronDuration = qActivity.getOpenTime().findValidateTime();
                        if (cronDuration != null && cronDuration.valid(nowMillis)) {
                            long start = cronDuration.getStart();
                            long end = cronDuration.getEnd();
                            activityData.setStartTime(start);
                            activityData.setEndTime(end);
                            activityDataMap.put(qActivity.getType(), activityData);
                            abstractActivityHandler.start(activityData);
                            log.info("{}",
                                    "活动开启：activityType=%5s, activityId=%10s, activityName=%15s, startTime=%s, endTime=%s"
                                            .formatted(
                                                    activityData.getActivityType(), activityData.getActivityId(), qActivity.getName(),
                                                    MyClock.formatDate(activityData.getStartTime()),
                                                    MyClock.formatDate(activityData.getEndTime())
                                            )
                            );
                        }
                    }
                }
                break;
            }
        }
        Map<HeartConst, List<ActivityData>> tmp = new HashMap<>();
        for (ActivityData activityData : activityDataMap.values()) {
            Collection<HeartConst> heartConsts = activityHandlerMap.get(activityData.getActivityType()).heartConst();
            for (HeartConst heartConst : heartConsts) {
                List<ActivityData> handlers = tmp.computeIfAbsent(heartConst, l -> new ArrayList<>());
                handlers.add(activityData);
            }
        }
        heartHandlerMap = tmp;
    }

    @SuppressWarnings("unchecked")
    @Override public void heart(long millis) {
        List<ActivityData> activityDataList = heartHandlerMap.getOrDefault(HeartConst.Heart, Collections.emptyList());
        for (ActivityData activityData : activityDataList) {
            AbstractActivityHandler<ActivityData> abstractActivityHandler = activityHandlerMap.get(activityData.getActivityType());
            abstractActivityHandler.heart(activityData);
        }
    }

    @SuppressWarnings("unchecked")
    @Override public void heartSecond(int second) {
        List<ActivityData> activityDataList = heartHandlerMap.getOrDefault(HeartConst.Second, Collections.emptyList());
        for (ActivityData activityData : activityDataList) {
            AbstractActivityHandler<ActivityData> abstractActivityHandler = activityHandlerMap.get(activityData.getActivityType());
            abstractActivityHandler.heartSecond(activityData);
        }
    }

    @SuppressWarnings("unchecked")
    @Override public void heartMinute(int minute) {
        List<ActivityData> activityDataList = heartHandlerMap.getOrDefault(HeartConst.Minute, Collections.emptyList());
        for (ActivityData activityData : activityDataList) {
            AbstractActivityHandler<ActivityData> abstractActivityHandler = activityHandlerMap.get(activityData.getActivityType());
            abstractActivityHandler.heartMinute(activityData);
        }
    }

    @SuppressWarnings("unchecked")
    @Override public void heartHour(int hour) {
        List<ActivityData> activityDataList = heartHandlerMap.getOrDefault(HeartConst.Hour, Collections.emptyList());
        for (ActivityData activityData : activityDataList) {
            AbstractActivityHandler<ActivityData> abstractActivityHandler = activityHandlerMap.get(activityData.getActivityType());
            abstractActivityHandler.heartHour(activityData);
        }
    }

    @SuppressWarnings("unchecked")
    @Override public void heartDayEnd(int dayOfYear) {
        List<ActivityData> activityDataList = heartHandlerMap.getOrDefault(HeartConst.DayEnd, Collections.emptyList());
        for (ActivityData activityData : activityDataList) {
            AbstractActivityHandler<ActivityData> abstractActivityHandler = activityHandlerMap.get(activityData.getActivityType());
            abstractActivityHandler.heartDayEnd(activityData);
        }
    }

}
