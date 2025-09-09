package wxdgaming.game.login.inner.api;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.CacheHttpServletRequest;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.SpringUtil;
import wxdgaming.boot2.core.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.game.login.entity.server.InnerServerInfoBean;
import wxdgaming.game.login.cdkey.CDKeyService;
import wxdgaming.game.login.inner.InnerService;

import java.util.ArrayList;
import java.util.List;

/**
 * 登录接口
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-07 18:41
 **/
@Slf4j
@RestController
@RequestMapping(value = "/inner")
public class InnerController extends HoldApplicationContext {

    final InnerService innerService;
    final CDKeyService cdKeyService;

    public InnerController(InnerService innerService, CDKeyService cdKeyService) {
        this.innerService = innerService;
        this.cdKeyService = cdKeyService;
    }

    @RequestMapping("/cdkey/use")
    public RunResult use(CacheHttpServletRequest request, @RequestBody JSONObject data) {
        int sid = data.getIntValue("sid");
        String account = data.getString("account");
        long rid = data.getLongValue("rid");
        return cdKeyService.use(request.getParameter("key"), sid, account, rid);
    }

    @RequestMapping(value = "/registerGame")
    public RunResult registerGame(CacheHttpServletRequest request, @RequestBody JSONObject data) {
        ArrayList<Integer> sidList = data.getObject("sidList", new TypeReference<ArrayList<Integer>>() {});
        String jsonBean = data.getString("serverBean");
        InnerServerInfoBean serverBean = FastJsonUtil.parse(jsonBean, InnerServerInfoBean.class);
        for (Integer sid : sidList) {
            InnerServerInfoBean clone = serverBean.clone();
            clone.setServerId(sid);
            clone.setHost(SpringUtil.getClientIp(request));
            clone.setLastSyncTime(MyClock.millis());
            innerService.getInnerGameServerInfoMap().put(sid, clone);
            innerService.getSqlDataHelper().getDataBatch().save(clone);
        }
        return RunResult.ok();
    }

    @RequestMapping(value = "/gameServerList")
    public RunResult gameServerList(HttpServletRequest context, @RequestBody JSONObject data) {
        List<InnerServerInfoBean> list = innerService.getInnerGameServerInfoMap().values()
                .stream()
                .toList();
        return RunResult.ok().data(list);
    }

}
