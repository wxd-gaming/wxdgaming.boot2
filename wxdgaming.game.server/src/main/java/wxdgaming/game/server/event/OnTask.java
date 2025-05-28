package wxdgaming.game.server.event;

import java.lang.annotation.*;

/**
 * 触发任务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-30 13:34
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnTask {
}
