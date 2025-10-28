package run;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import wxdgaming.boot2.starter.net.pojo.ProtoBuf2Pojo;

/**
 * protobuf篡改
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-05-27 13:15
 **/
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProtoTest {

    // @Test
    // @Order(1)
    // public void buildProtoBuf() {
    //     ProtoBuf2Pojo.actionProtoFile("src/main/java", "../wxdgaming.game.test-script/src/main/proto");
    // }

    @Test
    @Order(2)
    public void buildProtoHandler() {
        ProtoBuf2Pojo.createMapping(
                "src/main/java",
                "wxdgaming.game.robot.script",
                "Res",
                "wxdgaming.game.message",
                null,
                null,
                () -> """
                        SocketSession socketSession = event.getSocketSession();
                                Robot robot = socketSession.bindData("robot");
                        """
        );
    }

}
