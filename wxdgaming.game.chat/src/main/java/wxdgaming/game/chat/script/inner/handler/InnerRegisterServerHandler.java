package wxdgaming.game.chat.script.inner.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.chat.module.inner.InnerService;
import wxdgaming.game.message.inner.InnerRegisterServer;
import wxdgaming.game.message.inner.ServiceType;

/**
 * 注册服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class InnerRegisterServerHandler {

    final InnerService innerService;

    public InnerRegisterServerHandler(InnerService innerService) {
        this.innerService = innerService;
    }

    /** 注册服务 */
    @ProtoRequest
    public void innerRegisterServer(SocketSession socketSession, InnerRegisterServer req) {
        int mainSid = req.getMainSid();
        ServiceType serviceType = req.getServiceType();

    }

}