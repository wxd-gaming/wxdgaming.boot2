package wxdgaming.game.test.script.chat.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.test.script.chat.ChatScript;
import wxdgaming.game.test.script.chat.message.ReqChatMessage;

/**
 * 请求聊天
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ReqChatMessageHandler {

    private final ChatScript chatScript;

    @Inject
    public ReqChatMessageHandler(ChatScript chatScript) {
        this.chatScript = chatScript;
    }

    /** 请求聊天 */
    @ProtoRequest
    public void reqChatMessage(SocketSession socketSession, ReqChatMessage req) {
        chatScript.chatHandler(req.getType()).chat(socketSession, req);
    }

}