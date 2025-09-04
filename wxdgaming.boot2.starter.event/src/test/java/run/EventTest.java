package run;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import run.event.B1Event;
import run.event.StartEvent;
import run.event.StopEvent;
import wxdgaming.boot2.core.MainApplicationContextProvider;
import wxdgaming.boot2.core.SpringUtil;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.starter.event.EventScan;
import wxdgaming.boot2.starter.event.EventService;

/**
 * 测试
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-04 10:09
 **/
@SpringBootApplication(scanBasePackageClasses = {
        EventScan.class,
        EventTest.class,
})
public class EventTest {

    public static void main(String[] args) {
        MainApplicationContextProvider.builder(EventTest.class).run(args);
        SpringUtil.mainApplicationContextProvider.executeMethodWithAnnotatedInit();
        EventService eventService = SpringUtil.mainApplicationContextProvider.getBean(EventService.class);
        eventService.postEvent(new StartEvent());
        eventService.postEvent(new B1Event(1));
        eventService.postEvent(new B1Event(2));
        eventService.postEvent(new StopEvent());
    }

}
