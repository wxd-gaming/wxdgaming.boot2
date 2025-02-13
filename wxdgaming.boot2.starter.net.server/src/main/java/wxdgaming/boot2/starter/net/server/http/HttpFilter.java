package wxdgaming.boot2.starter.net.server.http;

import java.lang.reflect.Method;

/**
 * http 请求接口过滤器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-13 16:32
 **/
public abstract class HttpFilter {

    /**
     * 过滤器
     *
     * @param event    本次需要执行事件
     * @param instance 执行事件的实例
     * @param method   执行事件的方法体
     * @return false 则终止后续过滤器
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2025-02-13 16:33
     */
    public abstract boolean doFilter(HttpListenerEvent event, Object instance, Method method);

}
