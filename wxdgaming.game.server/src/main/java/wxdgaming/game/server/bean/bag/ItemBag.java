package wxdgaming.game.server.bean.bag;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.game.bean.goods.Item;

import java.util.*;
import java.util.stream.Stream;

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
    /** 道具格子 */
    private Item[] itemGrids;

    public ItemBag() {
    }

    public ItemBag(int initGrid) {
        this.initGrid = initGrid;
    }

    public ItemBag resetGrid() {
        int newLength = maxGrid();
        if (itemGrids == null) {
            itemGrids = new Item[newLength];
        } else {
            AssertUtil.assertTrue(itemGrids.length <= newLength, "背包格子数异常，最大格子小于当前格子数");
            if (itemGrids.length < newLength) {
                itemGrids = Arrays.copyOf(itemGrids, newLength);
            }
        }
        return this;
    }

    /** 获取道具，根据uid */
    public ItemGrid itemGridById(long uid) {
        for (int i = 0; i < itemGrids.length; i++) {
            Item item = itemGrids[i];
            if (item == null) continue;
            if (item.getUid() != uid) continue;
            return new ItemGrid(i, item);
        }
        return null;
    }

    /** 根据配置id，获取道具 */
    public List<ItemGrid> itemGridListByCfgId(int cfgId) {
        List<ItemGrid> itemGridList = new ArrayList<>();
        for (int i = 0; i < itemGrids.length; i++) {
            Item item = itemGrids[i];
            if (item == null) continue;
            if (item.getCfgId() != cfgId) continue;
            itemGridList.add(new ItemGrid(i, item));
        }
        return itemGridList;
    }

    /** 根据配置id，获取道具 */
    public long itemCountByCfgId(int cfgId) {
        long itemCount = 0;
        for (int i = 0; i < itemGrids.length; i++) {
            Item item = itemGrids[i];
            if (item == null) continue;
            if (item.getCfgId() != cfgId) continue;
            itemCount += item.getCount();
        }
        return itemCount;
    }

    /** 添加 */
    public ItemGrid add(Item item) {
        for (int i = 0; i < itemGrids.length; i++) {
            if (itemGrids[i] == null) {
                itemGrids[i] = item;
                return new ItemGrid(i, item);
            }
        }
        return null;
    }

    /** 删除 */
    public void remove(ItemGrid itemGrid) {
        itemGrids[itemGrid.getGrid()] = null;
    }

    /** 删除 */
    public ItemGrid remove(Item item) {
        for (int i = 0; i < itemGrids.length; i++) {
            if (Objects.equals(itemGrids[i], item)) {
                itemGrids[i] = null;
                return new ItemGrid(i, item);
            }
        }
        return null;
    }

    /** 判定当前背包是否已满 */
    public boolean checkFull() {
        return freeGrid() < 1;
    }

    /** 当前背包最大格子数 */
    public int maxGrid() {
        return (initGrid + buyGrid + googsGrid);
    }

    /** 剩余格子数 */
    public int freeGrid() {
        int free = 0;
        for (int i = 0; i < itemGrids.length; i++) {
            if (itemGrids[i] == null) {
                free++;
            }
        }
        return free;
    }

}
