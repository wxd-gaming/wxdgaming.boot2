package run;

import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import wxdgaming.boot2.starter.net.pojo.ProtoBuf2Pojo;
import wxdgaming.game.message.role.ResLogin;

import java.util.Objects;

/**
 * protobuf篡改
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-05-27 13:15
 **/
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GatewayProtoTest {

    // @Test
    // @Order(1)
    // public void buildProtoBuf() {
    //     ProtoBuf2Pojo.actionProtoFile("src/main/java", "../wxdgaming.game.test-script/src/main/proto");
    // }

    @Test
    @Order(2)
    public void buildResGatewayProtoHandler() {
        ProtoBuf2Pojo.createMapping(
                "src/main/java",
                "wxdgaming.game.gateway.script",
                "Req",
                "wxdgaming.game.message.role",
                cls -> Objects.equals(cls, ResLogin.class) || true,
                () -> """
                        @ThreadParam(path = "forwardMessage") InnerForwardMessage forwardMessage""",
                null
        );
    }

    @Test
    @Order(2)
    public void buildReqGatewayProtoHandler() {
        ProtoBuf2Pojo.createMapping(
                "src/main/java",
                "wxdgaming.game.chart.script",
                "Inner",
                "wxdgaming.game.message",
                cls -> true,
                null,
                null
        );
    }

}
