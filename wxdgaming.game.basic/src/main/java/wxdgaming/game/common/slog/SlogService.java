package wxdgaming.game.common.slog;

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
    }

    public long newLogId(String logType) {
        return logHexIdMap.computeIfAbsent(logType, (key) -> new HexId(bootstrapProperties.getSid())).newId();
    }


    public void pushLog(AbstractLog abstractLog) {
        LogEntity logEntity = new LogEntity();
        logEntity.setUid(newLogId(abstractLog.logType()));
        logEntity.setCreateTime(System.currentTimeMillis());
        logEntity.setLogType(abstractLog.logType());
        logEntity.getLogData().putAll(abstractLog.toJSONObject());
        if (abstractLog instanceof AbstractSlog abstractSlog) {
            if (abstractSlog.getSid() == 0) {
                abstractSlog.setSid(bootstrapProperties.getSid());
            }
            if (abstractSlog.getCurSid() == 0) {
                abstractSlog.setCurSid(bootstrapProperties.getSid());
            }
        }
        logBusService.pushLog(logEntity);
    }

    public void updateLog(long uid, long time, AbstractLog abstractLog) {
        LogEntity logEntity = new LogEntity();
        logEntity.setUid(uid);
        logEntity.setCreateTime(time);
        logEntity.setLogType(abstractLog.logType());
        logEntity.getLogData().putAll(abstractLog.toJSONObject());
        if (abstractLog instanceof AbstractSlog abstractSlog) {
            if (abstractSlog.getSid() == 0) {
                abstractSlog.setSid(bootstrapProperties.getSid());
            }
            if (abstractSlog.getCurSid() == 0) {
                abstractSlog.setCurSid(bootstrapProperties.getSid());
            }
        }
        logBusService.updateLog(logEntity);
    }

}
