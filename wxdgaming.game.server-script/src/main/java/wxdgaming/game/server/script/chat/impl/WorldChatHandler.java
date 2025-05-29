package wxdgaming.game.server.script.chat.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.message.chat.ChatType;
import wxdgaming.game.message.chat.ReqChatMessage;
import wxdgaming.game.message.chat.ResChatMessage;
import wxdgaming.game.message.inner.ServiceType;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.script.chat.ChatHandler;
import wxdgaming.game.server.script.inner.InnerService;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天接口
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-28 19:32
 **/
@Slf4j
@Singleton
public class WorldChatHandler extends ChatHandler {

    private final InnerService innerService;
    private final DataCenterService dataCenterService;

    @Inject
    public WorldChatHandler(InnerService innerService, DataCenterService dataCenterService) {
        this.innerService = innerService;
        this.dataCenterService = dataCenterService;
    }

    public ChatType chatType() {
        return ChatType.Chat_TYPE_World;
    }

    public void chat(Player player, ReqChatMessage req) {
        ResChatMessage res = new ResChatMessage();
        res.setType(req.getType());
        res.setContent(req.getContent());
        res.setParams(req.getParams());
        res.setSenderId(player.getUid());
        res.setSenderName(player.getName());
        ConcurrentHashMap<Long, Long> onlinePlayerGroup = this.dataCenterService.getOnlinePlayerGroup();
        innerService.forwardMessage(ServiceType.GATEWAY, onlinePlayerGroup.values(), res);
    }

}
