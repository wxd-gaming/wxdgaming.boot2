package wxdgaming.boot2.core.assist;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * aop 切面拦截 耗时统计
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-24 21:41
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogCostTime {

    String value() default ""; // 方法描述

    long threshold() default 0; // 耗时阈值（ms），超过则告警

}
