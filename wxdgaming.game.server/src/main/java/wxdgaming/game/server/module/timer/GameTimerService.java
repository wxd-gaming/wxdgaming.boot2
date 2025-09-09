package wxdgaming.game.server.module.timer;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.core.executor.ExecutorWith;
import wxdgaming.boot2.starter.net.httpclient5.HttpRequestPost;
import wxdgaming.boot2.starter.net.httpclient5.HttpResponse;
import wxdgaming.boot2.starter.net.server.SocketServer;
import wxdgaming.boot2.starter.scheduled.ann.Scheduled;
import wxdgaming.game.common.bean.login.ConnectLoginProperties;
import wxdgaming.game.login.entity.server.ServerInfoEntity;
import wxdgaming.game.server.GameServerProperties;
import wxdgaming.game.server.module.drive.PlayerDriveService;
import wxdgaming.game.server.module.inner.InnerService;

import java.util.List;

/**
 * 游戏进程的定时器服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-11 20:22
 **/
@Slf4j
@Getter
@Service
public class GameTimerService extends HoldApplicationContext {

    @Value("${server.port}")
    int webPort;
    final SocketServer socketServer;
    final GameServerProperties gameServerProperties;
    final ConnectLoginProperties connectLoginProperties;
    final PlayerDriveService playerDriveService;
    final InnerService innerService;

    public GameTimerService(SocketServer socketServer,
                            GameServerProperties gameServerProperties, ConnectLoginProperties connectLoginProperties,
                            PlayerDriveService playerDriveService, InnerService innerService) {
        this.socketServer = socketServer;
        this.gameServerProperties = gameServerProperties;
        this.connectLoginProperties = connectLoginProperties;
        this.playerDriveService = playerDriveService;
        this.innerService = innerService;
    }


    /** 向登陆服务器注册 */
    @Scheduled(value = "*/5", async = true)
    @ExecutorWith(useVirtualThread = true)
    public void registerLoginServer() {

        ServerInfoEntity serverInfoBean = new ServerInfoEntity();
        serverInfoBean.setServerId(gameServerProperties.getSid());
        serverInfoBean.setMainId(gameServerProperties.getSid());
        serverInfoBean.setGid(gameServerProperties.getGid());
        serverInfoBean.setName(gameServerProperties.getName());
        serverInfoBean.setPort(socketServer.getConfig().getPort());
        serverInfoBean.setHttpPort(webPort);

        serverInfoBean.setMaxOnlineSize(connectLoginProperties.getMaxOnlineSize());
        serverInfoBean.setOnlineSize(playerDriveService.onlineSize());


        JSONObject jsonObject = MapOf.newJSONObject();
        jsonObject.put("sidList", List.of(gameServerProperties.getSid()));
        jsonObject.put("sid", gameServerProperties.getSid());
        jsonObject.put("serverBean", serverInfoBean.toJSONString());

        innerService.sign(jsonObject);

        String url = connectLoginProperties.getUrl() + "/inner/registerGame";
        HttpResponse execute = HttpRequestPost.ofJson(url, jsonObject.toString()).execute();
        if (!execute.isSuccess()) {
            log.error("访问登陆服务器失败{}", url);
            return;
        }
        log.debug("向登陆服务器注册: {}", execute.bodyString());
    }

}
