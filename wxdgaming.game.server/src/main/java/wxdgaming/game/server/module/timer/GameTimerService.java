package wxdgaming.game.server.module.timer;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.BootstrapProperties;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.core.executor.ExecutorWith;
import wxdgaming.boot2.core.util.Md5Util;
import wxdgaming.boot2.starter.net.httpclient5.HttpRequestPost;
import wxdgaming.boot2.starter.net.httpclient5.HttpResponse;
import wxdgaming.boot2.starter.net.server.SocketServer;
import wxdgaming.boot2.starter.scheduled.ann.Scheduled;
import wxdgaming.game.basic.login.LoginProperties;
import wxdgaming.game.basic.login.bean.info.InnerServerInfoBean;
import wxdgaming.game.server.module.drive.PlayerDriveService;

import java.util.List;

/**
 * 游戏进程的定时器服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-11 20:22
 **/
@Slf4j
@Service
public class GameTimerService {

    final SocketServer socketServer;
    final BootstrapProperties bootstrapProperties;
    final LoginProperties loginProperties;
    final PlayerDriveService playerDriveService;

    public GameTimerService(SocketServer socketServer, BootstrapProperties bootstrapProperties, LoginProperties loginProperties, PlayerDriveService playerDriveService) {
        this.socketServer = socketServer;
        this.bootstrapProperties = bootstrapProperties;
        this.loginProperties = loginProperties;
        this.playerDriveService = playerDriveService;
    }


    /** 向登陆服务器注册 */
    @Scheduled(value = "*/5", async = true)
    @ExecutorWith(useVirtualThread = true)
    public void registerLoginServer() {

        InnerServerInfoBean serverInfoBean = new InnerServerInfoBean();
        serverInfoBean.setServerId(bootstrapProperties.getSid());
        serverInfoBean.setMainId(bootstrapProperties.getSid());
        serverInfoBean.setGid(bootstrapProperties.getGid());
        serverInfoBean.setName(bootstrapProperties.getName());
        serverInfoBean.setPort(socketServer.getConfig().getPort());
        serverInfoBean.setHttpPort(socketServer.getConfig().getPort());

        serverInfoBean.setMaxOnlineSize(loginProperties.getMaxOnlineSize());
        serverInfoBean.setOnlineSize(playerDriveService.onlineSize());


        JSONObject jsonObject = MapOf.newJSONObject();
        jsonObject.put("sidList", List.of(bootstrapProperties.getSid()));
        jsonObject.put("sid", bootstrapProperties.getSid());
        jsonObject.put("serverBean", serverInfoBean.toJSONString());

        String json = jsonObject.toString(SerializerFeature.MapSortField, SerializerFeature.SortField);
        String md5DigestEncode = Md5Util.md5DigestEncode0("#", json, loginProperties.getJwtKey());
        jsonObject.put("sign", md5DigestEncode);

        HttpResponse execute = HttpRequestPost.ofJson(loginProperties.getUrl() + "/inner/registerGame", jsonObject.toString()).execute();
        log.info("向登陆服务器注册: {}", execute.bodyString());
    }

}
