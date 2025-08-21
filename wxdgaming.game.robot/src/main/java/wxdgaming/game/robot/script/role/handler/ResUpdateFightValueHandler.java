package wxdgaming.game.robot.script.role.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.game.message.role.ResUpdateFightValue;

/**
 * 更新战斗力
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ResUpdateFightValueHandler {

    /** 更新战斗力 */
    @ProtoRequest(ResUpdateFightValue.class)
    public void resUpdateFightValue(ProtoEvent event) {

    }

}