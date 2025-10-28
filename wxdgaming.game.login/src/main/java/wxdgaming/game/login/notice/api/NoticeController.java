package wxdgaming.game.login.notice.api;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.CacheHttpServletRequest;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.core.util.NumberUtil;
import wxdgaming.game.login.entity.NoticeEntity;
import wxdgaming.game.login.notice.NoticeService;

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
@RequestMapping("/notice")
public class NoticeController implements InitPrint {

    final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @RequestMapping(value = "/list")
    public RunResult list(CacheHttpServletRequest context) {
        Collection<NoticeEntity> noticeEntities = this.noticeService.list();
        long millis = MyClock.millis();
        List<JSONObject> list = noticeEntities.stream()
                .distinct()
                .filter(entity -> NumberUtil.check(entity.getStartTime(), millis, entity.getEndTime()))
                .map(NoticeEntity::toJSONObject)
                .toList();
        return RunResult.ok().data(list);
    }

}
