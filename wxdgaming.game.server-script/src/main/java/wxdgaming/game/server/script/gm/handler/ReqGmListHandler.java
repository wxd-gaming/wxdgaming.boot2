package wxdgaming.game.server.script.gm.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.game.message.gm.ReqGmList;

/**
 * gm命令列表
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ReqGmListHandler {

    /** gm命令列表 */
    @ProtoRequest(ReqGmList.class)
    public void reqGmList(ProtoEvent event) {
        ReqGmList message = event.buildMessage();
        UserMapping userMapping = event.bindData();
        Player player = userMapping.player();

    }

}