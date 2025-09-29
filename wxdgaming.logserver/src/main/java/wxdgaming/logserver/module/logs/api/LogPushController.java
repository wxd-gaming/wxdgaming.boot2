package wxdgaming.logserver.module.logs.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.SpringUtil;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.executor.ExecutorEvent;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.io.FileReadUtil;
import wxdgaming.boot2.core.io.FileWriteUtil;
import wxdgaming.boot2.core.io.Objects;
import wxdgaming.boot2.core.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.AssertException;
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

    private final LogServerProperties logServerProperties;
    final LogService logService;

    public LogPushController(LogServerProperties logServerProperties, LogService logService) {
        this.logServerProperties = logServerProperties;
        this.logService = logService;
    }

    @Start
    public void start() {
        ExecutorFactory.getExecutorServiceLogic()
                .scheduleAtFixedRate(
                        new PostLog2FileEvent("insert", logService::submitLog),
                        500,
                        500,
                        TimeUnit.MILLISECONDS
                );
        ExecutorFactory.getExecutorServiceLogic()
                .scheduleAtFixedRate(
                        new PostLog2FileEvent("update", logService::updateLog),
                        500,
                        500,
                        TimeUnit.MILLISECONDS
                );
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
            throw new AssertException("sign 签名错误");
        }
        return JSON.parseArray(map.get("data"), LogEntity.class);
    }

    @RequestMapping("/push")
    public RunResult pushList(HttpServletRequest request) throws Exception {
        List<LogEntity> logEntityList = checkSign(request);
        savePath("insert", logEntityList);
        //        for (LogEntity logEntity : logEntityList) {
        //            if (StringUtils.isBlank(logEntity.getLogType())) {
        //                logService.saveErrorLog("logType 空", logEntity.toJSONString(), "pushList");
        //                continue;
        //            }
        //            logService.submitLog(logEntity);
        //        }
        return RunResult.ok();
    }

    @RequestMapping("/update")
    public RunResult updateList(HttpServletRequest request) throws Exception {

        List<LogEntity> logEntityList = checkSign(request);
        savePath("update", logEntityList);
        //        for (LogEntity logEntity : logEntityList) {
        //            if (StringUtils.isBlank(logEntity.getLogType())) {
        //                logService.saveErrorLog("logType 空", logEntity.toJSONString(), "updateList");
        //                continue;
        //            }
        //            if (logEntity.getUid() == 0) {
        //                logService.saveErrorLog("uid 为0", logEntity.toJSONString(), "updateList");
        //                continue;
        //            }
        //            logService.updateLog(logEntity);
        //        }
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
        private final Consumer<LogEntity> consumer;

        public PostLog2FileEvent(String type, Consumer<LogEntity> consumer) {
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
                        for (LogEntity object : objects) {
                            consumer.accept(object);
                        }
                        f.delete();
                        log.debug("LogService delete: {}", f);
                    });
        }

    }

}
