package run;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import run.event.B1Event;
import run.event.StartEvent;
import run.event.StopEvent;
import wxdgaming.boot2.core.InitPrint;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-04 10:12
 **/
@Slf4j
@Service
public class TestServer implements InitPrint {

    public TestServer() {
    }

    public void start(StartEvent startEvent) {
        log.info("启动成功");
    }

    public void b1(B1Event event) {
        log.info("B1Event {}", event.i());
    }

    public void stop(StopEvent stopEvent) {
        log.info("停服");
    }

}
