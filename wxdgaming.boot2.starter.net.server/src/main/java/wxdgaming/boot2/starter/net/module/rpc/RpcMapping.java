package wxdgaming.boot2.starter.net.module.rpc;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.assist.JavassistProxy;
import wxdgaming.boot2.starter.net.ann.RpcRequest;

/**
 * rpc 消息注解映射
 *
 * @param rpcRequest      注解
 * @param path            路径
 * @param javassistProxy javassist的代理类
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-08 14:25
 */
@Slf4j
public record RpcMapping(RpcRequest rpcRequest, String path, JavassistProxy javassistProxy) {

}
