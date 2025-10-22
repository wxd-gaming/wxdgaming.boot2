package wxdgaming.game.server.script.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.event.InitEvent;
import wxdgaming.game.message.chat.ChatType;

import java.util.HashMap;
import java.util.Map;

/**
 * 聊天模块
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-28 19:30
 **/
@Slf4j
@Service
public class ChatService extends HoldApplicationContext {

    Map<ChatType, AbstractChatAction> chatHandlerMap = new HashMap<>();

    @EventListener
    public void init(InitEvent initEvent) {
        this.chatHandlerMap = applicationContextProvider.toMap(AbstractChatAction.class, AbstractChatAction::chatType);
    }

    public AbstractChatAction chatHandler(ChatType chatType) {
        return chatHandlerMap.getOrDefault(chatType, chatHandlerMap.get(ChatType.Chat_TYPE_NONE));
    }

}
