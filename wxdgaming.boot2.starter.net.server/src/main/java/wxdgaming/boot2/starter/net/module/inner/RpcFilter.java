package wxdgaming.boot2.starter.net.module.inner;

import com.alibaba.fastjson.JSONObject;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.RpcRequest;

import java.lang.reflect.Method;

/**
 * http 请求接口过滤器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-13 16:32
 **/
public abstract class RpcFilter {

    public abstract boolean doFilter(RpcRequest rpcRequest,
                                     Method method,
                                     String cmd,
                                     SocketSession socketSession,
                                     JSONObject paramObject);

}
