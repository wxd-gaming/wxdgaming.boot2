package wxdgaming.boot2.starter.net.client;


import lombok.Getter;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.boot2.starter.net.server.http.HttpListenerFactory;

/**
 * tcp socket client
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-20 19:13
 */
@Getter
public class TcpSocketClient extends SocketClient {

    public TcpSocketClient(SocketClientConfig config) {
        super(config);
    }

    @Start
    @Override public void start(ProtoListenerFactory protoListenerFactory, HttpListenerFactory httpListenerFactory) {
        super.start(protoListenerFactory, httpListenerFactory);
    }
}
