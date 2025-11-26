package wxdgaming.boot2.util;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import wxdgaming.boot2.core.executor.ThreadStopWatch;

/**
 * 通过aop切面管理，线程运行上下文的方法耗时记录仪
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-11-18 19:30
 */
@Aspect // 标记为切面
public class ChildThreadStopWatchAspect {

    // 定义切点：匹配 UserService 中所有方法
    @Pointcut("(execution(* wxdgaming..*(..)))&& !execution(* java.lang.Object.*(..))")
    public void allPointcut() {}

    // 前置通知：目标方法执行前执行
    @Before("allPointcut()")
    public void beforeAdvice(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        ThreadStopWatch.startIfPresent(methodName);
    }

    // 后置通知：目标方法执行后执行（无论是否异常）
    @After("allPointcut()")
    public void afterAdvice() {
        ThreadStopWatch.stopIfPresent();
    }
}
