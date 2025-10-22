package wxdgaming.logbus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.collection.SplitCollection;
import wxdgaming.boot2.core.event.StartEvent;
import wxdgaming.boot2.core.executor.ExecutorEvent;
import wxdgaming.boot2.core.io.FileReadUtil;
import wxdgaming.boot2.core.io.FileWriteUtil;
import wxdgaming.boot2.core.util.Md5Util;
import wxdgaming.boot2.starter.net.http.HttpDataAction;
import wxdgaming.boot2.starter.net.httpclient5.HttpRequestPost;
import wxdgaming.boot2.starter.net.httpclient5.HttpResponse;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

/**
 * 日志服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-08 11:36
 **/
@Slf4j
@Service
public class LogBusService implements InitPrint {

    private final LogBusProperties logBusProperties;
    private final SaveLog2FileEvent savePushLog2FileEvent = new SaveLog2FileEvent("push");
    private final PostLog2FileEvent postPushLog2FileEvent = new PostLog2FileEvent("push");

    private final SaveLog2FileEvent saveUpdateLog2FileEvent = new SaveLog2FileEvent("update");
    private final PostLog2FileEvent postUpdateLog2FileEvent = new PostLog2FileEvent("update");

    private Thread postPushLogThread;
    private Thread postUpdateLogThread;
    private Thread saveLog2FileThread;

    private final AtomicBoolean close = new AtomicBoolean(false);

    public LogBusService(LogBusProperties logBusProperties) {
        this.logBusProperties = logBusProperties;
        this.savePushLog2FileEvent.reset();
        this.saveUpdateLog2FileEvent.reset();
    }


    @EventListener
    public void start(StartEvent event) {
        log.info("LogBusService start...");

        saveLog2FileThread = Thread.ofPlatform().start(() -> {
            while (!close.get()) {
                try {
                    LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(3000));
                    savePushLog2FileEvent.run();
                    saveUpdateLog2FileEvent.run();
                } catch (Exception e) {
                    log.error("LogBusService error", e);
                }
            }
        });

        postPushLogThread = Thread.ofPlatform().start(() -> {
            while (!close.get()) {
                try {
                    LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(500));
                    postPushLog2FileEvent.run();
                } catch (Exception e) {
                    log.error("LogBusService error", e);
                }
            }
        });

        postUpdateLogThread = Thread.ofPlatform().start(() -> {
            while (!close.get()) {
                try {
                    LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(500));
                    postUpdateLog2FileEvent.run();
                } catch (Exception e) {
                    log.error("LogBusService error", e);
                }
            }
        });

    }

    /** 推送日志 */
    public void pushLog(LogEntity logEntity) {
        savePushLog2FileEvent.addLog(logEntity);
    }

    /** 更新记录 */
    public void updateLog(LogEntity logEntity) {
        saveUpdateLog2FileEvent.addLog(logEntity);
    }

    private class SaveLog2FileEvent extends ExecutorEvent {

        private final String type;

        public SaveLog2FileEvent(String type) {
            this.type = type;
        }

        @Override public String getStack() {
            return "logbus-post";
        }

        private SplitCollection<LogEntity> logEntities = null;

        private synchronized SplitCollection<LogEntity> reset() {
            SplitCollection<LogEntity> tmpLogEntities = logEntities;
            logEntities = new SplitCollection<>(logBusProperties.getSplitOrg());
            return tmpLogEntities;
        }

        private synchronized void addLog(LogEntity logEntity) {
            logEntities.add(logEntity);
        }

        @Override public void onEvent() throws Exception {
            SplitCollection<LogEntity> tmpLogEntities = reset();
            while (!tmpLogEntities.isEmpty()) {
                List<LogEntity> logEntities1 = tmpLogEntities.removeFirst();
                try {
                    Path path = Path.of(logBusProperties.getFilePath(), type, "log_" + RandomStringUtils.secure().next(8, true, true) + "_" + System.nanoTime() + ".dat");
                    FileWriteUtil.writeString(path.toFile(), JSON.toJSONString(logEntities1, SerializerFeature.MapSortField, SerializerFeature.SortField));
                } catch (Exception e) {
                    log.error("LogBusService error", e);
                }
            }
        }
    }


    private class PostLog2FileEvent extends ExecutorEvent {

        private final String type;

        public PostLog2FileEvent(String type) {
            this.type = type;
        }

        @Override public String getStack() {
            return "logbus-post";
        }

        @Override public void onEvent() throws Exception {
            Path path = Path.of(logBusProperties.getFilePath(), type);
            if (!Files.exists(path)) return;
            Files.walk(path)
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(f -> System.currentTimeMillis() - f.lastModified() > TimeUnit.SECONDS.toMillis(5))
                    .sorted(Comparator.comparingLong(File::lastModified))
                    .limit(50)
                    .filter(f -> f.toString().endsWith(".dat"))
                    .forEach(f -> {
                        String json = FileReadUtil.readString(f);
                        TreeMap<String, String> jsonObject = new TreeMap<>();
                        jsonObject.put("time", String.valueOf(System.currentTimeMillis()));
                        jsonObject.put("data", json);
                        String signBefore = HttpDataAction.httpData(jsonObject) + logBusProperties.getToken();
                        String sign = Md5Util.md5DigestEncode(signBefore);
                        jsonObject.put("sign", sign);
                        String url = logBusProperties.getPostUrl() + "/log/" + type;
                        HttpRequestPost httpRequestPost = HttpRequestPost.ofJson(url, jsonObject);
                        if (logBusProperties.isGzip()) {
                            httpRequestPost.useGzip();
                        }
                        HttpResponse execute = httpRequestPost.execute();
                        if (execute.isSuccess()) {
                            f.delete();
                            log.debug("LogBusService url={} logFile={} gzip={}", url, f, logBusProperties.isGzip());
                        } else {
                            log.error("LogBusService url={} logFile={} gzip={} error:{}", url, f, logBusProperties.isGzip(), execute);
                            f.setLastModified(System.currentTimeMillis());
                        }
                    });
        }

    }

}
