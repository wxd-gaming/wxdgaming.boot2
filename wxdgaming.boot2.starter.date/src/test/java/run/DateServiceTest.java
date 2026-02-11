package run;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wxdgaming.boot2.core.ApplicationContextProvider;
import wxdgaming.boot2.core.event.InitEvent;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.date.DateScan;
import wxdgaming.boot2.starter.date.DateService;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-11 19:51
 **/
@Slf4j
@SpringBootTest(classes = {DateScan.class})
public class DateServiceTest {

    @Autowired
    private DateService dateService;
    @Autowired
    protected ApplicationContextProvider applicationContextProvider;

    @PostConstruct
    public void init() {
        dateService.___initHold(new InitEvent(applicationContextProvider));
    }

    @Test
    public void t1() {
        String cfgString = "cron#0 0 20 * * ?&cron#0 30 20 * * ?";
        Triple<Boolean, Long, Long> booleanLongLongTriple = dateService.convertBeginEnd(cfgString);
        log.info("cron表达式控制开启时间和结束时间 {}, {}, {}, {}", cfgString, booleanLongLongTriple, MyClock.formatDate(booleanLongLongTriple.getMiddle()), MyClock.formatDate(booleanLongLongTriple.getRight()));
    }

    @Test
    public void t2() {
        String cfgString = "cron#0 0 20 * * ?&minute#30";
        Triple<Boolean, Long, Long> booleanLongLongTriple = dateService.convertBeginEnd(cfgString);
        log.info("cron表达式控制开始时间，然后持续30分钟 {}, {}, {}, {}", cfgString, booleanLongLongTriple, MyClock.formatDate(booleanLongLongTriple.getMiddle()), MyClock.formatDate(booleanLongLongTriple.getRight()));
    }

    @Test
    public void t3() {
        String cfgString = "yyyymmdd#20260211&yyyymmdd#20260401";
        Triple<Boolean, Long, Long> booleanLongLongTriple = dateService.convertBeginEnd(cfgString);
        log.info("通过yyyymmdd时间格式化控制 {}, {}, {}, {}", cfgString, booleanLongLongTriple, MyClock.formatDate(booleanLongLongTriple.getMiddle()), MyClock.formatDate(booleanLongLongTriple.getRight()));
    }

    @Test
    public void t4() {
        String cfgString = "yyyymmdd#20260211&minute#30";
        Triple<Boolean, Long, Long> booleanLongLongTriple = dateService.convertBeginEnd(cfgString);
        log.info("通过yyyymmdd时间格式化控制, 并且持续30分钟 {}, {}, {}, {}", cfgString, booleanLongLongTriple, MyClock.formatDate(booleanLongLongTriple.getMiddle()), MyClock.formatDate(booleanLongLongTriple.getRight()));
    }

    @Test
    public void t5() {
        String cfgString = "yyyymmddhhmmss#20260211200000&yyyymmddhhmmss#20260331235959";
        Triple<Boolean, Long, Long> booleanLongLongTriple = dateService.convertBeginEnd(cfgString);
        log.info("通过yyyymmdd时间格式化控制 {}, {}, {}, {}", cfgString, booleanLongLongTriple, MyClock.formatDate(booleanLongLongTriple.getMiddle()), MyClock.formatDate(booleanLongLongTriple.getRight()));
    }

    @Test
    public void t6() {
        String cfgString = "current#0&CurrentDayMax#0";
        Triple<Boolean, Long, Long> booleanLongLongTriple = dateService.convertBeginEnd(cfgString);
        log.info("current 当前时间持续到今天的23:59:59 {}, {}, {}, {}", cfgString, booleanLongLongTriple, MyClock.formatDate(booleanLongLongTriple.getMiddle()), MyClock.formatDate(booleanLongLongTriple.getRight()));
    }

}
