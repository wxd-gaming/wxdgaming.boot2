package wxdgaming.boot2.core;

import ch.qos.logback.core.LogbackUtil;
import jakarta.annotation.PostConstruct;

/**
 * 添加初始化打印
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-22 09:59
 */
public interface InitPrint {

    @PostConstruct
    default void ___initPrint() {
        LogbackUtil.logger().debug("init print {}, hashcode: {}", this.getClass().getName(), this.hashCode());
    }

}
