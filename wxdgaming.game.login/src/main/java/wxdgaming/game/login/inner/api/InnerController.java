package wxdgaming.game.login.inner.api;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.core.SpringUtil;
import wxdgaming.game.basic.login.bean.info.InnerServerInfoBean;
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

    public InnerController(InnerService innerService) {
        this.innerService = innerService;
    }

    @RequestMapping(value = "/registerGame")
    public RunResult registerGame(HttpServletRequest context, @RequestBody JSONObject data) {
        ArrayList<Integer> sidList = data.getObject("sidList", new TypeReference<ArrayList<Integer>>() {});
        String jsonBean = data.getString("serverBean");
        InnerServerInfoBean serverBean = FastJsonUtil.parse(jsonBean, InnerServerInfoBean.class);
        for (Integer sid : sidList) {
            InnerServerInfoBean clone = serverBean.clone();
            clone.setServerId(sid);
            clone.setHost(SpringUtil.getClientIp(context));
            clone.setLastSyncTime(MyClock.millis());
            innerService.getInnerGameServerInfoMap().put(sid, clone);
            innerService.getSqlDataHelper().getDataBatch().save(clone);
        }
        return RunResult.ok();
    }

    @RequestMapping(value = "/registerGateway")
    public RunResult registerGateway(HttpServletRequest context, @RequestBody JSONObject data) {
        Integer sid = data.getInteger("sid");
        String jsonBean = data.getString("serverBean");
        InnerServerInfoBean serverBean = FastJsonUtil.parse(jsonBean, InnerServerInfoBean.class);
        serverBean.setHost(SpringUtil.getClientIp(context));
        serverBean.setLastSyncTime(MyClock.millis());
        innerService.getInnerGatewayServerInfoMap().put(sid, serverBean);
        return gameMainServerList();
    }

    public RunResult gameMainServerList() {
        List<InnerServerInfoBean> list = innerService.getInnerGameServerInfoMap().values()
                .stream()
                .filter(v -> v.getMainId() == v.getServerId())
                .sorted((o1, o2) -> Integer.compare(o2.getServerId(), o1.getServerId()))
                .toList();
        return RunResult.ok().data(list);
    }

    @RequestMapping(value = "/gameServerList")
    public RunResult gameServerList(HttpServletRequest context, @RequestBody JSONObject data) {
        List<InnerServerInfoBean> list = innerService.getInnerGameServerInfoMap().values()
                .stream()
                .sorted((o1, o2) -> Integer.compare(o2.getServerId(), o1.getServerId()))
                .toList();
        return RunResult.ok().data(list);
    }

    @RequestMapping(value = "/gatewayServerList")
    public RunResult gatewayServerList(HttpServletRequest context, @RequestBody JSONObject data) {
        List<InnerServerInfoBean> list = innerService.getInnerGatewayServerInfoMap().values()
                .stream()
                .sorted((o1, o2) -> Integer.compare(o2.getServerId(), o1.getServerId()))
                .toList();
        return RunResult.ok().data(list);
    }

}
