package wxdgaming.boot2.starter.date.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.executor.CronExpressionUtil;
import wxdgaming.boot2.starter.date.IDateConvert;

/**
 * cron 表达式转换
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-11 19:23
 **/
@Slf4j
@Component
public class CronConvertImpl implements InitPrint, IDateConvert {

    public CronConvertImpl() {
    }

    @Override public String type() {
        return "cron";
    }

    @Override public long convert(String date) {
        return CronExpressionUtil.nextMillis(date);
    }

    @Override public long convertEndTime(long startTime, String date) {
        return convert(date);
    }

}
