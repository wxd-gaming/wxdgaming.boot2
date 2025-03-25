package wxdgaming.boot2.starter.net.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.ann.Sort;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.ann.shutdown;
import wxdgaming.boot2.core.threading.ExecutorUtil;
import wxdgaming.boot2.core.util.BytesUnit;
import wxdgaming.boot2.starter.net.NioFactory;
import wxdgaming.boot2.starter.net.SessionGroup;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.boot2.starter.net.server.http.HttpListenerFactory;
import wxdgaming.boot2.starter.net.ssl.WxdSslHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * tcp client
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-15 13:12
 **/
@Slf4j
@Getter
public abstract class SocketClient {

    protected Bootstrap bootstrap;
    protected final SocketClientConfig config;


    protected volatile SessionGroup sessionGroup = new SessionGroup();
    protected volatile boolean started = false;
    protected volatile boolean closed = false;
    /** 包含的http head参数 */
    protected final HttpHeaders httpHeaders = new DefaultHttpHeaders();

    public SocketClient(SocketClientConfig config) {
        this.config = config;
    }

    public WebSocketClientHandshaker newHandshaker() {
        String protocol = "ws";
        if (config.isEnabledSSL()) {
            protocol = "wss";
        }
        String url = protocol + "://" + getConfig().getHost() + ":" + getConfig().getPort() + this.getConfig().getWebSocketPrefix();
        log.debug("{}", url);
        return WebSocketClientHandshakerFactory.newHandshaker(
                URI.create(url),
                WebSocketVersion.V13,
                null,
                false,
                httpHeaders,
                (int) BytesUnit.Mb.toBytes(config.getMaxAggregatorLength())/*64 mb*/
        );
    }

