package wxdgaming.game.login.external.api;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.CacheHttpServletRequest;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.core.util.NumberUtil;
import wxdgaming.boot2.starter.batis.EntityIntegerUID;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.boot2.starter.batis.sql.WebSqlQueryCondition;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.game.login.entity.NoticeEntity;
import wxdgaming.game.login.notice.NoticeService;
import wxdgaming.game.util.Util;

import java.util.Collection;
import java.util.List;

/**
 * 礼包码接口
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-01 10:57
 **/
@Slf4j
@RestController
@RequestMapping("/admin/notice")
public class AdminNoticeController implements InitPrint {

    final NoticeService noticeService;
    final SqlDataHelper sqlDataHelper;

    public AdminNoticeController(NoticeService noticeService, PgsqlDataHelper pgsqlDataHelper) {
        this.noticeService = noticeService;
        this.sqlDataHelper = pgsqlDataHelper;
    }

    @RequestMapping(value = "/edit")
    public RunResult add(CacheHttpServletRequest context,
                         @RequestParam(value = "uid", required = false) String uidStr,
                         @RequestParam("title") String title,
                         @RequestParam("content") String content,
                         @RequestParam("startTime") String startTime,
                         @RequestParam("endTime") String endTime) {
        int uid = 0;
        if (StringUtils.isNotBlank(uidStr)) {
            uid = NumberUtil.parseInt(uidStr, 0);
        }
        int maxUid = noticeService.getDataTable().getList().stream().mapToInt(EntityIntegerUID::getUid).max().orElse(0);
        NoticeEntity noticeEntity = noticeService.getDataTable().get(uid);
        if (noticeEntity == null) {
            noticeEntity = new NoticeEntity();
            noticeEntity.setUid(maxUid + 1);
            noticeEntity.setCreateTime(MyClock.millis());
        }
        AssertUtil.isTrue(StringUtils.isNotBlank(title), "标题不能为空");
        AssertUtil.isTrue(StringUtils.isNotBlank(content), "内容不能为空");
        noticeEntity.setTitle(title);
        noticeEntity.setContent(content);
        if (StringUtils.isBlank(startTime)) {
            noticeEntity.setStartTime(0);
        } else {
            noticeEntity.setStartTime(Util.parseWebDate(startTime));
        }
        if (StringUtils.isBlank(endTime)) {
            noticeEntity.setEndTime(0);
        } else {
            noticeEntity.setEndTime(Util.parseWebDate(endTime));
        }
        this.sqlDataHelper.save(noticeEntity);
        this.noticeService.getDataTable().loadAll();
        return RunResult.ok().msg("成功");
    }

    @RequestMapping(value = "/del")
    public RunResult del(CacheHttpServletRequest context, @RequestParam(value = "uid") int uid) {
        this.noticeService.del(uid);
        return RunResult.ok().msg("删除成功");
    }

    @RequestMapping(value = "/queryList")
    public RunResult queryList(CacheHttpServletRequest context, @RequestBody WebSqlQueryCondition condition) {
        int pageIndex = condition.getPageIndex();
        int pageSize = condition.getPageSize();
        if (pageIndex < 1) pageIndex = 1;
        if (pageSize < 10) pageSize = 10;

        int skip = (pageIndex - 1) * pageSize;

        Collection<NoticeEntity> entities = noticeService.getDataTable().getList();
        List<JSONObject> list = entities.stream()
                .distinct()
                .skip(skip)
                .limit(pageSize)
                .map(entity -> {
                    JSONObject jsonObject = entity.toJSONObject();
                    jsonObject.put("createTime", Util.formatWebDate(entity.getCreateTime()));
                    jsonObject.put("startTime", Util.formatWebDate(entity.getStartTime()));
                    jsonObject.put("endTime", Util.formatWebDate(entity.getEndTime()));
                    return jsonObject;
                })
                .toList();
        return RunResult.ok().fluentPut("rowCount", list.size()).data(list);
    }

}
