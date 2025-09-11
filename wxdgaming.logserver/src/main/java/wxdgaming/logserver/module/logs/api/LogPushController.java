package wxdgaming.logserver.module.logs.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.CacheHttpServletRequest;
import wxdgaming.boot2.core.SpringUtil;
import wxdgaming.boot2.core.io.Objects;
import wxdgaming.boot2.core.lang.AssertException;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.util.Md5Util;
import wxdgaming.boot2.starter.net.http.HttpDataAction;
import wxdgaming.logserver.LogServerProperties;
import wxdgaming.logserver.bean.LogEntity;
import wxdgaming.logserver.module.logs.LogService;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
        for (LogEntity logEntity : logEntityList) {
            if (StringUtils.isBlank(logEntity.getLogType())) {
                logService.saveErrorLog("logType 空", logEntity.toJSONString(), "pushList");
                continue;
            }
            logService.submitLog(logEntity);
        }
        return RunResult.ok();
    }

    @RequestMapping("/m")
    public String m(CacheHttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        return "ok";
    }

    @RequestMapping("/update")
    public RunResult updateList(HttpServletRequest request) throws Exception {

        List<LogEntity> logEntityList = checkSign(request);

        for (LogEntity logEntity : logEntityList) {
            if (StringUtils.isBlank(logEntity.getLogType())) {
                logService.saveErrorLog("logType 空", logEntity.toJSONString(), "updateList");
                continue;
            }
            if (logEntity.getUid() == 0) {
                logService.saveErrorLog("uid 为0", logEntity.toJSONString(), "updateList");
                continue;
            }
            logService.updateLog(logEntity);
        }
        return RunResult.ok();
    }

}
