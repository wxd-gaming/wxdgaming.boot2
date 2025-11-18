package wxdgaming.boot2.core.proxy;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.executor.ThreadStopWatch;

@Aspect // 标记为切面
@Component // 纳入 Spring 容器管理
public class CustomAspect {

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
