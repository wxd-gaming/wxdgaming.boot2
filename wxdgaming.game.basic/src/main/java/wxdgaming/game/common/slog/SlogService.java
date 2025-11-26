package wxdgaming.game.common.slog;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.BootstrapProperties;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.format.HexId;
import wxdgaming.game.common.bean.slog.AbstractLog;
import wxdgaming.game.common.bean.slog.AbstractSlog;
import wxdgaming.logbus.LogBusService;
import wxdgaming.logbus.LogEntity;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 日志服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-12 19:17
 **/
@Slf4j
@Service
public class SlogService implements InitPrint {

    final BootstrapProperties bootstrapProperties;
    final LogBusService logBusService;
    final ConcurrentHashMap<String, HexId> logHexIdMap = new ConcurrentHashMap<>();

    public SlogService(BootstrapProperties bootstrapProperties, LogBusService logBusService) {
        this.bootstrapProperties = bootstrapProperties;
        this.logBusService = logBusService;
        log.debug("this.logBusService:{}", this.logBusService.getClass());
    }

    public long newLogId(String logType) {
        return logHexIdMap.computeIfAbsent(logType, (key) -> new HexId(bootstrapProperties.getSid())).newId();
    }


    public void pushLog(AbstractLog abstractLog) {
        if (abstractLog instanceof AbstractSlog abstractSlog) {
            if (abstractSlog.getSid() == 0) {
                abstractSlog.setSid(bootstrapProperties.getSid());
            }
            if (abstractSlog.getCurSid() == 0) {
                abstractSlog.setCurSid(bootstrapProperties.getSid());
            }
        }
        pushLog(abstractLog.logType(), abstractLog.toJSONObject());
    }

    public void pushLog(String logType, JSONObject jsonObject) {
        LogEntity logEntity = new LogEntity();
        logEntity.setUid(newLogId(logType));
        logEntity.setCreateTime(System.currentTimeMillis());
        logEntity.setLogType(logType);
        logEntity.getLogData().putAll(jsonObject);
        logBusService.pushLog(logEntity);
    }

    public void updateLog(long uid, long time, AbstractLog abstractLog) {
        if (abstractLog instanceof AbstractSlog abstractSlog) {
            if (abstractSlog.getSid() == 0) {
                abstractSlog.setSid(bootstrapProperties.getSid());
            }
            if (abstractSlog.getCurSid() == 0) {
                abstractSlog.setCurSid(bootstrapProperties.getSid());
            }
        }
        updateLog(uid, time, abstractLog.logType(), abstractLog.toJSONObject());
    }

    public void updateLog(long uid, long time, String logType, JSONObject jsonObject) {
        LogEntity logEntity = new LogEntity();
        logEntity.setUid(uid);
        logEntity.setCreateTime(time);
        logEntity.setLogType(logType);
        logEntity.getLogData().putAll(jsonObject);
        logBusService.updateLog(logEntity);
    }

}
