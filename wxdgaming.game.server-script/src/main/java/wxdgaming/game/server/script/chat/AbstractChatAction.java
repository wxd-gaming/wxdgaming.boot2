package wxdgaming.game.server.script.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wxdgaming.game.message.chat.ChatType;
import wxdgaming.game.message.chat.ReqChatMessage;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.script.tips.TipsService;

/**
 * 聊天接口
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-28 19:32
 **/
@Slf4j
@Component
public abstract class AbstractChatAction {

    @Autowired protected TipsService tipsService;
    @Autowired protected DataCenterService dataCenterService;

    public ChatType chatType() {
        return ChatType.Chat_TYPE_NONE;
    }

    public abstract void chat(Player player, ReqChatMessage req);

}
