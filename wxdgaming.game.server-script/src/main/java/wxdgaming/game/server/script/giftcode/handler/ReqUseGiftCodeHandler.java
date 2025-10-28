package wxdgaming.game.server.script.giftcode.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.game.message.giftcode.ReqUseGiftCode;
import wxdgaming.game.server.bean.UserMapping;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.giftcode.GiftCodeService;

/**
 * 请求使用GiftCode
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ReqUseGiftCodeHandler {

    final GiftCodeService giftCodeService;

    public ReqUseGiftCodeHandler(GiftCodeService giftCodeService) {
        this.giftCodeService = giftCodeService;
    }

    /** 请求使用GiftCode */
    @ProtoRequest(ReqUseGiftCode.class)
    public void reqUseGiftCode(ProtoEvent event) {
        ReqUseGiftCode message = event.buildMessage();
        UserMapping userMapping = event.bindData();
        Player player = userMapping.player();
        giftCodeService.use(player, message.getGiftCode());
    }

}