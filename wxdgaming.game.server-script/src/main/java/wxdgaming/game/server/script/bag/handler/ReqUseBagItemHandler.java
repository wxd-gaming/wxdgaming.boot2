package wxdgaming.game.server.script.bag.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.game.message.bag.BagType;
import wxdgaming.game.message.bag.ReqUseBagItem;
import wxdgaming.game.server.bean.UserMapping;
import wxdgaming.game.server.bean.bag.ItemBag;
import wxdgaming.game.server.bean.goods.BagChangeDTO4ItemGrid;
import wxdgaming.game.server.bean.goods.ItemGrid;
import wxdgaming.game.server.bean.reason.ReasonConst;
import wxdgaming.game.server.bean.reason.ReasonDTO;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.bag.BagService;
import wxdgaming.game.server.script.tips.TipsService;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求使用道具
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ReqUseBagItemHandler {

    final BagService bagService;
    final TipsService tipsService;

    public ReqUseBagItemHandler(BagService bagService, TipsService tipsService) {
        this.bagService = bagService;
        this.tipsService = tipsService;
    }

    /** 请求使用道具 */
    @ProtoRequest(ReqUseBagItem.class)
    public void reqUseBagItem(ProtoEvent event) {

        ReqUseBagItem message = event.buildMessage();
        UserMapping userMapping = event.bindData();
        Player player = userMapping.player();
        BagChangeDTO4ItemGrid.BagChangeDTO4ItemGridBuilder<?, ?> builder = BagChangeDTO4ItemGrid.builder();
        ReasonDTO reasonDTO = ReasonDTO.of(ReasonConst.USE_ITEM);
        builder.setReasonDTO(reasonDTO);

        BagType bagType = message.getBagType();
        ItemBag itemBag = player.getBagPack().itemBag(bagType);
        Map<ItemGrid, Long> itemGridMap = new HashMap<>();
        for (Map.Entry<Long, Long> entry : message.getUseMap().entrySet()) {
            long uid = entry.getKey();
            long count = entry.getValue();
            ItemGrid itemGrid = itemBag.itemGridById(uid);
            if (itemGrid == null) {
                tipsService.tips(player, "玩家使用道具失败，道具不存在");
                return;
            }
            itemGridMap.put(itemGrid, count);
        }

        builder.setItemMap(itemGridMap);
        BagChangeDTO4ItemGrid bagChangeDTO = builder.build();
        bagService.use(player, bagChangeDTO);
    }

}