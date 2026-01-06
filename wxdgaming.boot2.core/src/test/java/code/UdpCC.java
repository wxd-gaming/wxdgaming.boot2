package code;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class UdpCC {


    public static void main(String[] args) throws IOException {

        final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(6);

        for (int i = 0; i < 50; i++) {
            scheduledExecutorService.scheduleWithFixedDelay(new CCClass(), 0, 10, TimeUnit.MILLISECONDS);
        }
        LockSupport.parkNanos(TimeUnit.MINUTES.toNanos(20));
    }

    public static class CCClass implements Runnable {

        // 1. 创建 DatagramChannel
        final DatagramChannel channel;
        final InetSocketAddress targetAddress;

        public CCClass() throws IOException {
            channel = DatagramChannel.open();
            // String hostname = "118.31.167.55";
            String hostname = "10.28.0.202";
            targetAddress = new InetSocketAddress(hostname, ThreadLocalRandom.current().nextInt(1000, 12000));
        }

        @Override public void run() {
            try {
                // 2. 准备要发送的数据
                for (int j = 0; j < 100000; j++) {
                    // 3. 指定目标地址和端口
                    int anInt = ThreadLocalRandom.current().nextInt(10, 100);
                    String message = RandomStringUtils.random(anInt, true, true);
                    ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
                    // 4. 发送数据包
                    channel.send(buffer, targetAddress);
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
    }

}
