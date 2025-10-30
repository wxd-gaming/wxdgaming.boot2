package wxdgaming.game.server.script;

import com.alibaba.fastjson.JSONObject;
import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.event.InitEvent;
import wxdgaming.boot2.core.executor.ExecutorLog;
import wxdgaming.boot2.core.executor.ExecutorWith;
import wxdgaming.boot2.core.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.net.httpclient5.HttpRequestPost;
import wxdgaming.boot2.starter.net.httpclient5.HttpResponse;
import wxdgaming.boot2.starter.net.server.SocketServer;
import wxdgaming.boot2.starter.scheduled.ann.Scheduled;
import wxdgaming.game.authority.SignUtil;
import wxdgaming.game.common.bean.ban.BanVO;
import wxdgaming.game.common.bean.login.ConnectLoginProperties;
import wxdgaming.game.common.global.GlobalDataService;
import wxdgaming.game.common.slog.SlogService;
import wxdgaming.game.login.bean.ServerInfoDTO;
import wxdgaming.game.server.GameServerProperties;
import wxdgaming.game.server.bean.global.GlobalDataConst;
import wxdgaming.game.server.bean.global.impl.ServerData;
import wxdgaming.game.server.bean.slog.OnlineRecord;
import wxdgaming.game.server.module.drive.PlayerDriveService;

import java.util.HashMap;
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
public class GameTimerScript extends HoldApplicationContext {

    @Value("${server.port}")
    int webPort;
    final SocketServer socketServer;
    final GameServerProperties gameServerProperties;
    final ConnectLoginProperties connectLoginProperties;
    final PlayerDriveService playerDriveService;
    final GlobalDataService globalDataService;
    final SlogService slogService;

    public GameTimerScript(SocketServer socketServer,
                           GameServerProperties gameServerProperties,
                           ConnectLoginProperties connectLoginProperties,
                           PlayerDriveService playerDriveService,
                           GlobalDataService globalDataService,
                           SlogService slogService) {
        this.socketServer = socketServer;
        this.gameServerProperties = gameServerProperties;
        this.connectLoginProperties = connectLoginProperties;
        this.playerDriveService = playerDriveService;
        this.globalDataService = globalDataService;
        this.slogService = slogService;
    }

    @EventListener
    public void init(InitEvent event) {
        remoteBanList();
    }

    /** 向登陆服务器注册 */
    @Scheduled(value = "*/5")
    @ExecutorWith(useVirtualThread = true)
    @ExecutorLog(logTime = 100)
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

    /** 向登陆服务器注册 */
    @Scheduled(value = "0 * * * ?")
    @ExecutorWith(useVirtualThread = true)
    @ExecutorLog(logTime = 100)
    public void recordOnlineSlog() {
        ServerData serverData = globalDataService.get(GlobalDataConst.SERVERDATA);
        OnlineRecord onlineRecord = serverData.getOnlineRecord();
        if (!MyClock.isSameDay(onlineRecord.getOnlineSlogTime())) {
            if (onlineRecord.getOnlineSlogTime() > 0) {
                saveOnlineSlog(onlineRecord);
            }
            onlineRecord.setOnlineSlogTime(MyClock.dayMinTime());
            onlineRecord.setHourOnlineMap(new HashMap<>());
        }

        int onlineSize = playerDriveService.onlineSize();
        int hour = MyClock.getHour();
        String key = "h" + hour;
        Integer oldSize = onlineRecord.getHourOnlineMap().get(key);
        if (oldSize == null || oldSize < onlineSize) {
            /*存储最高值*/
            onlineRecord.getHourOnlineMap().put(key, onlineSize);
        }
        /*记录当前值*/
        onlineRecord.setOnlineSize(onlineSize);
        saveOnlineSlog(onlineRecord);
    }

    private void saveOnlineSlog(OnlineRecord onlineRecord) {
        String yyyyMMdd = MyClock.formatDate("yyyyMMdd", onlineRecord.getOnlineSlogTime());
        long uid = Long.parseLong(yyyyMMdd) * 1000000 + gameServerProperties.getSid();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sid", gameServerProperties.getSid());
        jsonObject.put("onlineSize", onlineRecord.getOnlineSize());
        jsonObject.putAll(onlineRecord.getHourOnlineMap());
        slogService.updateLog(uid, onlineRecord.getOnlineSlogTime(), "onlineslog", jsonObject);
    }

    /** 向登陆服务器注册 */
    @Scheduled(value = "0 */5 * * ?")
    @ExecutorWith(useVirtualThread = true)
    @ExecutorLog(logTime = 100)
    public void remoteBanList() {
        try {
            String json = "{}";
            String sign = SignUtil.signByJsonKey(json, connectLoginProperties.getJwtKey());

            String url = connectLoginProperties.getUrl() + "/inner/game/banList";
            HttpResponse execute = HttpRequestPost.ofJson(url, json)
                    .addHeader(HttpHeaderNames.AUTHORIZATION.toString(), sign)
                    .execute();
            if (!execute.isSuccess()) {
                log.error("访问登陆服务器失败{}", url);
                return;
            }
            log.debug("获取封禁列表: {}", execute.getCode());
            RunResult runResult = execute.bodyRunResult();
            if (runResult.isOk()) {
                String string = runResult.getString("data");
                List<String> data = FastJsonUtil.parseArray(string, String.class);
                for (String datum : data) {
                    globalDataService.editBanVOTable(FastJsonUtil.parse(datum, BanVO.class));
                }
            }
        } catch (Exception e) {
            log.error("请求登陆服务封禁列表异常", e);
        }
    }

}
