package executor;

/**
 * 执行器告警时间
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-26 21:02
 **/
public interface RunnableWarnTime {

    /** 关闭警告日志 */
    default boolean offWarnLog() {return false;}

    /** 执行耗时超过33ms 告警日志 */
    default long getExecutorWarnTime() {return 33;}

    /** 任务new出来提交到线程池队列超过33ms 告警日志 */
    default long getSubmitWarnTime() {return 33;}

}
