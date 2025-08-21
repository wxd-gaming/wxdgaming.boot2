package wxdgaming.game.server.script.chat.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.message.chat.ChatType;
import wxdgaming.game.message.chat.ReqChatMessage;
import wxdgaming.game.message.chat.ResChatMessage;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.chat.AbstractChatAction;

/**
 * 聊天接口
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-28 19:32
 **/
@Slf4j
@Component
public class PrivateChatAction extends AbstractChatAction {

    public ChatType chatType() {
        return ChatType.Chat_TYPE_Private;
    }

    public void chat(Player player, ReqChatMessage req) {
        Player targetPlayer = dataCenterService.getPlayer(req.getTargetId());
        if (targetPlayer == null) {
            tipsService.tips(player, "目标玩家不存在");
            return;
        }
        if (!targetPlayer.checkOnline()) {
            tipsService.tips(player, "目标玩家不在线");
            return;
        }
        ResChatMessage res = new ResChatMessage();
        res.setType(req.getType());
        res.setContent(req.getContent());
        res.setParams(req.getParams());
        res.setSenderId(player.getUid());
        res.setSenderName(player.getName());
        player.write(res);
        targetPlayer.write(res);
    }

}
