package wxdgaming.game.robot.script.role.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.game.message.role.ResCreateRole;

/**
 * 创建角色响应
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ResCreateRoleHandler {

    /** 创建角色响应 */
    @ProtoRequest(ResCreateRole.class)
    public void resCreateRole(ProtoEvent event) {

    }

}