package wxdgaming.game.server.script.role.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.util.ObjectLockUtil;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.role.ReqCreateRole;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.role.RoleEntity;
import wxdgaming.game.server.event.OnCreateRole;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.script.role.PlayerService;
import wxdgaming.game.server.script.tips.TipsService;

import java.util.HashSet;

/**
 * 创建角色
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ReqCreateRoleHandler extends HoldRunApplication {

    private final DataCenterService dataCenterService;
    private final PlayerService playerService;
    private final TipsService tipsService;

    @Inject
    public ReqCreateRoleHandler(DataCenterService dataCenterService, PlayerService playerService, TipsService tipsService) {
        this.dataCenterService = dataCenterService;
        this.playerService = playerService;
        this.tipsService = tipsService;
    }

    /** 创建角色 */
    @ProtoRequest
    public void reqCreateRole(SocketSession socketSession, ReqCreateRole req) {

        Integer sid = socketSession.getBindData().getInteger("sid");
        String account = socketSession.getBindData().getString("account");

        HashSet<Long> longs = dataCenterService.getAccount2RidsMap().get(sid, account);
        if (longs != null && longs.size() >= 10) {
            /*创建角色错误*/
            log.error("sid={}, account={} 创建角色错误 角色数量超过10个", sid, account);
            this.tipsService.tips(socketSession, "角色数量超过10个");
            return;
        }

        Player player = null;
        String name = req.getName();
        if (StringUtils.isBlank(name) | name.length() < 2 || name.length() > 12) {
            /*创建角色错误*/
            log.error("sid={}, account={} 创建角色错误 角色名 {} 字符范围不合符", sid, account, name);
            this.tipsService.tips(socketSession, "角色名长度2-12");
            return;
        }

        boolean contains = dataCenterService.getKeywordsMapping().contains(name);
        if (contains) {
            /*触发敏感词库*/
            log.error("sid={}, account={} 创建角色错误 角色名 {} 有敏感字", sid, account, name);
            this.tipsService.tips(socketSession, "角色名不合符规范");
            return;
        }
        ObjectLockUtil.lock("role_" + name);
        try {

            boolean containsKey = dataCenterService.getName2RidMap().containsKey(name);
            if (containsKey) {
                /*创建角色错误*/
                log.error("sid={}, account={} 创建角色错误 角色名 {} 已存在", sid, account, name);
                this.tipsService.tips(socketSession, "角色名已存在");
                return;
            }

            int sex = req.getSex();
            int job = req.getJob();
            player = new Player();
            player.setUid(dataCenterService.getHexid().newId());
            player.setAccount(account);
            player.setSid(sid);
            player.setName(name);
            player.setLevel(1);
            player.setHp(100);
            player.setMp(100);
            player.setSex(sex);
            player.setJob(job);
            log.info("sid={}, account={}, 创建角色：{}", sid, account, player);
            dataCenterService.getPgsqlService().getCacheService()
                    .cache(RoleEntity.class)
                    .put(player.getUid(), new RoleEntity().setUid(player.getUid()).setPlayer(player));
            dataCenterService.getAccount2RidsMap().computeIfAbsent(sid, account, l -> new HashSet<>()).add(player.getUid());
            dataCenterService.getName2RidMap().put(name, player.getUid());
            dataCenterService.getRid2NameMap().put(player.getUid(), name);
        } finally {
            ObjectLockUtil.unlock("role_" + name);
        }
        runApplication.executeMethodWithAnnotatedException(OnCreateRole.class, player);
        playerService.sendPlayerList(socketSession, sid, account);
    }

}