package wxdgaming.game.server.module.timer;

import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.executor.ExecutorWith;
import wxdgaming.boot2.starter.net.httpclient5.HttpRequestPost;
import wxdgaming.boot2.starter.net.httpclient5.HttpResponse;
import wxdgaming.boot2.starter.net.server.SocketServer;
import wxdgaming.boot2.starter.scheduled.ann.Scheduled;
import wxdgaming.game.common.bean.login.ConnectLoginProperties;
import wxdgaming.game.login.bean.ServerInfoDTO;
import wxdgaming.game.server.GameServerProperties;
import wxdgaming.game.server.module.drive.PlayerDriveService;
import wxdgaming.game.server.module.inner.ConnectLoginService;
import wxdgaming.boot2.core.util.SignUtil;

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
    final ConnectLoginService connectLoginService;

    public GameTimerService(SocketServer socketServer,
                            GameServerProperties gameServerProperties, ConnectLoginProperties connectLoginProperties,
                            PlayerDriveService playerDriveService, ConnectLoginService connectLoginService) {
        this.socketServer = socketServer;
        this.gameServerProperties = gameServerProperties;
        this.connectLoginProperties = connectLoginProperties;
        this.playerDriveService = playerDriveService;
        this.connectLoginService = connectLoginService;
    }


    /** 向登陆服务器注册 */
    @Scheduled(value = "*/5", async = true)
    @ExecutorWith(useVirtualThread = true)
    public void registerLoginServer() {

        ServerInfoDTO serverInfoDTO = new ServerInfoDTO();
        serverInfoDTO.setSid(gameServerProperties.getSid());
        serverInfoDTO.setPort(socketServer.getConfig().getPort());
        serverInfoDTO.setHttpPort(webPort);
        serverInfoDTO.setOnlineSize(playerDriveService.onlineSize());

        String sign = SignUtil.signByJsonKey(serverInfoDTO, connectLoginProperties.getJwtKey());

        String url = connectLoginProperties.getUrl() + "/inner/game/sync";
        HttpResponse execute = HttpRequestPost.ofJson(url, serverInfoDTO.toJSONString())
                .addHeader(HttpHeaderNames.AUTHORIZATION.toString(), sign)
                .execute();
        if (!execute.isSuccess()) {
            log.error("访问登陆服务器失败{}", url);
            return;
        }
        log.debug("向登陆服务器注册: {}", execute.bodyString());
    }

}
