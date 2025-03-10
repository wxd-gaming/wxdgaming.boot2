package wxdgaming.boot2.starter.net.client;

import com.alibaba.fastjson.annotation.JSONField;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.starter.net.ssl.SslContextByJks;
import wxdgaming.boot2.starter.net.ssl.SslContextNoFile;
import wxdgaming.boot2.starter.net.ssl.SslProtocolType;

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
public class SocketClientConfig extends ObjectBase {

    @JSONField(ordinal = 1)
    private boolean debug = false;
    @JSONField(ordinal = 2)
    private String host = "127.0.0.1";
    @JSONField(ordinal = 2)
    private int port = 8080;
    @JSONField(ordinal = 5)
    private boolean enabledWebSocket = false;
    /** 如果开启 websocket 可以指定后缀 */
    @JSONField(ordinal = 6)
    private String webSocketPrefix = "/ws";
    /** 断线重连，默认开启 */
    @JSONField(ordinal = 7)
    private boolean enabledReconnection = true;
    /** 是否需要特别调用flush */
    @JSONField(ordinal = 8)
    private boolean enabledScheduledFlush = false;
    /** 是否需要特别调用flush, 调用频率单位 ms */
    @JSONField(ordinal = 9)
    private long scheduledDelayMs = 5;
    @JSONField(ordinal = 10)
    private boolean enabledSSL = true;
    @JSONField(ordinal = 11)
    private SslProtocolType sslProtocolType = SslProtocolType.TLS;
    /** 路径 */
    @JSONField(ordinal = 12)
    private String sslKeyStorePath = "";
    /** 路径 */
    @JSONField(ordinal = 13)
    private String sslPasswordPath = "";
    /** 每一个消息最大字节数，单位是kb */
    @JSONField(ordinal = 20)
    private int maxFrameBytes = -1;
    /** 每一秒钟接受消息的最大量 */
    @JSONField(ordinal = 21)
    private int maxFrameLength = -1;
    /** 完整消息一次最大传输，单位mb */
    @JSONField(ordinal = 22)
    private int maxAggregatorLength = 64;
    @JSONField(ordinal = 23)
    private int maxConnectionCount = 1;
    @JSONField(ordinal = 30)
    private int readTimeout = 0;
    @JSONField(ordinal = 31)
    private int writeTimeout = 0;
    @JSONField(ordinal = 32)
    private int idleTimeout = 0;
    @JSONField(ordinal = 33)
    private int connectTimeout = 500;

    /** 接收缓冲区大小，单位 mb */
    @JSONField(ordinal = 40)
    private int recvByteBufM = 12;
    /** 发送缓冲区大小，单位 mb */
    @JSONField(ordinal = 41)
    private int writeByteBufM = 12;

    public IdleStateHandler idleStateHandler() {
        return new IdleStateHandler(getReadTimeout(), getWriteTimeout(), getIdleTimeout(), TimeUnit.SECONDS);
    }

    public SSLContext sslContext() {
        if (sslProtocolType == null) {
            return null;
        }
        if (StringUtils.isBlank(sslKeyStorePath)) {
            return SslContextNoFile.sslContext(sslProtocolType);
        }
        return SslContextByJks.sslContext(sslProtocolType, sslKeyStorePath, sslPasswordPath);
    }

}
