package wxdgaming.game.bean.goods;

import com.alibaba.fastjson.JSON;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import wxdgaming.game.core.ReasonArgs;
import wxdgaming.game.message.bag.BagType;

/**
 * 奖励道具参数
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-25 14:12
 **/
@Getter
@SuperBuilder(setterPrefix = "set")
public class BagChangeArgs {

    @Builder.Default
    private BagType bagType = BagType.Bag;
    private ReasonArgs reasonArgs;
    @Builder.Default
    private boolean bagFullNoticeClient = true;
    @Builder.Default
    private boolean bagFullSendMail = false;

    @Override public String toString() {
        return "BagChangeArgs" + JSON.toJSONString(this);
    }

}
