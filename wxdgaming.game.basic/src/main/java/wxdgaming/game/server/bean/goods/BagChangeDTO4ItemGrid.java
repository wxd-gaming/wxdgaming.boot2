package wxdgaming.game.server.bean.goods;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * 背包变更参数变量
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-25 14:12
 **/
@Getter
@SuperBuilder(setterPrefix = "set")
public class BagChangeDTO4ItemGrid extends BagChangeDTO {

    /** key:格子, value:数量 */
    private Map<ItemGrid, Long> itemMap;

}
