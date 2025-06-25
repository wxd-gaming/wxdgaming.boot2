package wxdgaming.game.server.module.timer;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.core.executor.ExecutorWith;
import wxdgaming.boot2.core.util.Md5Util;
import wxdgaming.boot2.starter.net.httpclient.HttpBuilder;
import wxdgaming.boot2.starter.net.httpclient.PostText;
import wxdgaming.boot2.starter.net.httpclient.Response;
import wxdgaming.boot2.starter.net.server.SocketServer;
import wxdgaming.boot2.starter.scheduled.ann.Scheduled;
import wxdgaming.game.login.bean.info.InnerServerInfoBean;
import wxdgaming.game.login.LoginConfig;
import wxdgaming.game.server.module.drive.PlayerDriveService;

import java.util.List;

/**
 * 游戏进程的定时器服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-11 20:22
 **/
@Slf4j
@Singleton
public class GameTimerService {

    final SocketServer socketServer;
    final LoginConfig loginConfig;
    final PlayerDriveService playerDriveService;

    @Inject
    public GameTimerService(SocketServer socketServer, LoginConfig loginConfig, PlayerDriveService playerDriveService) {
        this.socketServer = socketServer;
        this.loginConfig = loginConfig;
        this.playerDriveService = playerDriveService;
    }


    /** 向登陆服务器注册 */
    @Scheduled(value = "*/5", async = true)
    @ExecutorWith(useVirtualThread = true)
    public void registerLoginServer() {

        InnerServerInfoBean serverInfoBean = new InnerServerInfoBean();
        serverInfoBean.setServerId(BootConfig.getIns().sid());
        serverInfoBean.setMainId(BootConfig.getIns().sid());
        serverInfoBean.setGid(BootConfig.getIns().gid());
        serverInfoBean.setName(BootConfig.getIns().sname());
        serverInfoBean.setPort(socketServer.getConfig().getPort());
        serverInfoBean.setHttpPort(socketServer.getConfig().getPort());

        serverInfoBean.setMaxOnlineSize(loginConfig.getMaxOnlineSize());
        serverInfoBean.setOnlineSize(playerDriveService.onlineSize());


        JSONObject jsonObject = MapOf.newJSONObject();
        jsonObject.put("sidList", List.of(BootConfig.getIns().sid()));
        jsonObject.put("sid", BootConfig.getIns().sid());
        jsonObject.put("serverBean", serverInfoBean.toJSONString());

        String json = jsonObject.toString(SerializerFeature.MapSortField, SerializerFeature.SortField);
        String md5DigestEncode = Md5Util.md5DigestEncode0("#", json, loginConfig.getJwtKey());
        jsonObject.put("sign", md5DigestEncode);

        Response<PostText> request = HttpBuilder.postJson(loginConfig.getUrl() + "/inner/registerGame", jsonObject.toString()).request();
        log.info("向登陆服务器注册: {}", request.bodyString());
    }

}
