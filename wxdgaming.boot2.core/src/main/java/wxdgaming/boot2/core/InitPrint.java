package wxdgaming.boot2.core;

import ch.qos.logback.core.LogbackUtil;
import wxdgaming.boot2.core.ann.PostConstruct;

/**
 * 添加初始化打印
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-22 09:59
 */
public interface InitPrint {

    @PostConstruct
    default void initPrint() {
        LogbackUtil.logger().debug("init print {}", this.getClass().getName());
    }

}
