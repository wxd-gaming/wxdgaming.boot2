package wxdgaming.boot2.core.threading;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 线程池配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-13 15:05
 **/
@Getter
@Setter
public class ExecutorConfig extends ObjectBase {

    private int defaultCoreSize = 2;
    private int defaultMaxSize = 4;

    private int logicCoreSize = 10;
    private int logicMaxSize = 32;

    private int virtualCoreSize = 100;
    private int virtualMaxSize = 200;

}
