package run;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import wxdgaming.boot2.starter.net.pojo.ProtoBuf2Pojo;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;

/**
 * protobuf篡改
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-05-27 13:15
 **/
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProtoTest {

    @Test
    @Order(2)
    public void buildGameProtoHandler() {
        ProtoBuf2Pojo.createMapping(
                "src/main/java",
                "wxdgaming.game.server.script",
                "Req",
                "wxdgaming.game.message",
                null,
                ProtoEvent.class,
                () -> """
                        """,
                () -> """
                        UserMapping userMapping = event.bindData();
                                Player player = userMapping.player();
                        """
        );
    }

}
