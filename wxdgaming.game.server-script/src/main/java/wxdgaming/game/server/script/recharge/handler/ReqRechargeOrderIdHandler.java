package wxdgaming.game.server.script.recharge.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.httpclient5.HttpRequestPost;
import wxdgaming.boot2.starter.net.httpclient5.HttpResponse;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.game.center.entity.order.OrderBean;
import wxdgaming.game.message.recharge.ReqRechargeOrderId;
import wxdgaming.game.message.recharge.ResRechargeOrderId;
import wxdgaming.game.server.GameServerProperties;
import wxdgaming.game.server.bean.UserMapping;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.script.recharge.filter.IRechargeFilter;

import java.util.List;
import java.util.Map;

/**
 * 请求下单
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ReqRechargeOrderIdHandler extends HoldApplicationContext {

    private final GameServerProperties gameServerProperties;
    private final DataCenterService dataCenterService;

    Map<Integer, IRechargeFilter> rechargeFilterMap;

    public ReqRechargeOrderIdHandler(GameServerProperties gameServerProperties, DataCenterService dataCenterService) {
        this.gameServerProperties = gameServerProperties;
        this.dataCenterService = dataCenterService;
    }

    public Map<Integer, IRechargeFilter> getRechargeFilterMap() {
        if (rechargeFilterMap == null) {
            List<IRechargeFilter> list = applicationContextProvider.classWithSuperStream(IRechargeFilter.class).toList();
            rechargeFilterMap = MapOf.ofMap(list, IRechargeFilter::getRechargeType, v -> v);
        }
        return rechargeFilterMap;
    }

    /** 请求下单 */
    @ProtoRequest(ReqRechargeOrderId.class)
    public void reqRechargeOrderId(ProtoEvent event) {
        ReqRechargeOrderId message = event.buildMessage();
        UserMapping userMapping = event.bindData();
        Player player = userMapping.player();
        int productID = message.getProductID();
        int count = message.getCount();

        IRechargeFilter iRechargeFilter = rechargeFilterMap.get(1);
        if (iRechargeFilter != null && !iRechargeFilter.filter(productID, count, true)) {
            /* TODO 检查购买条件，比如等级，功能开启，购买次数等 */
            return;
        }

        OrderBean orderBean = new OrderBean();
        orderBean.setOrderId(String.valueOf(dataCenterService.getOrderHexid().newId()));

        orderBean.setGid(gameServerProperties.getGid());
        orderBean.setSid(gameServerProperties.getSid());
        orderBean.setSName(gameServerProperties.getName());
        orderBean.setLoginName(userMapping.getAccount());
        orderBean.setRoleId(player.getUid());
        orderBean.setRoleName(player.getName());
        orderBean.setCreateTime(System.currentTimeMillis());

        orderBean.setProductID(String.valueOf(productID));
        orderBean.setProductName(String.valueOf(productID));

        HttpResponse execute = HttpRequestPost.ofJson(gameServerProperties.getCenterUrl(), orderBean.toJSONString()).execute();
        if (!execute.isSuccess()) {
            log.error("访问中心服失败");
            return;
        }

        ResRechargeOrderId resRechargeOrderId = new ResRechargeOrderId();
        resRechargeOrderId.setOrderId(orderBean.getOrderId());
        resRechargeOrderId.setPayUrl("");

        player.write(resRechargeOrderId);

    }

}