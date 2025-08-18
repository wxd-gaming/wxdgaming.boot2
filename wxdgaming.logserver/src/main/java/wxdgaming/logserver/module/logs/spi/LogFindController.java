package wxdgaming.logserver.module.logs.spi;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.ann.RequestParam;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.starter.net.ann.HttpRequest;
import wxdgaming.boot2.starter.net.ann.RequestMapping;
import wxdgaming.logserver.module.logs.LogService;

import java.util.List;

/**
 * 日志
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-09 18:06
 **/
@Slf4j
@Singleton
@RequestMapping("/api/log")
public class LogFindController implements InitPrint {

    final LogService logService;

    @Inject
    public LogFindController(LogService logService) {
        this.logService = logService;
    }

    @HttpRequest("/nav")
    public RunResult nav() {
        List<JSONObject> nav = logService.nav();
        return RunResult.ok().data(nav);
    }

    @HttpRequest("/logTitle")
    public RunResult logTitle(@RequestParam("tableName") String tableName) {
        List<JSONObject> list = logService.logTitle(tableName);
        return RunResult.ok().data(list);
    }

    @HttpRequest("/logPage")
    public RunResult logPage(
            @RequestParam("tableName") String tableName,
            @RequestParam("pageIndex") int pageIndex,
            @RequestParam("pageSize") int pageSize,
            @RequestParam("minDay") String minDay,
            @RequestParam("maxDay") String maxDay,
            @RequestParam("where") String where
    ) {
        return logService.logPage(tableName, pageIndex, pageSize, minDay, maxDay, where);
    }

}
