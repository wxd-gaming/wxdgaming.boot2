package run;

import io.protostuff.Tag;
import org.junit.Test;
import wxdgaming.boot2.starter.net.pojo.PojoBase;
import wxdgaming.boot2.starter.net.pojo.ProtoBuf2Pojo;

/**
 * protobuf篡改
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-05-27 13:15
 **/
public class ProtoTest {

    @Test
    public void buildProtoBuf() {
        ProtoBuf2Pojo.actionProtoFile("src/main/java", "src/main/proto");
    }

    @Test
    public void buildProtoHandler() {
        ProtoBuf2Pojo.createMapping(
                "src/main/java",
                "wxdgaming.boot2.starter.net.module",
                "Req",
                "wxdgaming.boot2.starter.net.message",
                null,
                null,
                null
        );
        ProtoBuf2Pojo.createMapping(
                "src/main/java",
                "wxdgaming.boot2.starter.net.module",
                "Res",
                "wxdgaming.boot2.starter.net.message",
                null,
                null,
                null
        );
    }

    // @Test
    // public void t0() {
    //     ReqRemote builder = new ReqRemote();
    //     builder.setRpcId(1);
    //     builder.setGzip(1);
    //     builder.setCmd("ss");
    //     builder.setParams("1");
    //     builder.setRpcToken("1");
    //     byte[] byteArray = builder.encode();
    //     Tq tq = new Tq();
    //     byte[] encode = SerializerUtil.encode(tq);
    //     System.out.println(Arrays.toString(byteArray));
    //     System.out.println(Arrays.toString(encode));
    //     System.out.println("1");
    // }

    public static class Tq extends PojoBase {

        public static Tq parseFrom(byte[] bytes) {
            Tq tq = new Tq();
            tq.decode(bytes);
            return tq;
        }

        @Tag(1)
        private long rpcId = 1;
        @Tag(2)
        private int gzip = 1;
        @Tag(3)
        private String cmd = "ss";
        @Tag(4)
        private String params = "1";
        @Tag(5)
        private String rpcToken = "1";

    }

}
