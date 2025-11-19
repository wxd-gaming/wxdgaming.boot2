package wxdgaming.boot2.core.proxy;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;

/**
 * 代理
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-11-18 09:47
 **/
public abstract class AopProxyUtil {

    public static boolean isProxy(Object bean) {
        // JDK 动态代理：类名包含 "$Proxy"
        // CGLIB 代理：类名包含 "$$EnhancerBySpringCGLIB$$"
        return bean.getClass().getName().contains("$$") || AopUtils.isAopProxy(bean); // Spring 提供的工具类（推荐）
    }

    public static Object getTargetObject(Object proxyBean) {
        if (!isProxy(proxyBean)) {
            return proxyBean; // 不是代理对象，直接返回自身
        }
        // Spring 工具类：获取原始目标对象
        Object singletonTarget = AopProxyUtils.getSingletonTarget(proxyBean);
        return singletonTarget == null ? proxyBean : singletonTarget;
    }

}
