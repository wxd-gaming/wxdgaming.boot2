package wxdgaming.boot2.starter.tailf;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemProperties;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;

/**
 * 从倒数第几行开始读取
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-12-08 13:43
 **/
@Slf4j
public class LastAccessFile extends Thread {

    private final Path filePath;
    private final Consumer<String> consumer;
    private final AtomicLong filePointer = new AtomicLong();
    private final AtomicLong fileLastModified = new AtomicLong();
    private final AtomicBoolean runing = new AtomicBoolean(true);
    private int lastLines;

    public LastAccessFile(String path, int lastLines, Consumer<String> consumer) {
        this.filePath = Paths.get(path);
        this.consumer = consumer;

        if (StringUtils.isBlank(path)) {
            throw new RuntimeException("尚未选择文件");
        }

        // 获取当前系统登录的用户名
        String currentUser = SystemProperties.getUserName();
        log.debug("当前系统登录的用户是: {}", currentUser);

        if (path.contains("${user.name}")) {
            path = path.replace("${user.name}", currentUser);
        }

        Path filePath = Paths.get(path);
        Path absolutePath = filePath.toAbsolutePath();
        if (!Files.exists(absolutePath.getParent())) {
            throw new RuntimeException("需要监听的文件夹: " + absolutePath + " 异常");
        }
        this.lastLines = lastLines;

        try {
            skipped();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override public void run() {
        long pollingInterval = 20; // Poll every second
        while (runing.get()) {
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(pollingInterval));
            try {
                if (Files.exists(filePath)) {
                    if (Files.getLastModifiedTime(filePath).toMillis() != fileLastModified.get()) {
                        readLastLine();
                    }
                }
            } catch (Exception e) {
                log.error("监听文件异常", e);
            }
        }
    }

    public void close() {
        runing.set(false);
        try {
            this.join();
        } catch (InterruptedException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    void skipped() throws IOException {
        long findFilePointer = 0;
        if (Files.exists(filePath)) {
            try (RandomAccessFile file = new RandomAccessFile(filePath.toFile(), "r")) {
                // 记录文件的末尾处
                findFilePointer = file.length();
                while (lastLines > 0 && findFilePointer > 0) {
                    findFilePointer--;
                    /*把指针移动到上次读取的位置*/
                    file.seek(findFilePointer);
                    int read = file.read();
                    if (read != -1) {
                        if ((char) read == '\n') {
                            lastLines--;
                        }
                    }
                }
            }
        }
        filePointer.set(findFilePointer);
    }

    void readLastLine() throws IOException {
        if (Files.exists(filePath)) {
            File file = filePath.toFile();
            fileLastModified.set(file.lastModified());
            try (RandomAccessFile accessFile = new RandomAccessFile(file, "r")) {
                /*日志跨天了的话，文件长度会被设置0*/
                if (filePointer.get() > accessFile.length()) {
                    filePointer.set(accessFile.length());
                }
                /*这个位置可能触发变更，但是内容没有变化*/
                if (accessFile.length() == filePointer.get()) {
                    return;
                }
                /*把指针移动到上次读取的位置*/
                accessFile.seek(filePointer.get());
                try (FileChannel channel = accessFile.getChannel();
                     InputStream inputStream = Channels.newInputStream(channel);
                     InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);) {

                    try (BufferedReader bufferedReader = new BufferedReader(streamReader)) {
                        // 使用 Channels 和 CharsetDecoder 来读取 UTF-8 编码的内容
                        String line;
                        while (runing.get() && (line = bufferedReader.readLine()) != null) {
                            try {
                                consumer.accept(line);
                            } catch (Exception e) {
                                log.error("监听文件异常", e);
                            }
                        }
                        // 将文件指针更新为当前文件大小，跳到当前末尾处
                        filePointer.set(accessFile.getFilePointer());
                    }
                }
            }
        }
    }

}
