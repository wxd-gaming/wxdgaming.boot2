package run;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.core.MainApplicationContextProvider;
import wxdgaming.boot2.core.SpringUtil;
import wxdgaming.boot2.starter.scheduled.ScheduledScan;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-04 17:38
 **/
@SpringBootApplication(scanBasePackageClasses = {
        CoreScan.class,
        ScheduledScan.class,
        ScheduledTest.class,
})
public class ScheduledApplicationTest {

    public static void main(String[] args) {
        MainApplicationContextProvider.builder(ScheduledApplicationTest.class)
                .web(WebApplicationType.NONE)
                .run(args);

        SpringUtil.mainApplicationContextProvider.postInitEvent().executeMethodWithAnnotatedStart();

    }

}
