package wxdgaming.game.gateway.module.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.Throw;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.core.executor.ExecutorWith;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.util.Md5Util;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.client.SocketClientConfig;
import wxdgaming.boot2.starter.net.httpclient5.HttpResponse;
import wxdgaming.boot2.starter.net.httpclient5.HttpRequestPost;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.boot2.starter.net.server.SocketServer;
import wxdgaming.boot2.starter.net.server.http.HttpListenerFactory;
import wxdgaming.boot2.starter.scheduled.ann.Scheduled;
import wxdgaming.game.login.LoginConfig;
import wxdgaming.game.login.bean.info.InnerServerInfoBean;
import wxdgaming.game.message.inner.InnerRegisterServer;
import wxdgaming.game.message.inner.ServiceType;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 网关和游戏服之间的连接管理服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-28 13:45
 **/
@Slf4j
@Getter
@Singleton
public class Gateway2GameSessionService extends HoldRunApplication {

    final LoginConfig loginConfig;
    final SocketServer socketServer;
    final ConcurrentHashMap<Integer, Gateway2GameSocketClientImpl> gameSessionMap = new ConcurrentHashMap<>();

    private final ProtoListenerFactory protoListenerFactory;
    private final HttpListenerFactory httpListenerFactory;

    @Inject
    public Gateway2GameSessionService(LoginConfig loginConfig, SocketServer socketServer,
                                      ProtoListenerFactory protoListenerFactory, HttpListenerFactory httpListenerFactory) {
        this.loginConfig = loginConfig;
        this.socketServer = socketServer;
        this.protoListenerFactory = protoListenerFactory;
        this.httpListenerFactory = httpListenerFactory;
    }

    @Start
    public void start() {
        // checkGatewaySession();
    }

    /** 向登陆服务器注册 */
    @Scheduled(value = "*/5", async = true)
    @ExecutorWith(useVirtualThread = true)
    public void registerLoginServer() {

        InnerServerInfoBean serverInfoBean = new InnerServerInfoBean();
        serverInfoBean.setServerId(BootConfig.getIns().sid());
        serverInfoBean.setMainId(BootConfig.getIns().sid());
        serverInfoBean.setName(BootConfig.getIns().sname());
        serverInfoBean.setPort(socketServer.getConfig().getPort());
        serverInfoBean.setHttpPort(socketServer.getConfig().getPort());

        serverInfoBean.setMaxOnlineSize(loginConfig.getMaxOnlineSize());
        serverInfoBean.setOnlineSize(socketServer.getSessionGroup().size());

        JSONObject jsonObject = MapOf.newJSONObject();
        jsonObject.put("sid", BootConfig.getIns().sid());
        jsonObject.put("serverBean", serverInfoBean.toJSONString());

        String json = jsonObject.toString(SerializerFeature.MapSortField, SerializerFeature.SortField);
        String md5DigestEncode = Md5Util.md5DigestEncode0("#", json, loginConfig.getJwtKey());
        jsonObject.put("sign", md5DigestEncode);

        String string = jsonObject.toString();
        HttpResponse httpResponse = HttpRequestPost.ofJson(loginConfig.getUrl() + "/inner/registerGateway", string).execute();
        if (!httpResponse.isSuccess()) {
            log.error("访问登陆服务器失败{}", Throw.ofString(httpResponse.getException(), false));
            return;
        }
        log.info("登录服务器注册完成返回信息: {}", httpResponse.bodyString());
        RunResult runResult = httpResponse.bodyRunResult();
        if (runResult.code() == 1) {

            InnerRegisterServer registerServer = new InnerRegisterServer();
            registerServer.setServiceType(ServiceType.GATEWAY);
            registerServer.setMainSid(BootConfig.getIns().sid());

            List<InnerServerInfoBean> data = runResult.getObject("data", new TypeReference<List<InnerServerInfoBean>>() {});
            HashSet<Integer> hasServerIdSet = new HashSet<>();
            for (InnerServerInfoBean bean : data) {
                hasServerIdSet.add(bean.getServerId());
                checkGatewaySession(bean.getServerId(), bean.getHost(), bean.getPort(), registerServer);
            }
            Iterator<Map.Entry<Integer, Gateway2GameSocketClientImpl>> iterator = getGameSessionMap().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, Gateway2GameSocketClientImpl> next = iterator.next();
                if (!hasServerIdSet.contains(next.getKey())) {
                    next.getValue().shutdown();
                    iterator.remove();
                    SocketClientConfig config = next.getValue().getConfig();
                    log.info("sid={}, host={}, port={} 游戏服务下线，不在需要链接", next.getKey(), config.getHost(), config.getPort());
                    /*可能还需要对在改服的玩家进行处理*/
                }
            }
        }

    }

    /** 网关主动连游戏服 */
    public void checkGatewaySession(int sid, final String inetHost, final int inetPort, InnerRegisterServer registerServer) {
        Gateway2GameSocketClientImpl gatewaySocketClient = getGameSessionMap().computeIfAbsent(sid, l -> {
            SocketClientConfig socketClientConfig = BootConfig.getIns().getNestedValue("socket.client-forward", SocketClientConfig.class);
            socketClientConfig = (SocketClientConfig) socketClientConfig.clone();
            socketClientConfig.setHost(inetHost);
            socketClientConfig.setPort(inetPort);
            socketClientConfig.setMaxConnectionCount(1);
            socketClientConfig.setEnabledReconnection(false);
            Gateway2GameSocketClientImpl socketClient = new Gateway2GameSocketClientImpl(socketClientConfig);
            socketClient.init(protoListenerFactory, httpListenerFactory);
            return socketClient;
        });

        gatewaySocketClient.checkSync(null);

        SocketSession socketSession = gatewaySocketClient.idle();
        if (socketSession != null) {
            if (socketSession.isOpen()) {
                log.info("{}", registerServer);
                socketSession.write(registerServer);
            }
        }
    }


}
