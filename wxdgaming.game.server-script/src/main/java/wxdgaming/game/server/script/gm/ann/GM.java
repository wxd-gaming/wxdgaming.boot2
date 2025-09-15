package wxdgaming.game.server.script.gm.ann;

import java.lang.annotation.*;

/**
 * gm 命令
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-30 09:56
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GM {

    String group() default "";

    String name() default "";

    String param() default "";

    /** 等级权限。默认1. 特殊权限可以设置999， */
    int level() default 1;

}
