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
    boolean off() default false;

    /** 执行日志记录时间 */
    long logTime() default 33;

    /** 执行报警时间 */
    long warningTime() default 1000;

}
