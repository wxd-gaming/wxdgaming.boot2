package wxdgaming.game.server.script.timer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.boot2.starter.scheduled.ann.Scheduled;
import wxdgaming.game.message.inner.ReqRegisterServer;

import java.util.Collection;

/**
 * 服务器定时器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-28 09:49
 **/
@Slf4j
@Singleton
public class ServerTimer extends HoldRunApplication {

    private final ProtoListenerFactory protoListenerFactory;

    @Inject
    public ServerTimer(ProtoListenerFactory protoListenerFactory) {
        this.protoListenerFactory = protoListenerFactory;
    }


    @Scheduled("*/20")
    public void reportGateWay() {
        ReqRegisterServer registerServer = new ReqRegisterServer();
        registerServer.setGameId(BootConfig.getIns().gid());
        registerServer.getServerIds().add(BootConfig.getIns().sid());
        Collection<Integer> values = protoListenerFactory.getProtoListenerContent().getMessage2MappingMap().values();
        registerServer.getMessageIds().addAll(values);
        log.info("{}", registerServer);
    }

}
