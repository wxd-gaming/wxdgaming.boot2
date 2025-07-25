package wxdgaming.game.bean.goods;

import com.alibaba.fastjson.JSON;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import wxdgaming.game.core.ReasonArgs;
import wxdgaming.game.message.bag.BagType;

/**
 * 背包变更参数变量
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
    /** 当背包出现异常是否发送提示到客户端，比如背包已满，或者道具不足 ,TODO 当 {@link #bagFullSendMail}为true时，此参数无效 */
    @Builder.Default
    private boolean bagErrorNoticeClient = true;
    /** 只有在添加道具的时候，背包已满是否通过发送邮件的方式发放道具 */
    @Builder.Default
    private boolean bagFullSendMail = false;

    @Override public String toString() {
        return this.getClass().getSimpleName() + JSON.toJSONString(this);
    }

}
