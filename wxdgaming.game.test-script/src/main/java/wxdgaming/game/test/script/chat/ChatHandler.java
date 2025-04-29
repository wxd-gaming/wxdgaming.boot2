package wxdgaming.game.test.script.chat;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.game.test.script.chat.message.ChatType;
import wxdgaming.game.test.script.chat.message.ReqChatMessage;
import wxdgaming.game.test.script.tips.TipsService;

/**
 * 聊天接口
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-28 19:32
 **/
@Slf4j
@Singleton
public class ChatHandler {

    protected TipsService tipsService;

    @Init
    public void init(TipsService tipsService) {
        this.tipsService = tipsService;
    }

    public ChatType chatType() {
        return ChatType.Chat_TYPE_NONE;
    }

    public void chat(SocketSession socketSession, ReqChatMessage req) {
        tipsService.tips(socketSession, "暂未实现");
    }

}
