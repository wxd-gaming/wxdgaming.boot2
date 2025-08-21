package wxdgaming.game.server.script.role.handler;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.util.SingletonLockUtil;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.basic.slog.SlogService;
import wxdgaming.game.message.role.ReqCreateRole;
import wxdgaming.game.server.bean.ClientSessionMapping;
import wxdgaming.game.server.bean.InnerForwardEvent;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.role.RoleEntity;
import wxdgaming.game.server.event.OnCreateRole;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.script.role.PlayerService;
import wxdgaming.game.server.script.role.log.RoleRegisterLog;
import wxdgaming.game.server.script.tips.TipsService;

import java.util.HashSet;

/**
 * 创建角色
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ReqCreateRoleHandler extends HoldApplicationContext {

    private final DataCenterService dataCenterService;
    private final PlayerService playerService;
    private final TipsService tipsService;
    final SlogService slogService;

    public ReqCreateRoleHandler(DataCenterService dataCenterService, PlayerService playerService, TipsService tipsService, SlogService slogService) {
        this.dataCenterService = dataCenterService;
        this.playerService = playerService;
        this.tipsService = tipsService;
        this.slogService = slogService;
    }

    /** 创建角色 */
    @ProtoRequest(ReqCreateRole.class)
    public void reqCreateRole(InnerForwardEvent event) {
        SocketSession socketSession = event.getSocketSession();
        ReqCreateRole req = event.buildMessage();
        ClientSessionMapping clientSessionMapping = event.getClientSessionMapping();

        long clientSessionId = clientSessionMapping.getClientSessionId();
        log.info("创建角色请求:{}, clientSession={}", req, clientSessionMapping);
        Integer sid = clientSessionMapping.getSid();
        String account = clientSessionMapping.getAccount();

        HashSet<Long> longs = dataCenterService.getAccount2RidsMap().get(sid, account);
        if (longs != null && longs.size() >= 10) {
            /*创建角色错误*/
            log.error("sid={}, account={} 创建角色错误 角色数量超过10个", sid, account);
            this.tipsService.tips(socketSession, clientSessionId, "角色数量超过10个");
            return;
        }

        Player player = null;
        String name = req.getName();
        if (StringUtils.isBlank(name) | name.length() < 2 || name.length() > 12) {
            /*创建角色错误*/
            log.error("sid={}, account={} 创建角色错误 角色名 {} 字符范围不合符", sid, account, name);
            this.tipsService.tips(socketSession, clientSessionId, "角色名长度2-12");
            return;
        }

        boolean contains = dataCenterService.getKeywordsMapping().contains(name);
        if (contains) {
            /*触发敏感词库*/
            log.error("sid={}, account={} 创建角色错误 角色名 {} 有敏感字", sid, account, name);
            this.tipsService.tips(socketSession, clientSessionId, "角色名不合符规范");
            return;
        }
        SingletonLockUtil.lock("role_" + name);
        try {

            boolean containsKey = dataCenterService.getName2RidMap().containsKey(name);
            if (containsKey) {
                /*创建角色错误*/
                log.error("sid={}, account={} 创建角色错误 角色名 {} 已存在", sid, account, name);
                this.tipsService.tips(socketSession, clientSessionId, "角色名已存在");
                return;
            }

            int sex = req.getSex();
            int job = req.getJob();
            player = new Player();
            player.setUid(dataCenterService.getHexid().newId());
            player.setAccount(account);
            player.setAppId(clientSessionMapping.getAppId());
            player.setPlatform(clientSessionMapping.getPlatform());
            player.setPlatformUserId(clientSessionMapping.getPlatformUserId());
            player.setSid(sid);
            player.setName(name);
            player.setLevel(1);
            player.setHp(100);
            player.setMp(100);
            player.setSex(sex);
            player.setJob(job);
            log.info("sid={}, account={}, 创建角色：{}", sid, account, player);
            RoleEntity roleEntity = new RoleEntity().setUid(player.getUid()).setPlayer(player);
            roleEntity.setNewEntity(true);
            dataCenterService.putCache(roleEntity);
            dataCenterService.getAccount2RidsMap().computeIfAbsent(sid, account, l -> new HashSet<>()).add(player.getUid());
            dataCenterService.getName2RidMap().put(name, player.getUid());
            dataCenterService.getRid2NameMap().put(player.getUid(), name);

            RoleRegisterLog roleLoginLog = new RoleRegisterLog(player, clientSessionMapping.getClientIp(), JSON.toJSONString(clientSessionMapping.getClientParams()));
            slogService.addLog(roleLoginLog);

        } finally {
            SingletonLockUtil.unlock("role_" + name);
        }
        applicationContextProvider.executeMethodWithAnnotatedException(OnCreateRole.class, player);
        playerService.sendPlayerList(socketSession, clientSessionId, sid, account);
    }

}