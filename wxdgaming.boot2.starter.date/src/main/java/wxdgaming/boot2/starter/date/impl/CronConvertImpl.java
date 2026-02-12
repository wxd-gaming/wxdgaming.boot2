package wxdgaming.boot2.starter.date.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.executor.CronExpressionUtil;
import wxdgaming.boot2.starter.date.AbstractDateConvert;

/**
 * cron 表达式转换
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-11 19:23
 **/
@Slf4j
@Component
public class CronConvertImpl extends AbstractDateConvert implements InitPrint {

    @Override public String type() {
        return "cron";
    }

    @Override public long convert(String[] params) {
        return CronExpressionUtil.nextMillis(params[1]);
    }

    @Override public long convertEndTime(long startTime, String[] params) {
        return convert(params);
    }

}
