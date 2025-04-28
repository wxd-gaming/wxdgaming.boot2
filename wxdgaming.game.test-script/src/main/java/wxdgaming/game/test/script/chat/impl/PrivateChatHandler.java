package wxdgaming.game.test.script.chat.impl;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.game.test.script.chat.ChatHandler;
import wxdgaming.game.test.script.chat.message.ChatType;
import wxdgaming.game.test.script.chat.message.ReqChatMessage;

/**
 * 聊天接口
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-28 19:32
 **/
@Slf4j
@Singleton
public class PrivateChatHandler extends ChatHandler {

    public ChatType chatType() {
        return ChatType.Chat_TYPE_Private;
    }

    public void chat(SocketSession socketSession, ReqChatMessage req) {

    }

}
