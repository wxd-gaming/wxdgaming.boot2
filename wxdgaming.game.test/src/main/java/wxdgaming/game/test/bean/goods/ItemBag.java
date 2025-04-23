package wxdgaming.game.test.bean.goods;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 道具背包
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-23 17:21
 **/
@Getter
@Setter
public class ItemBag {

    /** 初始化格子数 */
    private int initGrid = 100;
    /** 购买的格子数 */
    private int buyGrid = 0;
    /** 使用道具激活的格子数 */
    private int googsGrid = 0;

    /** 货币 */
    private HashMap<Integer, Long> currencyMap = new HashMap<>();
    private ArrayList<Item> items = new ArrayList<>();

    public ItemBag() {
    }

    public ItemBag(int initGrid) {
        this.initGrid = initGrid;
    }

    public boolean isFull() {
        return items.size() >= initGrid + buyGrid + googsGrid;
    }

}
