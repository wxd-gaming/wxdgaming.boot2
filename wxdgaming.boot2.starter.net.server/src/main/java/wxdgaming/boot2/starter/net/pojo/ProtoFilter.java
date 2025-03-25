package wxdgaming.boot2.starter.net.pojo;

import wxdgaming.boot2.starter.net.SocketSession;

/**
 * http 请求接口过滤器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-13 16:32
 **/
public abstract class ProtoFilter {

    public abstract boolean doFilter(ProtoListenerTrigger protoListenerTrigger,
                                     SocketSession socketSession,
                                     PojoBase pojoBase);

}
