package wxdgaming.boot2.starter.net.pojo;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.ann.Sort;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.starter.net.SocketSession;

import java.util.List;

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

    /** 相当于用 read and copy write方式作为线程安全性 */
    ProtoListenerContent protoListenerContent = null;
    IWebSocketStringListener iWebSocketStringListener = null;
    List<ProtoFilter> protoFilters;

    @Init
    @Sort(6)
    public void init(RunApplication runApplication) {
        protoListenerContent = new ProtoListenerContent(runApplication);
        iWebSocketStringListener = runApplication.classWithSuper(IWebSocketStringListener.class).findFirst().orElse(null);
        protoFilters = runApplication.classWithSuper(ProtoFilter.class).toList();
    }

    public RunApplication getRunApplication() {
        return protoListenerContent.getRunApplication();
    }

    public int messageId(Class<? extends PojoBase> pojoClass) {
        return protoListenerContent.messageId(pojoClass);
    }

    /** 这里是由netty的work线程触发 */
    public void dispatch(SocketSession socketSession, int messageId, byte[] data) {
        ProtoMapping mapping = protoListenerContent.getMappingMap().get(messageId);
        if (mapping == null) {
            throw new RuntimeException("未找到消息id: %s".formatted(messageId));
        }
        /*根据映射解析生成触发事件*/
        ProtoListenerTrigger protoListenerTrigger = new ProtoListenerTrigger(mapping, protoListenerContent.getRunApplication(), socketSession, messageId, data);
        if (log.isDebugEnabled()) {
            log.debug("收到消息：{} {} {}", socketSession, messageId, protoListenerTrigger.getPojoBase());
        }
        boolean allMatch = protoFilters.stream().allMatch(filter -> filter.doFilter(protoListenerTrigger));
        if (!allMatch) {
            return;
        }
        if (StringUtils.isBlank(protoListenerTrigger.getQueueName())) {
            /*这里相当于绑定每个session的队列*/
            protoListenerTrigger.setQueueName("session-" + String.valueOf(socketSession.getChannel().id().asShortText().hashCode() % 16));
        }
        /*提交到对应的线程和队列*/
        protoListenerTrigger.submit();
    }

}
