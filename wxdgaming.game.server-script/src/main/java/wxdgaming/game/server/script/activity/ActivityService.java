package wxdgaming.game.server.script.activity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.event.InitEvent;
import wxdgaming.boot2.core.executor.HeartConst;
import wxdgaming.boot2.core.executor.HeartDriveHandler;
import wxdgaming.boot2.core.format.TableFormatter;
import wxdgaming.boot2.core.function.FunctionUtil;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.condition.Condition;
import wxdgaming.boot2.starter.condition.ConditionService;
import wxdgaming.boot2.starter.date.DateExpression;
import wxdgaming.boot2.starter.date.DateService;
import wxdgaming.boot2.starter.excel.store.DataRepository;
import wxdgaming.game.cfg.QActivityTable;
import wxdgaming.game.cfg.bean.QActivity;
import wxdgaming.game.common.global.GlobalDataService;
import wxdgaming.game.server.bean.activity.ActivityData;
import wxdgaming.game.server.bean.global.GlobalDataConst;
import wxdgaming.game.server.bean.global.impl.ServerActivityData;
import wxdgaming.game.server.module.system.GameService;

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
    final ConditionService conditionService;
    final DateService dateService;
    Map<Integer, AbstractActivityHandler> activityHandlerMap;
    Map<HeartConst, List<ActivityData>> heartHandlerMap = Map.of();

    public ActivityService(GameService gameService, GlobalDataService globalDataService, DataRepository dataRepository, ConditionService conditionService, DateService dateService) {
        this.gameService = gameService;
        this.globalDataService = globalDataService;
        this.dataRepository = dataRepository;
        this.conditionService = conditionService;
        this.dateService = dateService;
    }

    @EventListener
    @Order(Integer.MAX_VALUE)
    public void init(InitEvent initEvent) {
        activityHandlerMap = getApplicationContextProvider().toMap(AbstractActivityHandler.class, AbstractActivityHandler::activityType);
        gameService.getActivityThreadDrive().setDriveHandler(this);
        check();
        printAllActivity();
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
        Map<Integer, Integer> activityType2MaxIdMap = qActivityTable.getActivityType2MaxIdMap();
        boolean changed = false;
        for (Map.Entry<Integer, Map<Integer, QActivity>> mapEntry : activityType2IdCfgMap.entrySet()) {
            Integer type = mapEntry.getKey();
            @SuppressWarnings("unchecked")
            AbstractActivityHandler<ActivityData> abstractActivityHandler = activityHandlerMap.get(type);
            if (abstractActivityHandler == null) {
                log.warn("{}", "活动处理器不存在：activityType=%5s".formatted(type));
                continue;
            }
            /*当前id*/
            int selfActivityId = activityType2IdMap.getOrDefault(type, 0);
            Integer maxId = activityType2MaxIdMap.get(type);
            /*活动是否循环*/
            boolean poll = false;
            ActivityData selfActivityData = activityDataMap.get(type);
            if (selfActivityData != null) {
                boolean over = selfActivityData.getEndTime() < nowMillis;
                QActivity qActivity = qActivityTable.getByKey(selfActivityData.getActivityId());
                if (qActivity != null) {
                    poll = qActivity.getPoll() == 1;
                    DateExpression expression = dateService.convertBeginAndEnd(null, qActivity.getOpenTime());
                    /* 查找一个可以的时间，会先向过去时间查询，如果时间戳符合范围条件，如果不合法会向未来时间查询 */
                    if (expression == null || !expression.valid()) {
                        /*到这里说明策划该配置，当前活动需要关闭*/
                        over = true;
                    }
                } else {
                    log.warn("{}", "活动配置不存在：activityId=%5s".formatted(selfActivityData.getActivityId()));
                    over = true;
                }

                if (!over) {
                    /*TODO 当前活动未结束，跳过*/
                    continue;
                }

                if (selfActivityData.getEndTime() > 0) {
                    String formatted = "活动结束：activityType=%5s, activityId=%10s, activityName=%15s, startTime=%s, endTime=%s"
                            .formatted(
                                    selfActivityData.getActivityType(), selfActivityData.getActivityId(),
                                    FunctionUtil.nullDefaultValue(qActivity, QActivity::getName, "活动已删除"),
                                    MyClock.formatDate(selfActivityData.getStartTime()),
                                    MyClock.formatDate(selfActivityData.getEndTime())
                            );
                    log.info(formatted);
                    try {
                        abstractActivityHandler.end(selfActivityData);
                    } catch (Exception e) {
                        log.error(formatted, e);
                    } finally {
                        selfActivityData.clear();
                        changed = true;
                    }
                }
            }

            if (selfActivityId >= maxId && poll) {
                log.info("{}", "活动自动循环：activityType=%5s, activityId=%10s".formatted(type, selfActivityId));
                selfActivityId = 0;
            }

            for (Map.Entry<Integer, QActivity> integerQActivityEntry : mapEntry.getValue().entrySet()) {
                QActivity qActivity = integerQActivityEntry.getValue();
                if (qActivity.getId() <= selfActivityId) {
                    /*TODO 活动滚动下一轮，所以id是递增的*/
                    continue;
                }
                /* 查找一个可以的时间，会先向过去时间查询，如果时间戳符合范围条件，如果不合法会向未来时间查询 */
                DateExpression expression = dateService.convertBeginAndEnd(null, qActivity.getOpenTime());
                if (expression == null || expression.end() <= nowMillis) {
                    /*说明这个活动永久性过期*/
                    continue;
                }
                if (expression.valid()) {
                    List<Condition> conditions = conditionService.parse(qActivity.getValidation().getValue());
                    if (conditionService.testAll(null, conditions, (msg) -> {})) {
                        if (selfActivityData == null) {
                            selfActivityData = abstractActivityHandler.newData();
                        }
                        selfActivityData.setActivityId(qActivity.getId());
                        selfActivityData.setActivityType(qActivity.getType());
                        selfActivityData.setStartTime(expression.start());
                        selfActivityData.setEndTime(expression.end());
                        String formatted = "活动开启：activityType=%5s, activityId=%10s, activityName=%15s, startTime=%s, endTime=%s"
                                .formatted(
                                        selfActivityData.getActivityType(), selfActivityData.getActivityId(), qActivity.getName(),
                                        MyClock.formatDate(selfActivityData.getStartTime()),
                                        MyClock.formatDate(selfActivityData.getEndTime())
                                );
                        log.info(formatted);
                        try {
                            abstractActivityHandler.start(selfActivityData);
                            activityDataMap.put(qActivity.getType(), selfActivityData);
                            changed = true;
                        } catch (Exception e) {
                            log.error("活动开启异常：{}", formatted, e);
                        }
                    }
                } else {
                    log.debug("活动时间未开始：{}({}), {}", qActivity.getId(), qActivity.getName(), expression.fmt());
                }
                break;
            }
        }
        if (changed || heartHandlerMap == null || heartHandlerMap.isEmpty()) {
            Map<HeartConst, List<ActivityData>> tmp = new HashMap<>();
            for (ActivityData activityData : activityDataMap.values()) {
                @SuppressWarnings("unchecked")
                Collection<HeartConst> heartConsts = activityHandlerMap.get(activityData.getActivityType()).heartConst();
                for (HeartConst heartConst : heartConsts) {
                    List<ActivityData> handlers = tmp.computeIfAbsent(heartConst, l -> new ArrayList<>());
                    handlers.add(activityData);
                }
            }
            heartHandlerMap = tmp;
        }
    }

    public void printAllActivity() {
        QActivityTable qActivityTable = dataRepository.dataTable(QActivityTable.class);
        ServerActivityData serverActivityData = globalDataService.get(GlobalDataConst.ActivityData);
        ConcurrentHashMap<Integer, ActivityData> activityDataMap = serverActivityData.getActivityDataMap();
        TableFormatter tableFormatter = new TableFormatter();
        tableFormatter.addRow("activityType", "activityId", "activityName", "startTime", "endTime");
        for (ActivityData activityData : activityDataMap.values()) {
            QActivity qActivity = qActivityTable.getByKey(activityData.getActivityId());
            tableFormatter.addRow(
                    qActivity.getType(),
                    qActivity.getId(), qActivity.getName(),
                    MyClock.formatDate(activityData.getStartTime()),
                    MyClock.formatDate(activityData.getEndTime())
            );
        }
        String s = tableFormatter.generateTable();
        log.info("\n{}", s);
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
        check();
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

    @SuppressWarnings("unchecked")
    @Override public void heartWeek(long weekFirstDayStartTime) {
        List<ActivityData> activityDataList = heartHandlerMap.getOrDefault(HeartConst.Week, Collections.emptyList());
        for (ActivityData activityData : activityDataList) {
            AbstractActivityHandler<ActivityData> abstractActivityHandler = activityHandlerMap.get(activityData.getActivityType());
            abstractActivityHandler.heartWeek(activityData);
        }
    }
}
