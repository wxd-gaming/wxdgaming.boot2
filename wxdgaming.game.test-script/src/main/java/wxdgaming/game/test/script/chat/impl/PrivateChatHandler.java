package wxdgaming.game.test.script.chat.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.module.data.DataCenterService;
import wxdgaming.game.test.script.chat.ChatHandler;
import wxdgaming.game.test.script.chat.message.ChatType;
import wxdgaming.game.test.script.chat.message.ReqChatMessage;
import wxdgaming.game.test.script.chat.message.ResChatMessage;

/**
 * 聊天接口
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-28 19:32
 **/
@Slf4j
@Singleton
public class PrivateChatHandler extends ChatHandler {


    private final DataCenterService dataCenterService;

    @Inject
    public PrivateChatHandler(DataCenterService dataCenterService) {
        this.dataCenterService = dataCenterService;
    }

    public ChatType chatType() {
        return ChatType.Chat_TYPE_Private;
    }

    public void chat(SocketSession socketSession, Player player, ReqChatMessage req) {
        Player targetPlayer = dataCenterService.player(req.getTargetId());
        if (targetPlayer == null) {
            tipsService.tips(socketSession, "目标玩家不存在");
            return;
        }
        if (!targetPlayer.checkOnline()) {
            tipsService.tips(socketSession, "目标玩家不在线");
            return;
        }
        ResChatMessage res = new ResChatMessage();
        res.setType(req.getType());
        res.setContent(req.getContent());
        res.setParams(req.getParams());
        res.setSenderId(player.getUid());
        res.setSenderName(player.getName());
        targetPlayer.write(res);
    }

}
