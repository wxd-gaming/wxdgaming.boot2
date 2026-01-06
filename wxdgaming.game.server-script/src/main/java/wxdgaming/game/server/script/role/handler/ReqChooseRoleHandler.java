package wxdgaming.game.server.script.role.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.core.executor.ExecutorEvent;
import wxdgaming.boot2.core.executor.ExecutorWith;
import wxdgaming.boot2.core.executor.ThreadStopWatch;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.httpclient5.HttpRequestPost;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.game.authority.SignUtil;
import wxdgaming.game.common.bean.login.ConnectLoginProperties;
import wxdgaming.game.common.slog.SlogService;
import wxdgaming.game.message.role.ReqChooseRole;
import wxdgaming.game.message.role.ResChooseRole;
import wxdgaming.game.server.GameServerProperties;
import wxdgaming.game.server.bean.UserMapping;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.entity.role.PlayerSnap;
import wxdgaming.game.server.event.EventConst;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.module.data.GlobalDbDataCenterService;
import wxdgaming.game.server.module.drive.PlayerDriveService;
import wxdgaming.game.server.script.role.slog.RoleLoginSlog;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * 选择角色
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ReqChooseRoleHandler extends HoldApplicationContext {

    final GameServerProperties gameServerProperties;
    final DataCenterService dataCenterService;
    final GlobalDbDataCenterService globalDbDataCenterService;
    final PlayerDriveService playerDriveService;
    final SlogService slogService;
    final ConnectLoginProperties connectLoginProperties;

    public ReqChooseRoleHandler(GameServerProperties gameServerProperties, DataCenterService dataCenterService,
                                GlobalDbDataCenterService globalDbDataCenterService,
                                PlayerDriveService playerDriveService, SlogService slogService, ConnectLoginProperties connectLoginProperties) {
        this.gameServerProperties = gameServerProperties;
        this.dataCenterService = dataCenterService;
        this.globalDbDataCenterService = globalDbDataCenterService;
        this.playerDriveService = playerDriveService;
        this.slogService = slogService;
        this.connectLoginProperties = connectLoginProperties;
    }

    /** 选择角色 */
    @ProtoRequest(ReqChooseRole.class)
    @ExecutorWith(queueName = "login")
    public void reqChooseRole(ProtoEvent event) {
        SocketSession socketSession = event.getSocketSession();
        ReqChooseRole req = event.buildMessage();
        UserMapping userMapping = event.bindData();
        long rid = req.getRid();
        log.info("选择角色请求:{}, {} clientSession={}", req, userMapping, socketSession);
        Integer sid = userMapping.getSid();
        String account = userMapping.getAccount();
        HashSet<Long> longs = dataCenterService.getAccount2RidsMap().get(sid, account);
        if (longs == null || !longs.contains(rid)) {
            /*选择角色错误*/
            log.error("sid={}, account={} 选择角色错误 角色id不存在：{}", sid, account, rid);
            return;
        }

        Player player = dataCenterService.getPlayer(rid);
        playerDriveService.executor(player, new ExecutorEvent() {

            @Override public void onEvent() throws Exception {
                if (userMapping.getRid() > 0 && userMapping.getRid() != player.getUid()) {
                    /*角色切换*/
                    log.info("sid={}, account={} 角色切换 rid={} -> {}", sid, account, userMapping.getRid(), player.getUid());
                    applicationContextProvider.postEventIgnoreException(new EventConst.LogoutPlayerEvent(player));
                }

                player.setClientData(new ArrayList<>(userMapping.getClientParams()));

                PlayerSnap playerSnap = globalDbDataCenterService.playerSnap(player.getUid());
                player.buildPlayerSnap(playerSnap);
                player.setUserMapping(userMapping);
                userMapping.setRid(player.getUid());

                dataCenterService.getOnlinePlayers().add(socketSession);

                /*绑定*/
                log.info("sid={}, {} 触发登录之前校验事件", sid, player);
                ThreadStopWatch.startIfPresent("LoginBeforePlayerEvent");
                applicationContextProvider.postEvent(new EventConst.LoginBeforePlayerEvent(player));
                ThreadStopWatch.stopIfPresent();
                ThreadStopWatch.startIfPresent("ResChooseRole");
                ResChooseRole resChooseRole = new ResChooseRole();
                resChooseRole.setRid(rid);
                socketSession.write(resChooseRole);
                ThreadStopWatch.stopIfPresent();
                log.info("sid={}, {} 触发登录事件", sid, player);
                ThreadStopWatch.startIfPresent("LoginPlayerEvent");
                applicationContextProvider.postEventIgnoreException(new EventConst.LoginPlayerEvent(player));
                ThreadStopWatch.stopIfPresent();
                log.info("sid={}, {} 选择角色成功", sid, player);

                RoleLoginSlog roleLoginLog = new RoleLoginSlog(player, userMapping.getClientIp(), JSON.toJSONString(userMapping.getClientParams()));
                slogService.pushLog(roleLoginLog);

                reportPlayer2Login(player, 0);
            }
        });
    }

    public void playerHeartMinuteEvent(EventConst.MapNpcHeartMinuteEvent event) {
        reportPlayer2Login((Player) event.mapNpc(), event.minute());
    }

    public void reportPlayer2Login(Player player, int minute) {
        String url = connectLoginProperties.getUrl();
        url = url + "/inner/game/lastLogin";
        JSONObject jsonObject = MapOf.newJSONObject();
        jsonObject.put("account", player.getAccount());
        jsonObject.put("sid", gameServerProperties.getSid());
        JSONObject roleInfo = new JSONObject()
                .fluentPut("rid", player.getUid())
                .fluentPut("name", player.getName())
                .fluentPut("level", player.getLevel());
        jsonObject.put("roleInfo", roleInfo);

        String sign = SignUtil.signByJsonKey(jsonObject, connectLoginProperties.getJwtKey());

        HttpRequestPost.of(url).setParamsJson(jsonObject)
                .addHeader(HttpHeaderNames.AUTHORIZATION.toString(), sign)
                .executeAsync()
                .subscribe(
                        response -> {
                            log.info("上报角色登录信息：{} -> {}", jsonObject, response);
                        },
                        throwable -> {
                            log.info("上报角色 {} 登录信息", jsonObject, throwable);
                        }
                );
    }

}