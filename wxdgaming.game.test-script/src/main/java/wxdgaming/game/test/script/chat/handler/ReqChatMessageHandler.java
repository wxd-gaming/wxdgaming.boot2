package wxdgaming.game.test.script.chat.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.test.bean.global.GlobalDataType;
import wxdgaming.game.test.bean.global.impl.YunyingData;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.module.data.GlobalDataService;
import wxdgaming.game.test.script.chat.ChatService;
import wxdgaming.game.test.script.chat.message.ReqChatMessage;
import wxdgaming.game.test.script.gm.GmService;
import wxdgaming.game.test.script.tips.TipsService;

/**
 * 请求聊天
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ReqChatMessageHandler {

    private final GlobalDataService globalDataService;
    private final ChatService chatService;
    private final TipsService tipsService;
    private final GmService gmService;

    @Inject
    public ReqChatMessageHandler(GlobalDataService globalDataService, ChatService chatService, TipsService tipsService, GmService gmService) {
        this.globalDataService = globalDataService;
        this.chatService = chatService;
        this.tipsService = tipsService;
        this.gmService = gmService;
    }

    /** 请求聊天 */
    @ProtoRequest
    public void reqChatMessage(SocketSession socketSession, ReqChatMessage req) {
        Player player = socketSession.attribute("player");
        String content = req.getContent();
        if (content.startsWith("@gm")) {
            YunyingData yunyingData = globalDataService.get(GlobalDataType.YunyingData);
            if (BootConfig.getIns().isDebug()
                || yunyingData.getGmAccountSet().contains(player.getAccount())
                || yunyingData.getGmPlayerIdSet().contains(player.getUid())) {
                gmService.doGm(player, content.substring(3).trim().split(" "));
                return;
            }
        }
        if (content.length() > 120) {
            tipsService.tips(socketSession, "消息长度不能超过100");
            return;
        }
        if (player.getLevel() < 10) {
            tipsService.tips(socketSession, "等级不足10级");
            return;
        }
        chatService.chatHandler(req.getType()).chat(socketSession, player, req);
    }

}