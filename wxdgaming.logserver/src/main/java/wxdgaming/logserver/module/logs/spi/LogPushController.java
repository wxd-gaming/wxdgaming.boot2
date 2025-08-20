package wxdgaming.logserver.module.logs.spi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.ann.RequestBody;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.io.Objects;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.util.Md5Util;
import wxdgaming.boot2.starter.net.ann.HttpRequest;
import wxdgaming.boot2.starter.net.ann.RequestMapping;
import wxdgaming.boot2.starter.net.http.HttpDataAction;
import wxdgaming.boot2.starter.net.server.http.HttpContext;
import wxdgaming.logserver.LogServerProperties;
import wxdgaming.logserver.bean.LogEntity;
import wxdgaming.logserver.module.logs.LogService;

import java.util.List;
import java.util.TreeMap;

/**
 * 日志接口
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-07 17:05
 **/
@Slf4j
@Singleton
@RequestMapping("/log/push")
public class LogPushController {

    private final LogServerProperties logServerProperties;
    final LogService logService;

    @Inject
    public LogPushController(LogServerProperties logServerProperties, LogService logService) {
        this.logServerProperties = logServerProperties;
        this.logService = logService;
    }

    @HttpRequest("/List")
    public RunResult pushList(HttpContext request, @RequestBody String json) {
        if (StringUtils.isBlank(json)) {
            return RunResult.fail("log list 不能为空");
        }

        TreeMap<String, String> map = JSON.parseObject(json, new TypeReference<TreeMap<String, String>>() {});
        String sign = map.remove("sign");

        String signBefore = HttpDataAction.httpData(map) + logServerProperties.getJwtKey();
        String selfSign = Md5Util.md5DigestEncode(signBefore);
        if (!Objects.equals(sign, selfSign)) {
            log.error("LogPushController sign={} signBefore={}", sign, signBefore);
            return RunResult.fail("sign 签名错误");
        }
        List<LogEntity> logEntityList = JSON.parseArray(map.get("data"), LogEntity.class);


        for (LogEntity logEntity : logEntityList) {
            if (StringUtils.isBlank(logEntity.getLogType())) {
                return RunResult.fail("logType 不能为空");
            }
            logService.submitLog(logEntity);
        }
        return RunResult.ok();
    }

}
