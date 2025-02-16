package wxdgaming.boot2.starter.net.server;

import io.netty.handler.timeout.IdleStateHandler;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.starter.net.server.ssl.SslContextByJks;
import wxdgaming.boot2.starter.net.server.ssl.SslProtocolType;

import javax.net.ssl.SSLContext;
import java.util.concurrent.TimeUnit;

/**
 * 网络监听配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-13 09:14
 **/
@Getter
@Setter
public class SocketServerConfig extends ObjectBase {

    private boolean debug = false;
    private int port = 8080;
    private int maxFrameBytes = -1;
    private int maxFrameLength = -1;
    private boolean enabledHttp = true;
    private boolean enabledWebSocket = false;
    /** 如果开启 websocket 可以指定后缀 */
    private String webSocketPrefix = "";
    private SslProtocolType sslProtocolType;
    /** 路径 */
    private String sslKeyStorePath;
    /** 路径 */
    private String sslPasswordPath;
    /** 完整消息一次最大传输，单位mb */
    private int maxAggregatorLength = 64;
    private int readTimeout = 0;
    private int writeTimeout = 0;
    private int idleTimeout = 0;

    public IdleStateHandler idleStateHandler() {
        return new IdleStateHandler(getReadTimeout(), getWriteTimeout(), getIdleTimeout(), TimeUnit.SECONDS);
    }

    public SSLContext sslContext() {
        if (sslProtocolType == null) {
            return null;
        }
        return SslContextByJks.sslContext(sslProtocolType, sslKeyStorePath, sslPasswordPath);
    }

}
