package wxdgaming.game.robot.script.cdkey.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.game.message.cdkey.ResUseCdKey;

/**
 * 响应使用cdkey
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ResUseCdKeyHandler {

    /** 响应使用cdkey */
    @ProtoRequest(ResUseCdKey.class)
    public void resUseCdKey(ProtoEvent event) {
        ResUseCdKey req = event.buildMessage();
    }

}