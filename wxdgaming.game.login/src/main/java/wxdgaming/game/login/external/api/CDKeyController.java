package wxdgaming.game.login.external.api;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.CacheHttpServletRequest;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.starter.batis.sql.WebSqlQueryCondition;
import wxdgaming.boot2.starter.excel.store.DataRepository;
import wxdgaming.game.login.cdkey.CDKeyService;
import wxdgaming.game.login.cfg.QCdkeyTable;
import wxdgaming.game.login.cfg.bean.QCdkey;
import wxdgaming.game.login.inner.InnerService;

import java.util.List;

/**
 * 激活码接口
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-01 10:57
 **/
@Slf4j
@RestController
@RequestMapping("/admin/cdkey")
public class CDKeyController implements InitPrint {

    final CDKeyService cdKeyService;
    final InnerService innerService;
    final DataRepository dataRepository;

    public CDKeyController(CDKeyService cdKeyService, InnerService innerService, DataRepository dataRepository) {
        this.cdKeyService = cdKeyService;
        this.innerService = innerService;
        this.dataRepository = dataRepository;
    }

    @RequestMapping("/gainCode")
    public RunResult gainCDKey(CacheHttpServletRequest request, @RequestParam("cdKeyId") int cdKeyId, @RequestParam("num") int num) {
        return cdKeyService.gain(cdKeyId, num);
    }

    @RequestMapping("/queryCode")
    public RunResult queryCDKey(CacheHttpServletRequest request, @RequestParam("cdKeyId") int cdKeyId) {
        return cdKeyService.queryByUid(cdKeyId);
    }

    @RequestMapping(value = "/queryList")
    public RunResult queryCDKeyList(@RequestBody WebSqlQueryCondition condition) {
        int pageIndex = condition.getPageIndex();
        int pageSize = condition.getPageSize();
        if (pageIndex < 1) pageIndex = 1;
        if (pageSize < 10) pageSize = 10;

        int skip = (pageIndex - 1) * pageSize;

        QCdkeyTable qCdkeyTable = this.dataRepository.dataTable(QCdkeyTable.class);
        List<JSONObject> list = qCdkeyTable.getDataList().stream()
                .map(QCdkey::toJSONObject)
                .skip(skip)
                .limit(pageSize)
                .peek(jsonObject -> jsonObject.put("rewards", jsonObject.getString("rewards")))
                .toList();
        return RunResult.ok().fluentPut("rowCount", qCdkeyTable.getDataList().size()).data(list);
    }

}
