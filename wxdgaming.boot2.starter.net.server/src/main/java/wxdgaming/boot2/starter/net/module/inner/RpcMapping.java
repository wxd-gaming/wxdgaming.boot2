package wxdgaming.boot2.starter.net.module.inner;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.ann.RpcRequest;

import java.lang.reflect.Method;

@Slf4j
public record RpcMapping(RpcRequest rpcRequest, String path, Object ins, Method method) {

}
