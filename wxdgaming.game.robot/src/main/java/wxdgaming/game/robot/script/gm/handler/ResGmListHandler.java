package wxdgaming.game.robot.script.gm.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.game.message.gm.ResGmList;

/**
 * gm命令列表
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ResGmListHandler {

    /** gm命令列表 */
    @ProtoRequest(ResGmList.class)
    public void resGmList(ProtoEvent event) {
        ResGmList message = event.buildMessage();
        
    }

}