package wxdgaming.game.robot.script.giftcode.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.game.message.giftcode.ResUseGiftCode;

/**
 * 响应使用GiftCode
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ResUseGiftCodeHandler {

    /** 响应使用GiftCode */
    @ProtoRequest(ResUseGiftCode.class)
    public void resUseGiftCode(ProtoEvent event) {
        ResUseGiftCode message = event.buildMessage();
        log.info("礼包码使用成功：{}", message);
    }

}