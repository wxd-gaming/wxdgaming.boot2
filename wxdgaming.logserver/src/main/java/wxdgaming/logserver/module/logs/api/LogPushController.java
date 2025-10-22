package wxdgaming.logserver.module.logs.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.SpringUtil;
import wxdgaming.boot2.core.event.StartEvent;
import wxdgaming.boot2.core.executor.ExecutorEvent;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.io.FileReadUtil;
import wxdgaming.boot2.core.io.FileWriteUtil;
import wxdgaming.boot2.core.io.Objects;
import wxdgaming.boot2.core.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.core.util.Md5Util;
import wxdgaming.boot2.starter.net.http.HttpDataAction;
import wxdgaming.logserver.LogServerProperties;
import wxdgaming.logserver.bean.LogEntity;
import wxdgaming.logserver.module.logs.LogService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * 日志接口
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-07 17:05
 **/
@Slf4j
@RestController
@RequestMapping("/log")
public class LogPushController {

    final AtomicLong insert_count = new AtomicLong();
    final AtomicLong update_count = new AtomicLong();
    final LogServerProperties logServerProperties;
    final LogService logService;

    public LogPushController(LogServerProperties logServerProperties, LogService logService) {
        this.logServerProperties = logServerProperties;
        this.logService = logService;
    }

    @EventListener
    public void start(StartEvent event) {
        for (int i = 0; i < 10; i++) {
            ExecutorFactory.getExecutorServiceLogic()
                    .scheduleAtFixedRate(
                            new PostLog2FileEvent("insert" + File.separator + i, logService::submitLog),
                            500,
                            500,
                            TimeUnit.MILLISECONDS
                    );
            ExecutorFactory.getExecutorServiceLogic()
                    .scheduleAtFixedRate(
                            new PostLog2FileEvent("update" + File.separator + i, logService::updateLog),
                            500,
                            500,
                            TimeUnit.MILLISECONDS
                    );
        }
    }

    List<LogEntity> checkSign(HttpServletRequest request) throws Exception {
        String json = SpringUtil.readBody(request);

        TreeMap<String, String> map = JSON.parseObject(json, new TypeReference<TreeMap<String, String>>() {});
        String sign = map.remove("sign");

        String signBefore = HttpDataAction.httpData(map) + logServerProperties.getJwtKey();
        String selfSign = Md5Util.md5(signBefore);
        if (!Objects.equals(sign, selfSign)) {
            log.warn("LogPushController sign={} signBefore={}", sign, signBefore);
            logService.saveErrorLog("sign 签名错误", json, "pushList");
            throw new IllegalArgumentException("sign 签名错误");
        }
        return JSON.parseArray(map.get("data"), LogEntity.class);
    }

    @RequestMapping("/push")
    public RunResult pushList(HttpServletRequest request) throws Exception {
        List<LogEntity> logEntityList = checkSign(request);
        logEntityList = logEntityList.stream()
                .filter(logEntity -> {
                    boolean blank = StringUtils.isBlank(logEntity.getLogType());
                    if (blank) {
                        logService.saveErrorLog("logType 空", logEntity.toJSONString(), "pushList");
                    }
                    return !blank;
                })
                .toList();
        long incremented = insert_count.incrementAndGet();
        long index = incremented % 10;
        savePath("insert" + File.separator + index, logEntityList);
        return RunResult.ok();
    }

    @RequestMapping("/update")
    public RunResult updateList(HttpServletRequest request) throws Exception {
        List<LogEntity> logEntityList = checkSign(request);

        logEntityList = logEntityList.stream()
                .filter(logEntity -> {
                    if (StringUtils.isBlank(logEntity.getLogType())) {
                        logService.saveErrorLog("logType 空", logEntity.toJSONString(), "pushList");
                        return false;
                    }
                    if (logEntity.getUid() == 0) {
                        logService.saveErrorLog("uid 为0", logEntity.toJSONString(), "pushList");
                        return false;
                    }
                    return true;
                })
                .toList();

        long incremented = update_count.incrementAndGet();
        long index = incremented % 10;
        savePath("update" + File.separator + index, logEntityList);
        return RunResult.ok();
    }

    void savePath(String path, List<LogEntity> logEntityList) {
        Path slog = Paths.get(
                "slog",
                path,
                MyClock.formatDate(MyClock.SDF_YYYYMMDD),
                RandomStringUtils.secure().next(128, true, true) + ".dat"
        );
        FileWriteUtil.writeString(slog.toFile(), FastJsonUtil.toJSONString(logEntityList));
    }

    private class PostLog2FileEvent extends ExecutorEvent {

        private final String type;
        private final Consumer<List<LogEntity>> consumer;

        public PostLog2FileEvent(String type, Consumer<List<LogEntity>> consumer) {
            this.type = type;
            this.consumer = consumer;
        }

        @Override public String getStack() {
            return "log-push-db";
        }

        @Override public void onEvent() throws Exception {
            Path path = Path.of("slog", type);
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
                        List<LogEntity> objects = FastJsonUtil.parseArray(json, LogEntity.class);
                        consumer.accept(objects);
                        f.delete();
                        log.debug("LogService delete: {}", f);
                    });
        }

    }

}
