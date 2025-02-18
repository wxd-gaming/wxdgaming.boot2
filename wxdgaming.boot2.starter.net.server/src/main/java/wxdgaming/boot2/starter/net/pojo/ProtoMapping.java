package wxdgaming.boot2.starter.net.pojo;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;

import java.lang.reflect.Method;

@Slf4j
public record ProtoMapping(
        ProtoRequest protoRequest,
        int messageId,
        Class<? extends PojoBase> pojoClass,
        Object ins,
        Method method) {

}
