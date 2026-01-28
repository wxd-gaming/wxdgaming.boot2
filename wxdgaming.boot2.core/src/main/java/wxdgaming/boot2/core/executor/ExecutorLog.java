package wxdgaming.boot2.core.executor;

import java.lang.annotation.*;

/**
 * 指定运行的线程池
 */
@Inherited
@Documented
@Target({
        ElementType.TYPE, /*方法*/
        ElementType.METHOD, /*方法*/
        ElementType.LOCAL_VARIABLE/*局部变量*/
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExecutorLog {

    /** 可以选择关闭或者显示 */
    boolean offWarnLog() default false;

    /** 执行耗时超过33ms 告警日志 */
    long executorWarnTime() default 33;

    /** 任务new出来提交到线程池队列超过 1000 ms 告警日志 */
    long submitWarnTime() default 1000;

}
