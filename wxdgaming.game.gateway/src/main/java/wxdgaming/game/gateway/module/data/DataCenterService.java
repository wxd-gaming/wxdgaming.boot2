package wxdgaming.game.gateway.module.data;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.starter.net.ChannelUtil;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.server.SocketServerImpl;
import wxdgaming.game.gateway.bean.ServerMapping;
import wxdgaming.game.gateway.bean.UserMapping;
import wxdgaming.game.message.inner.InnerRegisterServer;
import wxdgaming.game.message.inner.ServiceType;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据中心
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-28 10:27
 **/
@Slf4j
@Getter
@Singleton
public class DataCenterService extends HoldRunApplication {

    /** 服务映射 */
    private final ConcurrentHashMap<Integer, ServerMapping> gameServiceMappings = new ConcurrentHashMap<>();
    private final ServerMapping chartServiceMapping = new ServerMapping();
    /** key:account, value:mapping */
    private final ConcurrentHashMap<String, UserMapping> userMappings = new ConcurrentHashMap<>();
    private final SocketServerImpl socketServer;

    @Inject
    public DataCenterService(SocketServerImpl socketServer) {
        this.socketServer = socketServer;
    }

    public void registerServerMapping(SocketSession socketSession, InnerRegisterServer reqRegisterServer) {
        ServiceType serviceType = reqRegisterServer.getServiceType();
        switch (serviceType) {
            case GAME: {
                List<Integer> serverIds = reqRegisterServer.getServerIds();
                final int mainSid = reqRegisterServer.getMainSid();

                final ServerMapping serverMapping = gameServiceMappings.computeIfAbsent(mainSid, k -> new ServerMapping());
                serverMapping.setSession(socketSession);
                serverMapping.setGid(mainSid);
                serverMapping.setMainSid(mainSid);
                serverMapping.setSid(serverIds);
                serverMapping.getMessageIds().addAll(reqRegisterServer.getMessageIds());
                serverMapping.setIp(ChannelUtil.getIP(socketSession.getChannel()));

                for (Integer serverId : serverIds) {
                    /*覆盖子服的映射*/
                    gameServiceMappings.put(serverId, serverMapping);
                    if (log.isDebugEnabled()) {
                        log.debug("收到服务 {} sid={} 注册 {}", serviceType, serverId, serverMapping);
                    }
                }

                socketSession.getChannel().closeFuture().addListener(new ChannelFutureListener() {
                    @Override public void operationComplete(ChannelFuture future) throws Exception {
                        if (Objects.equals(serverMapping.getSession(), socketSession)) {
                            serverMapping.setSession(null);
                            log.info("游戏服务掉线 sid={} 注册 {}", mainSid, serverMapping);
                        }
                    }
                });

            }
            break;
            case CHAT: {
                int mainSid = reqRegisterServer.getMainSid();
                chartServiceMapping.setSession(socketSession);
                chartServiceMapping.setGid(mainSid);
                chartServiceMapping.setMainSid(mainSid);
                chartServiceMapping.getMessageIds().addAll(reqRegisterServer.getMessageIds());
                chartServiceMapping.setIp(ChannelUtil.getIP(socketSession.getChannel()));

                if (log.isDebugEnabled()) {
                    log.debug("收到服务 {} sid={} 注册 {}", serviceType, mainSid, chartServiceMapping);
                }

                socketSession.getChannel().closeFuture().addListener(new ChannelFutureListener() {
                    @Override public void operationComplete(ChannelFuture future) throws Exception {
                        if (Objects.equals(chartServiceMapping.getSession(), socketSession)) {
                            chartServiceMapping.setSession(null);
                            log.info("社交服务掉线 sid={} {}", mainSid, chartServiceMapping);
                        }
                    }
                });
            }
            break;
            default:
                break;
        }

    }

    public UserMapping getUserMapping(String account) {
        return userMappings.computeIfAbsent(account, l -> new UserMapping().setAccount(account));
    }

    public SocketSession getClientSession(long sessionId) {
        return socketServer.getSessionGroup().getChannelMap().get(sessionId);
    }

}
