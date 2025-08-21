package wxdgaming.game.gateway.script.inner.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.game.gateway.module.data.DataCenterService;
import wxdgaming.game.message.inner.InnerRegisterServer;

/**
 * null
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class InnerRegisterServerHandler {

    private final DataCenterService dataCenterService;

    public InnerRegisterServerHandler(DataCenterService dataCenterService) {
        this.dataCenterService = dataCenterService;
    }

    /** null */
    @ProtoRequest(InnerRegisterServer.class)
    public void reqRegisterServer(ProtoEvent event) {
        SocketSession socketSession = event.getSocketSession();
        InnerRegisterServer req = event.buildMessage();
        dataCenterService.registerServerMapping(socketSession, req);
    }

}