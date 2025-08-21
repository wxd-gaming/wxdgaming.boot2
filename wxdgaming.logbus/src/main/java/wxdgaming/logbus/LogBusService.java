package wxdgaming.logbus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.collection.SplitCollection;
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
    private final SaveLog2FileEvent saveLog2FileEvent = new SaveLog2FileEvent();
    private final PostLog2FileEvent postLog2FileEvent = new PostLog2FileEvent();

    private Thread postLogThread;
    private Thread saveLog2FileThread;

    private final AtomicBoolean close = new AtomicBoolean(false);

    public LogBusService(LogBusProperties logBusProperties) {
        this.logBusProperties = logBusProperties;
        this.saveLog2FileEvent.reset();
    }


    @Start
    public void start() {
        log.info("LogBusService start...");

        saveLog2FileThread = Thread.ofPlatform().start(() -> {
            while (!close.get()) {
                try {
                    LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(3000));
                    saveLog2FileEvent.run();
                } catch (Exception e) {
                    log.error("LogBusService error", e);
                }
            }
        });

        postLogThread = Thread.ofPlatform().start(() -> {
            while (!close.get()) {
                try {
                    LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(500));
                    postLog2FileEvent.run();
                } catch (Exception e) {
                    log.error("LogBusService error", e);
                }
            }
        });

    }

    public void addLog(LogEntity logEntity) {
        saveLog2FileEvent.addLog(logEntity);
    }

    private class SaveLog2FileEvent extends ExecutorEvent {

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
                    Path path = Path.of(logBusProperties.getFilePath(), "log_" + StringUtils.randomString(8) + "_" + System.nanoTime() + ".dat");
                    FileWriteUtil.writeString(path.toFile(), JSON.toJSONString(logEntities1, SerializerFeature.MapSortField, SerializerFeature.SortField));
                } catch (Exception e) {
                    log.error("LogBusService error", e);
                }
            }
        }
    }

    private class PostLog2FileEvent extends ExecutorEvent {

        @Override public void onEvent() throws Exception {
            Path path = Path.of(logBusProperties.getFilePath());
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
                        HttpRequestPost httpRequestPost = HttpRequestPost.ofJson(logBusProperties.getPostUrl(), jsonObject);
                        if (logBusProperties.isGzip()) {
                            httpRequestPost.useGzip();
                        }
                        HttpResponse execute = httpRequestPost.execute();
                        if (execute.isSuccess()) {
                            f.delete();
                            log.debug("LogBusService postUrl={} logFile={} gzip={}", execute, f, logBusProperties.isGzip());
                        } else {
                            log.error("LogBusService postUrl={} logFile={} gzip={} error:{}", execute, f, logBusProperties.isGzip(), execute);
                            f.setLastModified(System.currentTimeMillis());
                        }
                    });
        }

    }

}
