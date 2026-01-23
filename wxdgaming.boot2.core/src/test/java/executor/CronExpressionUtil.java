package executor;

import org.springframework.scheduling.support.CronExpression;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

/**
 * cron 表达式解析器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-23 10:54
 **/
public class CronExpressionUtil {

    /** 自动释放的缓存，不用太严谨，只是为了减少gc而已 */
    private static final WeakHashMap<String, CronExpression> CRON_EXPRESSION_CACHE = new WeakHashMap<>();

    public static synchronized CronExpression parse(String cronExpression) {
        return CRON_EXPRESSION_CACHE.computeIfAbsent(cronExpression, CronExpression::parse);
    }

    /**
     * 查找一个有效时间, 如果当前时间在上一次开启时间加上持续时间范围内，返回一个正确的时间
     *
     * @param cronString 表达式
     * @param duration   持续时间
     */
    public static LocalDateTime validityTime(String cronString, long duration) {
        LocalDateTime now = LocalDateTime.now();
        return validityTime(cronString, now, duration);
    }

    /**
     * 查找一个有效时间, 如果当前时间在上一次开启时间加上持续时间范围内，返回一个正确的时间
     *
     * @param cronString  表达式
     * @param targetStart 目标时间
     * @param duration    持续时间
     */
    public static LocalDateTime validityTime(String cronString, LocalDateTime targetStart, long duration) {
        LocalDateTime nowed = LocalDateTime.now();
        LocalDateTime startTime = up(cronString, targetStart);
        if (startTime != null) {
            /*TODO 优先查找上一次可用的时间，加上持续时间如果在有效范围 */
            LocalDateTime endTime = startTime.plus(duration, ChronoUnit.MILLIS);
            // 判断当前时间是否在活动时间范围内
            if (startTime.isBefore(nowed) && endTime.isAfter(nowed)) {
                return nowed;
            }
        }
        return next(cronString, targetStart);
    }

    public static LocalDateTime next(String cronString, LocalDateTime now) {
        CronExpression cronExpression = parse(cronString);
        return cronExpression.next(now);
    }

    public static LocalDateTime up(String cronString, LocalDateTime now) {
        long daysSeconds = TimeUnit.DAYS.toSeconds(30);
        for (int i = 0; i < daysSeconds; i++) {
            LocalDateTime localDateTime = now.plusSeconds(-1);
            CronExpression cronExpression = parse(cronString);
            LocalDateTime next = cronExpression.next(localDateTime);
            if (next != null) return next;
        }
        return null;
    }

}
