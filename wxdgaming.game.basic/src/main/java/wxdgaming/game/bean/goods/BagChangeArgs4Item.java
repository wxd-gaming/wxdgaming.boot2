package wxdgaming.game.bean.goods;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 奖励道具参数
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-25 14:12
 **/
@Getter
@SuperBuilder(setterPrefix = "set")
public class BagChangeArgs4Item extends BagChangeArgs {

    private List<Item> itemList;

}
