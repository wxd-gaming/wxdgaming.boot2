package wxdgaming.boot2.core.proxy;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.executor.ThreadStopWatch;

/**
 * 通过aop切面管理，线程运行上下文的方法耗时记录仪
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-11-18 19:30
 */
@Aspect // 标记为切面
@Component // 纳入 Spring 容器管理
public class MainThreadStopWatchAspect {

    // 定义切点：匹配 UserService 中所有方法
    @Pointcut("(execution(* wxdgaming..*(..))) && !execution(* java.lang.Object.*(..)) && !@annotation(wxdgaming.boot2.core.proxy.IgnoreAspect)")
    public void allPointcut() {}

    // 前置通知：目标方法执行前执行
    @Before("allPointcut()")
    public void beforeAdvice(JoinPoint joinPoint) {
        String declaringTypeName = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        ThreadStopWatch.startIfPresent(declaringTypeName + "." + methodName);
    }

    // 后置通知：目标方法执行后执行（无论是否异常）
    @After("allPointcut()")
    public void afterAdvice() {
        ThreadStopWatch.stopIfPresent();
    }
}
