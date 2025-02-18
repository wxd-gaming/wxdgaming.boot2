package wxdgaming.boot2.starter.net.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.ann.Sort;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.threading.ExecutorUtil;
import wxdgaming.boot2.core.util.BytesUnit;
import wxdgaming.boot2.core.util.GlobalUtil;
import wxdgaming.boot2.starter.net.NioFactory;
import wxdgaming.boot2.starter.net.SessionGroup;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.boot2.starter.net.server.http.HttpListenerFactory;
import wxdgaming.boot2.starter.net.ssl.WxdSslHandler;

import javax.net.ssl.SSLEngine;
import java.io.Closeable;
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
public abstract class SocketClient implements Closeable {

    protected Bootstrap bootstrap;
    protected final SocketClientConfig config;


    protected volatile SessionGroup sessionGroup = new SessionGroup();
    protected volatile boolean started = false;
    protected volatile boolean closed = false;

    public SocketClient(SocketClientConfig config) {
        this.config = config;
    }

    @Start
    @Sort(2000)
    public void start(ProtoListenerFactory protoListenerFactory, HttpListenerFactory httpListenerFactory) {

        SocketClientDeviceHandler socketClientDeviceHandler = new SocketClientDeviceHandler();
        ClientMessageDecode clientMessageDecode = new ClientMessageDecode(config, protoListenerFactory, httpListenerFactory);
        ClientMessageEncode clientMessageEncode = new ClientMessageEncode(protoListenerFactory);

        bootstrap = new Bootstrap();
        bootstrap.group(NioFactory.clientThreadGroup())
                .channel(NioFactory.clientSocketChannelClass())
                /*链接超时设置*/
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, this.config.getConnectTimeout())
                /*是否启用心跳保活机机制*/
                .option(ChannelOption.SO_KEEPALIVE, true)
                /*发送缓冲区 影响 channel.isWritable()*/
                .option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(1, (int) BytesUnit.Mb.toBytes(this.config.getMaxAggregatorLength())))
                /*使用内存池*/
                .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 1024, (int) BytesUnit.Mb.toBytes(this.config.getMaxAggregatorLength())))
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        if (SocketClient.this.config.isDebug()) {
                            pipeline.addLast(new LoggingHandler("DEBUG"));/*设置log监听器，并且日志级别为debug，方便观察运行流程*/
                        }
                        if (SocketClient.this.config.isEnabledSSL()) {
                            SSLEngine sslEngine = SocketClient.this.config.sslContext().createSSLEngine();
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
                        pipeline.addLast("encode", clientMessageEncode);

                        addChanelHandler(socketChannel, pipeline);
                    }
                });
        connect();
    }

    protected void addChanelHandler(SocketChannel socketChannel, ChannelPipeline pipeline) {}


    @Override public void close() {
        closed = true;
        log.info("shutdown tcp client：{}:{}", config.getHost(), config.getPort());
    }

    public final void connect() {
        if (sessionGroup.size() >= config.getMaxConnectionCount()) {
            log.error("{} 连接数已经达到最大连接数：{}", this.getClass().getSimpleName(), config.getMaxConnectionCount());
            return;
        }
        ChannelFuture connect = connect(null);
        connect.syncUninterruptibly();
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
                    SocketSession socketSession = new SocketSession(SocketSession.Type.client, channel, false);
                    if (config.getMaxFrameBytes() > 0) {
                        socketSession.setMaxFrameBytes(BytesUnit.Mb.toBytes(getConfig().getMaxFrameBytes()));
                    }
                    socketSession.setMaxFrameLength(getConfig().getMaxFrameLength());
                    socketSession.setSsl(config.isEnabledSSL());
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

        if (closed
            || !config.isEnableReconnection()
            || GlobalUtil.SHUTTING.get())
            return false;

        long l = atomicLong.get();
        if (l < 10) {
            l = atomicLong.incrementAndGet();
        }

        log.info("{} 链接异常 {} 秒 重连", this.hashCode(), l);

        ExecutorUtil.getLogicExecutor().schedule(this::connect, l, TimeUnit.SECONDS);
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
