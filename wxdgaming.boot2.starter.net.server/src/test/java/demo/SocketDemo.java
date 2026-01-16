package demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.util.BytesUnit;
import wxdgaming.boot2.core.util.JvmUtil;
import wxdgaming.boot2.starter.net.NioFactory;
import wxdgaming.boot2.starter.net.client.SocketClient;
import wxdgaming.boot2.starter.net.client.SocketClientConfig;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.boot2.starter.net.ssl.SslContextByJks;
import wxdgaming.boot2.starter.net.ssl.SslProtocolType;
import wxdgaming.boot2.starter.net.ssl.WxdOptionalSslHandler;

import javax.net.ssl.SSLContext;

/**
 * 同时 支持 websocket、tcp、http 并且兼容 ssl
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2023-08-25 09:43
 **/
@Slf4j
public class SocketDemo {

    public static void test() throws InterruptedException {
        SocketClientConfig socketClientConfig = new SocketClientConfig();
        socketClientConfig.setEnabledSSL(false);
        socketClientConfig.setEnabledWebSocket(true);
        socketClientConfig.setHost("127.0.0.1");
        socketClientConfig.setPort(8080);
        SocketClient socketClient = new SocketClient(socketClientConfig, new ProtoListenerFactory());
        socketClient.init();
        socketClient.connect(session -> {
            System.out.println(session.isOpen());
            session.writeAndFlush("ws test");
        });
        socketClient.connect().syncUninterruptibly();
        Thread.sleep(3000);
    }

    public static void main(String[] args) throws Exception {

        new Thread(() -> {
            SocketDemo socketDemo = new SocketDemo();
            socketDemo.bind(11001);
        }).start();

        Thread.sleep(3000);
        test();
    }

    //配置服务端线程组
    ChannelFuture socketfuture = null;

    public void stop() {
        if (socketfuture != null) {
            socketfuture.channel().close().addListener(ChannelFutureListener.CLOSE);
            socketfuture.awaitUninterruptibly();
            socketfuture = null;
            log.info("Netty 服务端关闭");
        }
    }

    /** 启动流程 */
    public void bind(int port) {
        try {
            final SSLContext sslContext = SslContextByJks.sslContext(
                    SslProtocolType.SSL,
                    "xiaw-jks/xiaw.net-2023-07-15.jks",
                    "xiaw-jks/xiaw.net-2023-07-15-pwd.txt"
            );

            ServerBootstrap serverBootstrap = new ServerBootstrap().group(NioFactory.bossThreadGroup(), NioFactory.workThreadGroup())
                    /*channel方法用来创建通道实例(NioServerSocketChannel类来实例化一个进来的链接)*/
                    .channel(NioFactory.serverSocketChannelClass())
                    /*方法用于设置监听套接字*/
                    .option(ChannelOption.SO_BACKLOG, 0)
                    /*地址重用，socket链接断开后，立即可以被其他请求使用*/
                    .option(ChannelOption.SO_REUSEADDR, true)
                    /*方法用于设置和客户端链接的套接字*/
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    /*是否启用心跳保活机机制*/
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    /*地址重用，socket链接断开后，立即可以被其他请求使用*/
                    .childOption(ChannelOption.SO_REUSEADDR, true)
                    /*发送缓冲区 影响 channel.isWritable()*/
                    .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(1, (int) BytesUnit.MB.toBytes(12)))
                    /*接收缓冲区，使用内存池*/
                    .childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(512, 2048, (int) BytesUnit.MB.toBytes(12)))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            if (JvmUtil.getProperty("Netty_Debug_Logger", false, Boolean::parseBoolean)) {
                                pipeline.addLast("logging", new LoggingHandler("DEBUG"));// 设置log监听器，并且日志级别为debug，方便观察运行流程
                            }
                            pipeline.addLast("active", new ChannelActiveHandler());
                            pipeline.addFirst(new WxdOptionalSslHandler(sslContext));
                            //Socket 连接心跳检测
                            pipeline.addLast("idleStateHandler", new IdleStateHandler(60, 0, 0));
                            pipeline.addLast("socketChoose", new SocketChooseHandler());
                            pipeline.addLast("commonhandler", new DeviceServerHandler());
                        }
                    });

            //绑定端口，同步等待成功
            socketfuture = serverBootstrap.bind(port).sync();
            if (socketfuture.isSuccess()) {
                log.info("Netty 服务已启动");
            }
            socketfuture.channel().closeFuture().sync();
        } catch (Exception e) {
            //优雅退出，释放线程池
            e.printStackTrace(System.out);
        }
    }
}
