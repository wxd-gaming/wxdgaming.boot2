package wxdgaming.boot2.core.threading;

import java.lang.annotation.*;

/**
 * 指定运行的线程池
 */
@Documented
@Target({ElementType.METHOD/*方法*/})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExecutorWith {

    /** 标记是否虚拟线程 指定thread 后失效 */
    boolean useVirtualThread() default false;

    /** 指定thread 如果指定改值，useVirtualThread 失效 */
    String threadName() default "";

    /** 执行队列名称 */
    String queueName() default "";

}
