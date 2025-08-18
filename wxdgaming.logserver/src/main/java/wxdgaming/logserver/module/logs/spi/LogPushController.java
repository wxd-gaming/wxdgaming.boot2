package wxdgaming.logserver.module.logs.spi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.ann.RequestBody;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.starter.net.ann.HttpRequest;
import wxdgaming.boot2.starter.net.ann.RequestMapping;
import wxdgaming.boot2.starter.net.server.http.HttpContext;
import wxdgaming.logserver.bean.LogEntity;
import wxdgaming.logserver.module.logs.LogService;

import java.util.List;

/**
 * 日志接口
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-07 17:05
 **/
@Slf4j
@Singleton
@RequestMapping("/api/log")
public class LogPushController {

    final LogService logService;

    @Inject
    public LogPushController(LogService logService) {
        this.logService = logService;
    }

    @HttpRequest("/pushList")
    public RunResult pushList(HttpContext request, @RequestBody String json) {
        if (StringUtils.isBlank(json)) {
            return RunResult.fail("log list 不能为空");
        }

        List<LogEntity> logEntityList = JSON.parseArray(json, LogEntity.class);

        String authorization = request.getRequest().header(HttpHeaderNames.AUTHORIZATION.toString());
        String jsonString = JSON.toJSONString(logEntityList, SerializerFeature.SortField, SerializerFeature.MapSortField);

        for (LogEntity logEntity : logEntityList) {
            if (StringUtils.isBlank(logEntity.getLogType())) {
                return RunResult.fail("logType 不能为空");
            }
            logService.submitLog(logEntity);
        }
        return RunResult.ok();
    }

    @HttpRequest("/push")
    public RunResult push(HttpContext request, @RequestBody LogEntity logEntity) {
        if (StringUtils.isBlank(logEntity.getLogType())) {
            return RunResult.fail("logType 不能为空");
        }

        String authorization = request.getRequest().header(HttpHeaderNames.AUTHORIZATION.toString());
        String jsonString = JSON.toJSONString(logEntity, SerializerFeature.SortField, SerializerFeature.MapSortField);

        logService.submitLog(logEntity);
        return RunResult.ok();
    }

}