    @Start
    @Sort(2000)
    public void start(ProtoListenerFactory protoListenerFactory, HttpListenerFactory httpListenerFactory) {

        SocketClientDeviceHandler socketClientDeviceHandler = new SocketClientDeviceHandler();
        ClientMessageDecode clientMessageDecode = new ClientMessageDecode(config, protoListenerFactory, httpListenerFactory);
        SSLContext sslContext = config.sslContext();

        int writeBytes = (int) BytesUnit.Mb.toBytes(config.getWriteByteBufM());
        int recvBytes = (int) BytesUnit.Mb.toBytes(config.getRecvByteBufM());
        int maxContentLength = (int) BytesUnit.Mb.toBytes(config.getMaxAggregatorLength());

        bootstrap = new Bootstrap();
        bootstrap.group(NioFactory.clientThreadGroup())
                .channel(NioFactory.clientSocketChannelClass())
                /*链接超时设置*/
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, this.config.getConnectTimeout())
                /*是否启用心跳保活机机制*/
                .option(ChannelOption.SO_KEEPALIVE, true)
                /*发送缓冲区 影响 channel.isWritable()*/
                .option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(1, writeBytes))
                /*使用内存池*/
                .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 1024, recvBytes))
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        if (SocketClient.this.config.isDebug()) {
                            pipeline.addLast(new LoggingHandler("DEBUG"));/*设置log监听器，并且日志级别为debug，方便观察运行流程*/
                        }
                        if (SocketClient.this.config.isEnabledSSL()) {
                            SSLEngine sslEngine = sslContext.createSSLEngine();
                            sslEngine.setUseClientMode(true);
                            sslEngine.setNeedClientAuth(false);
                            pipeline.addFirst("sslhandler", new WxdSslHandler(sslEngine));
                        }
                        /*空闲链接检查*/
                        pipeline.addLast(SocketClient.this.config.idleStateHandler());
                        /*处理链接*/
                        pipeline.addLast("device-handler", socketClientDeviceHandler);
                        /*解码消息*/
                        pipeline.addLast("decode", clientMessageDecode);
                        /*解码消息*/
                        pipeline.addLast(
                                "encode",
                                new ClientMessageEncode(
                                        config.isEnabledScheduledFlush(), config.getScheduledDelayMs(),
                                        socketChannel, protoListenerFactory
                                )
                        );

                        if (config.isEnabledWebSocket()) {

                            // HttpServerCodec：将请求和应答消息解码为HTTP消息
                            pipeline.addBefore("device-handler", "http-codec", new HttpClientCodec());
                            pipeline.addBefore("device-handler", "http-object-aggregator", new HttpObjectAggregator(maxContentLength));/*接受完整的http消息 64mb*/
                            // ChunkedWriteHandler：向客户端发送HTML5文件,文件过大会将内存撑爆
                            pipeline.addBefore("device-handler", "http-chunked", new ChunkedWriteHandler());
                            pipeline.addBefore("device-handler", "websocket-aggregator", new WebSocketFrameAggregator(maxContentLength));
                            pipeline.addBefore("device-handler", "ProtocolHandler", new WebSocketClientProtocolHandler(newHandshaker()));
                        }
                        addChanelHandler(socketChannel, pipeline);
                    }
                });
        for (int i = 0; i < config.getMaxConnectionCount(); i++) {
            connect();
        }
    }

    protected void addChanelHandler(SocketChannel socketChannel, ChannelPipeline pipeline) {}

    @Sort(100)
    @shutdown
    public void shutdown() {
        closed = true;
        log.info("shutdown tcp client：{}:{}", config.getHost(), config.getPort());
    }

    public final void connect() {
        if (sessionGroup.size() >= config.getMaxConnectionCount()) {
            log.error("{} 连接数已经达到最大连接数：{}", this.getClass().getSimpleName(), config.getMaxConnectionCount());
            return;
        }
        ChannelFuture connect = connect(null);
        try {
            connect.sync();
        } catch (InterruptedException e) {
        }
    }

    public ChannelFuture connect(Consumer<SocketSession> consumer) {
        return bootstrap.connect(config.getHost(), config.getPort())
                .addListener((ChannelFutureListener) future -> {
                    Throwable cause = future.cause();
                    if (cause != null) {
                        log.error("{} connect error {}", this.getClass().getSimpleName(), cause.toString());
                        if (reconnection()) {
                            log.info("{} reconnection", this.getClass().getSimpleName());
                        }
                        return;
                    }
                    Channel channel = future.channel();
                    SocketSession socketSession = new SocketSession(SocketSession.Type.client, channel, false, config.isEnabledScheduledFlush());
                    if (config.getMaxFrameBytes() > 0) {
                        socketSession.setMaxFrameBytes(BytesUnit.Kb.toBytes(getConfig().getMaxFrameBytes()));
                    }

                    socketSession.setMaxFrameLength(getConfig().getMaxFrameLength());
                    socketSession.setSsl(config.isEnabledSSL());

                    if (config.isEnabledWebSocket()) {
                        socketSession.setWebSocket(true);
                    }

                    log.debug("{} connect success {}", this.getClass().getSimpleName(), channel);

                    sessionGroup.add(socketSession);
                    /*添加事件，如果链接关闭了触发*/
                    socketSession.getChannel().closeFuture().addListener(closeFuture -> {
                        sessionGroup.remove(socketSession);
                        reconnection();
                    });

                    if (consumer != null) {
                        consumer.accept(socketSession);
                    }
                });
    }

    AtomicLong atomicLong = new AtomicLong();

    protected boolean reconnection() {

        if (closed || !config.isEnabledReconnection())
            return false;

        long l = atomicLong.get();
        if (l < 10) {
            l = atomicLong.incrementAndGet();
        }

        log.info("{} 链接异常 {} 秒 重连", this.hashCode(), l);

        ExecutorUtil.getInstance().getLogicExecutor().schedule(this::connect, l, TimeUnit.SECONDS);
        return true;
    }

    /** 空闲 如果 null 触发异常 */
    public SocketSession idleNullException() {
        return getSessionGroup().loopNullException();
    }

    /** 空闲 */
    public SocketSession idle() {
        return getSessionGroup().loop();
    }

}
