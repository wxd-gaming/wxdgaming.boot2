package wxdgaming.game.server.event;

import java.lang.annotation.*;

/**
 * 每秒执行
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-08 13:15
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnHeartSecond {
}
