package wxdgaming.game.test.script.chat;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.game.test.script.chat.message.ChatType;

import java.util.HashMap;

/**
 * 聊天模块
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-28 19:30
 **/
@Slf4j
@Singleton
public class ChatScript extends HoldRunApplication {

    HashMap<ChatType, ChatHandler> chatHandlerMap = new HashMap<>();

    @Init
    public void init() {
        HashMap<ChatType, ChatHandler> tmpChatHandlerMap = new HashMap<>();
        runApplication.classWithSuper(ChatHandler.class)
                .forEach(chatHandler -> {
                    ChatHandler put = tmpChatHandlerMap.put(chatHandler.chatType(), chatHandler);
                    AssertUtil.assertTrue(put == null, "重复注册类型：" + chatHandler.chatType());
                });
        this.chatHandlerMap = tmpChatHandlerMap;
    }

    public ChatHandler chatHandler(ChatType chatType) {
        return chatHandlerMap.getOrDefault(chatType, chatHandlerMap.get(ChatType.Chat_TYPE_NONE));
    }

}
