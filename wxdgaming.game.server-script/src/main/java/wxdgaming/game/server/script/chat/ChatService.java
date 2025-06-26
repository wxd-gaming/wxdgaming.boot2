package wxdgaming.game.server.script.chat;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.game.message.chat.ChatType;

import java.util.HashMap;

/**
 * 聊天模块
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-28 19:30
 **/
@Slf4j
@Singleton
public class ChatService extends HoldRunApplication {

    HashMap<ChatType, AbstractChatAction> chatHandlerMap = new HashMap<>();

    @Init
    public void init() {
        HashMap<ChatType, AbstractChatAction> tmpChatHandlerMap = new HashMap<>();
        runApplication.classWithSuper(AbstractChatAction.class)
                .forEach(abstractChatAction -> {
                    AbstractChatAction put = tmpChatHandlerMap.put(abstractChatAction.chatType(), abstractChatAction);
                    AssertUtil.assertTrue(put == null, "重复注册类型：" + abstractChatAction.chatType());
                });
        this.chatHandlerMap = tmpChatHandlerMap;
    }

    public AbstractChatAction chatHandler(ChatType chatType) {
        return chatHandlerMap.getOrDefault(chatType, chatHandlerMap.get(ChatType.Chat_TYPE_NONE));
    }

}
