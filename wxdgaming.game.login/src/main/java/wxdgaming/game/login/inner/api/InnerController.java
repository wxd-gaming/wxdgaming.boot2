package wxdgaming.game.login.inner.api;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.RequestBody;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.net.ann.HttpRequest;
import wxdgaming.boot2.starter.net.ann.RequestMapping;
import wxdgaming.boot2.starter.net.server.http.HttpContext;
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
@Singleton
@RequestMapping(value = "/inner")
public class InnerController extends HoldRunApplication {

    final InnerService innerService;

    @Inject
    public InnerController(InnerService innerService) {
        this.innerService = innerService;
    }

    @HttpRequest
    public RunResult registerGame(HttpContext context, @RequestBody JSONObject data) {
        ArrayList<Integer> sidList = data.getObject("sidList", new TypeReference<ArrayList<Integer>>() {});
        String jsonBean = data.getString("serverBean");
        InnerServerInfoBean serverBean = FastJsonUtil.parse(jsonBean, InnerServerInfoBean.class);
        for (Integer sid : sidList) {
            InnerServerInfoBean clone = serverBean.clone();
            clone.setServerId(sid);
            clone.setHost(context.getIp());
            clone.setLastSyncTime(MyClock.millis());
            innerService.getInnerGameServerInfoMap().put(sid, clone);
            innerService.getSqlDataHelper().getDataBatch().save(clone);
        }
        return RunResult.ok();
    }

    @HttpRequest
    public RunResult registerGateway(HttpContext context, @RequestBody JSONObject data) {
        Integer sid = data.getInteger("sid");
        String jsonBean = data.getString("serverBean");
        InnerServerInfoBean serverBean = FastJsonUtil.parse(jsonBean, InnerServerInfoBean.class);
        serverBean.setHost(context.getIp());
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

    @HttpRequest
    public RunResult gameServerList(HttpContext context, @RequestBody JSONObject data) {
        List<InnerServerInfoBean> list = innerService.getInnerGameServerInfoMap().values()
                .stream()
                .sorted((o1, o2) -> Integer.compare(o2.getServerId(), o1.getServerId()))
                .toList();
        return RunResult.ok().data(list);
    }

    @HttpRequest
    public RunResult gatewayServerList(HttpContext context, @RequestBody JSONObject data) {
        List<InnerServerInfoBean> list = innerService.getInnerGatewayServerInfoMap().values()
                .stream()
                .sorted((o1, o2) -> Integer.compare(o2.getServerId(), o1.getServerId()))
                .toList();
        return RunResult.ok().data(list);
    }

}
