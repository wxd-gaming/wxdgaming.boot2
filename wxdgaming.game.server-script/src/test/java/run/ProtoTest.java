package run;

import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import wxdgaming.boot2.core.ann.ThreadParam;
import wxdgaming.boot2.starter.net.pojo.ProtoBuf2Pojo;
import wxdgaming.game.server.bean.role.Player;

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
    public void buildGameProtoHandler() {
        ProtoBuf2Pojo.createMapping(
                "src/main/java",
                "wxdgaming.game.server.script",
                "Req",
                "wxdgaming.game.message",
                null,
                () -> """
                        @ThreadParam(path = "clientSessionMapping") ClientSessionMapping clientSessionMapping""",
                () -> """
                        """
        );
    }
    @Test
    @Order(2)
    public void buildGamePlayerProtoHandler() {
        ProtoBuf2Pojo.createMapping(
                "src/main/java",
                "wxdgaming.game.server.script",
                "Req",
                "wxdgaming.game.message",
                null,
                () -> """
                        @ThreadParam(path = "player") Player player""",
                () -> """
                        """
        );
    }

    @Test
    @Order(2)
    public void buildInnerProtoHandler() {
        ProtoBuf2Pojo.createMapping(
                "src/main/java",
                "wxdgaming.game.server.script",
                "Inner",
                "wxdgaming.game.message",
                null,
                () -> """
                        @ThreadParam(path = "clientSessionMapping") ClientSessionMapping clientSessionMapping""",
                () -> """
                        """
        );
    }

}
