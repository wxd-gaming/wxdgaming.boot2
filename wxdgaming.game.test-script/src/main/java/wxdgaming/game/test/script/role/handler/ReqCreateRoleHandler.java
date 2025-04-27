package wxdgaming.game.test.script.role.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.util.ObjectLockUtil;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.module.data.DataCenterService;
import wxdgaming.game.test.script.event.EventBus;
import wxdgaming.game.test.script.event.OnCreateRole;
import wxdgaming.game.test.script.role.PlayerScript;
import wxdgaming.game.test.script.role.message.ReqCreateRole;
import wxdgaming.game.test.script.tips.TipsScript;

import java.util.HashSet;

/**
 * 创建角色
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ReqCreateRoleHandler {

    private final EventBus eventBus;
    private final DataCenterService dataCenterService;
    private final PlayerScript playerScript;
    private final TipsScript tipsScript;

    @Inject
    public ReqCreateRoleHandler(EventBus eventBus, DataCenterService dataCenterService, PlayerScript playerScript, TipsScript tipsScript) {
        this.eventBus = eventBus;
        this.dataCenterService = dataCenterService;
        this.playerScript = playerScript;
        this.tipsScript = tipsScript;
    }

    /** 创建角色 */
    @ProtoRequest
    public void reqCreateRole(SocketSession socketSession, ReqCreateRole req) {

        String account = socketSession.attribute("account");
        Integer sid = socketSession.attribute("sid");

        HashSet<Long> longs = dataCenterService.getAccount2RidsMap().get(sid, account);
        if (longs != null && longs.size() >= 10) {
            /*创建角色错误*/
            log.error("sid={}, account={} 创建角色错误 角色数量超过10个", sid, account);
            this.tipsScript.tips(socketSession, "角色数量超过10个");
            return;
        }

        Player player = null;
        String name = req.getName();
        if (StringUtils.isBlank(name) | name.length() < 2 || name.length() > 12) {
            /*创建角色错误*/
            log.error("sid={}, account={} 创建角色错误 角色名 {} 字符范围不合符", sid, account, name);
            this.tipsScript.tips(socketSession, "角色名长度2-12");
            return;
        }
        ObjectLockUtil.lock("role_" + name);
        try {

            boolean containsKey = dataCenterService.getName2RidMap().containsKey(name);
            if (containsKey) {
                /*创建角色错误*/
                log.error("sid={}, account={} 创建角色错误 角色名 {} 已存在", sid, account, name);
                this.tipsScript.tips(socketSession, "角色名已存在");
                return;
            }

            int sex = req.getSex();
            int job = req.getJob();
            player = new Player();
            player.setUid(dataCenterService.getHexid().newId());
            player.setAccount(account);
            player.setSid(sid);
            player.setName(name);
            player.setSex(sex);
            player.setJob(job);
            log.info("sid={}, account={}, 创建角色：{}", sid, account, player);
            dataCenterService.getPgsqlService().getCacheService().cache(Player.class).put(player.getUid(), player);
            dataCenterService.getAccount2RidsMap().computeIfAbsent(sid, account, l -> new HashSet<>()).add(player.getUid());
            dataCenterService.getName2RidMap().put(name, player.getUid());
            dataCenterService.getRid2NameMap().put(player.getUid(), name);
        } finally {
            ObjectLockUtil.unlock("role_" + name);
        }
        eventBus.post(OnCreateRole.class, player);
        playerScript.sendPlayerList(socketSession, sid, account);
    }

}