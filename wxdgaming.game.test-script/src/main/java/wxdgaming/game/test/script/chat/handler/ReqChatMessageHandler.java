package wxdgaming.game.test.script.chat.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.test.script.chat.ChatService;
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

    private final ChatService chatService;

    @Inject
    public ReqChatMessageHandler(ChatService chatService) {
        this.chatService = chatService;
    }

    /** 请求聊天 */
    @ProtoRequest
    public void reqChatMessage(SocketSession socketSession, ReqChatMessage req) {
        chatService.chatHandler(req.getType()).chat(socketSession, req);
    }

}