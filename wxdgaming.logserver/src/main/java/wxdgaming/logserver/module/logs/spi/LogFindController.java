package wxdgaming.logserver.module.logs.spi;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.InitPrint;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.logserver.module.logs.LogService;

import java.util.List;

/**
 * 日志
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-09 18:06
 **/
@Slf4j
@RestController
@RequestMapping("/log/find")
public class LogFindController implements InitPrint {

    final LogService logService;

    public LogFindController(LogService logService) {
        this.logService = logService;
    }

    @RequestMapping("/nav")
    public RunResult nav() {
        List<JSONObject> nav = logService.nav();
        return RunResult.ok().data(nav);
    }

    @RequestMapping("/title")
    public RunResult logTitle(@RequestParam("tableName") String tableName) {
        AssertUtil.assertTrue(StringUtils.isNotBlank(tableName), "tableName不能为空");
        return logService.logTitle(tableName);
    }

    @RequestMapping("/page")
    public RunResult logPage(
            @RequestParam("tableName") String tableName,
            @RequestParam("pageIndex") int pageIndex,
            @RequestParam("pageSize") int pageSize,
            @RequestParam("minDay") String minDay,
            @RequestParam("maxDay") String maxDay,
            @RequestParam("where") String where,
            @RequestParam("order") String orderJson
    ) {
        return logService.logPage(tableName, pageIndex, pageSize, minDay, maxDay, where, orderJson);
    }

}
