package wxdgaming.boot2.core.executor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.concurrent.Executor;

/**
 * 线程信息
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-21 19:50
 **/
@Getter
@Setter(value = AccessLevel.PROTECTED)
@Accessors(chain = true)
public class ThreadVO {

    Executor executor;
    ExecutorQueue executorQueue;
    String queueName;

}
