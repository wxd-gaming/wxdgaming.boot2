package run;

import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.scheduled.ann.Scheduled;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-04 17:37
 **/
@Component
public class ScheduledTest {


    @Scheduled("* * * * * ?")
    public void s1() {
        System.out.println("s1");
    }

}
