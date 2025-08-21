package wxdgaming.game.chart.script.inner.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.game.message.inner.InnerRegisterServer;

/**
 * 注册服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class InnerRegisterServerHandler {

    /** 注册服务 */
    @ProtoRequest(value = InnerRegisterServer.class)
    public void innerRegisterServer(ProtoEvent event) {
        SocketSession socketSession = event.getSocketSession();
        InnerRegisterServer message = event.buildMessage();

    }

}