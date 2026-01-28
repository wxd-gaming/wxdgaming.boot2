package wxdgaming.boot2.core.timer;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.executor.CronExpressionUtil;
import wxdgaming.boot2.core.lang.ObjectBase;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * cron 表达式解析 {@code {"cron":"0 0","timeUnit":"SECONDS","duration":500}}
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2021-09-27 10:40
 **/
@Slf4j
public class CronExpress extends ObjectBase {

    /** 表达式 */
    @JSONField(ordinal = 1)
    @Getter String cron;
    /** 时间需求 */
    @JSONField(ordinal = 2)
    @Getter TimeUnit timeUnit;
    /** 偏移量 */
    @JSONField(ordinal = 3)
    @Getter long duration;



    /**
     * 用于获取下一次执行时间
     * <p>配置示例 {@code {"cron":"0 0","timeUnit":"SECONDS","duration":500}}
     * <br>
     * <br>
     * 秒 分 时 日 月 星期 年
     * <p> {@code * * * * * * * }
     * <p> 下面以 秒 配置举例
     * <p> * 或者 ? 无限制,
     * <p> 数字是 指定秒执行
     * <p> 0-5 第 0 秒 到 第 5 秒执行 每秒执行
     * <p> 0,5 第 0 秒 和 第 5 秒 各执行一次
     * <p> {@code *}/5 秒 % 5 == 0 执行
     * <p> 5/5 第五秒之后 每5秒执行一次
     * <p> 秒 0-59
     * <p> 分 0-59
     * <p> 时 0-23
     * <p> 日 1-28 or 29 or 30 or 31
     * <p> 月 1-12
     * <p> 星期 1-7 Mon Tues Wed Thur Fri Sat Sun
     * <p> 年 1970 - 2199
     */
    public CronExpress(String cron, TimeUnit timeUnit, long duration) {
        this.cron = cron;
        this.timeUnit = timeUnit;
        this.duration = duration;
    }

    public CronDuration findValidateTime() {
        LocalDateTime localDateTime = CronExpressionUtil.validityTime(cron, timeUnit.toMillis(duration));
        if (localDateTime == null) {
            return null;
        }
        long start = MyClock.time2Milli(localDateTime);
        long end = start + timeUnit.toMillis(duration);
        return new CronDuration(cron, start, end);
    }

    public CronDuration validateTimeBefore() {
        LocalDateTime localDateTime = CronExpressionUtil.up(cron);
        if (localDateTime == null) {
            return null;
        }
        long start = MyClock.time2Milli(localDateTime);
        long end = start + timeUnit.toMillis(duration);
        return new CronDuration(cron, start, end);
    }

    public CronDuration validateTimeAfter() {
        LocalDateTime localDateTime = CronExpressionUtil.next(cron);
        if (localDateTime == null) {
            return null;
        }
        long start = MyClock.time2Milli(localDateTime);
        long end = start + timeUnit.toMillis(duration);
        return new CronDuration(cron, start, end);
    }

}
