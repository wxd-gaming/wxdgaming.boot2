package wxdgaming.boot2.core.assist;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.InitPrint;

import java.util.concurrent.TimeUnit;

/**
 * 切面拦截统计耗时
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-24 21:41
 **/
@Slf4j
@Aspect
@Order(-10001)// 确保优先级
@Component
public class CostTimeAspect implements InitPrint {

    public CostTimeAspect() {
    }

    @Around("@annotation(logCostTime)")
    public Object around(ProceedingJoinPoint pjp, LogCostTime logCostTime) throws Throwable {
        String methodName = pjp.getSignature().getName();
        String desc = logCostTime.value();
        long threshold = logCostTime.threshold();

        long start = System.nanoTime(); // 高精度计时
        Object result;
        try {
            result = pjp.proceed();
        } finally {
            long costNanos = System.nanoTime() - start;
            long costMillis = TimeUnit.NANOSECONDS.toMillis(costNanos);

            // 根据阈值决定日志级别
            if (threshold > 0 && costMillis > threshold) {
                log.warn("方法: {}.{}({}) 耗时超阈值: {} ms (阈值: {} ms)",
                        pjp.getTarget().getClass().getSimpleName(), methodName, desc, costMillis, threshold);
            } else {
                log.info("方法: {}.{}({}) 耗时: {} ms",
                        pjp.getTarget().getClass().getSimpleName(), methodName, desc, costMillis);
            }
        }
        return result;
    }

}
