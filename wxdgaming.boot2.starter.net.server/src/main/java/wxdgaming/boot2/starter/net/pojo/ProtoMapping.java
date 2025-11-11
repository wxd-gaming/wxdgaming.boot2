package wxdgaming.boot2.starter.net.pojo;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.reflect.InstanceMethodProvider;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;

/**
 * proto消息注解映射
 *
 * @param protoRequest   注解
 * @param messageId      消息id
 * @param pojoClass      消息类
 * @param providerMethod javassist的代理类
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-08 13:34
 */
@Slf4j
public record ProtoMapping(ProtoRequest protoRequest, int messageId,
        Class<? extends PojoBase> pojoClass,
        InstanceMethodProvider providerMethod) {

}
