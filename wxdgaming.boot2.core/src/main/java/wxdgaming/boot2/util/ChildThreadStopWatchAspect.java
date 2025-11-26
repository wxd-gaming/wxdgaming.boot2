package wxdgaming.boot2.util;

import org.aspectj.lang.annotation.Aspect;
import wxdgaming.boot2.core.proxy.MainThreadStopWatchAspect;

/**
 * 通过aop切面管理，线程运行上下文的方法耗时记录仪
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-11-18 19:30
 */
@Aspect // 标记为切面
public class ChildThreadStopWatchAspect extends MainThreadStopWatchAspect {

}
