package wxdgaming.game.slog;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.format.HexId;
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
@Singleton
public class SlogService implements InitPrint {

    final BootConfig bootConfig;
    final LogBusService logBusService;
    final ConcurrentHashMap<String, HexId> logHexIdMap = new ConcurrentHashMap<>();

    @Inject
    public SlogService(BootConfig bootConfig, LogBusService logBusService) {
        this.bootConfig = bootConfig;
        this.logBusService = logBusService;
    }

    public long newLogId(String logType) {
        return logHexIdMap.computeIfAbsent(logType, (key) -> new HexId(bootConfig.sid())).newId();
    }


    public void addLog(AbstractSLog abstractSLog) {
        LogEntity logEntity = new LogEntity();
        logEntity.setUid(newLogId(abstractSLog.logType()));
        logEntity.setCreateTime(System.currentTimeMillis());
        logEntity.setLogType(abstractSLog.logType());
        logEntity.getLogData().putAll(abstractSLog.toJSONObject());
        if (abstractSLog.getSid() == 0) {
            abstractSLog.setSid(bootConfig.sid());
        }
        if (abstractSLog.getCurSid() == 0) {
            abstractSLog.setCurSid(bootConfig.sid());
        }
        logBusService.addLog(logEntity);
    }

}
