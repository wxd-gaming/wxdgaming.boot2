package wxdgaming.boot2.starter.net.server;

import com.alibaba.fastjson2.annotation.JSONField;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.starter.net.ssl.SslContextByJks;
import wxdgaming.boot2.starter.net.ssl.SslProtocolType;

import javax.net.ssl.SSLContext;
import java.util.concurrent.TimeUnit;

/**
 * 网络监听配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-13 09:14
 **/
@Getter
@Setter
public class SocketServerConfig extends ObjectBase {

    @JSONField(ordinal = 1)
    private boolean debug = false;
    @JSONField(ordinal = 2)
    private int port = 8800;
    @JSONField(ordinal = 3)
    private boolean enabledTcp = false;
    @JSONField(ordinal = 4)
    private boolean enabledHttp = true;
    @JSONField(ordinal = 5)
    private boolean enabledWebSocket = false;
    /** 如果开启 websocket 可以指定后缀 */
    @JSONField(ordinal = 6)
    private String webSocketPrefix = "/ws";
    /** 是否需要特别调用flush */
    @JSONField(ordinal = 7)
    private boolean enabledScheduledFlush = false;
    /** 是否需要特别调用flush, 调用频率单位 ms */
    @JSONField(ordinal = 8)
    private long scheduledDelayMs = 5;
    @JSONField(ordinal = 10)
    private SslProtocolType sslProtocolType;
    /** 路径 */
    @JSONField(ordinal = 11)
    private String sslKeyStorePath = "";
    /** 路径 */
    @JSONField(ordinal = 12)
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
    @JSONField(ordinal = 30)
    private int readTimeout = 0;
    @JSONField(ordinal = 31)
    private int writeTimeout = 0;
    @JSONField(ordinal = 32)
    private int idleTimeout = 0;
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
            throw new RuntimeException("jks path error");
        }
        if (StringUtils.isBlank(sslPasswordPath)) {
            throw new RuntimeException("jks pwd path error");
        }
        return SslContextByJks.sslContext(sslProtocolType, sslKeyStorePath, sslPasswordPath);
    }

}
