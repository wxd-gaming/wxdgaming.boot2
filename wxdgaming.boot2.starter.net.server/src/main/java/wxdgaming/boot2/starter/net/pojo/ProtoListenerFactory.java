package wxdgaming.boot2.starter.net.pojo;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.ann.Sort;
import wxdgaming.boot2.starter.net.SocketSession;

/**
 * 派发器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-14 20:01
 **/
@Slf4j
@Getter
@Singleton
public class ProtoListenerFactory {

    ProtoListenerContent protoListenerContent = null;

    @Init
    @Sort(6)
    public void init(RunApplication runApplication) {
        protoListenerContent = new ProtoListenerContent(runApplication);
    }

    public RunApplication getRunApplication() {
        return protoListenerContent.getRunApplication();
    }

    public int messageId(Class<? extends PojoBase> pojoClass) {
        return protoListenerContent.messageId(pojoClass);
    }

    public void dispatch(SocketSession socketSession, int messageId, byte[] data) {
        ProtoMapping mapping = protoListenerContent.getMappingMap().get(messageId);
        if (mapping == null) {
            throw new RuntimeException("未找到消息id: %s".formatted(messageId));
        }
        ProtoListenerTrigger protoListenerTrigger = new ProtoListenerTrigger(mapping, protoListenerContent.getRunApplication(), socketSession, messageId, data);
        protoListenerTrigger.submit();
    }

}
