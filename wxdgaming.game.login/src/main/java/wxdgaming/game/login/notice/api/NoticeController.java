package wxdgaming.game.login.notice.api;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.CacheHttpServletRequest;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.starter.batis.sql.WebSqlQueryCondition;
import wxdgaming.boot2.starter.excel.store.DataRepository;
import wxdgaming.game.login.cfg.QNoticeTable;
import wxdgaming.game.login.cfg.bean.QNotice;
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
@RequestMapping("/notice")
public class NoticeController implements InitPrint {

    final InnerService innerService;
    final DataRepository dataRepository;

    public NoticeController(InnerService innerService, DataRepository dataRepository) {
        this.innerService = innerService;
        this.dataRepository = dataRepository;
    }

    @RequestMapping(value = "/list")
    public RunResult list(CacheHttpServletRequest context) {
        QNoticeTable noticeTable = this.dataRepository.dataTable(QNoticeTable.class);
        List<JSONObject> list = noticeTable.getDataList().stream()
                .map(QNotice::toJSONObject)
                .toList();
        return RunResult.ok().fluentPut("rowCount", noticeTable.getDataList().size()).data(list);
    }

    @RequestMapping(value = "/queryList")
    public RunResult queryCDKeyList(CacheHttpServletRequest context, @RequestBody WebSqlQueryCondition condition) {
        int pageIndex = condition.getPageIndex();
        int pageSize = condition.getPageSize();
        if (pageIndex < 1) pageIndex = 1;
        if (pageSize < 10) pageSize = 10;

        int skip = (pageIndex - 1) * pageSize;

        QNoticeTable noticeTable = this.dataRepository.dataTable(QNoticeTable.class);
        List<JSONObject> list = noticeTable.getDataList().stream()
                .map(QNotice::toJSONObject)
                .skip(skip)
                .limit(pageSize)
                .toList();
        return RunResult.ok().fluentPut("rowCount", noticeTable.getDataList().size()).data(list);
    }

}
