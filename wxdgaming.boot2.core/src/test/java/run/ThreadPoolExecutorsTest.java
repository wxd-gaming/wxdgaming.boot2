package run;

import org.junit.Test;
import wxdgaming.boot2.core.threading.ExecutorServices;
import wxdgaming.boot2.core.threading.ExecutorUtil;

import java.util.concurrent.TimeUnit;

/**
 * 线程池测试
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-21 17:39
 **/
public class ThreadPoolExecutorsTest {

    @Test
    public void testClose() {
        ExecutorServices executorServices = ExecutorUtil.getInstance().newExecutorServices("scheduled-executor", 1, 1, 1000);
        executorServices.scheduleAtFixedDelay(() -> {}, 1, 1, TimeUnit.SECONDS);
        executorServices.shutdown();
    }

}
