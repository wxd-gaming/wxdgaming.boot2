package wxdgaming.game.server.bean.bag;

import lombok.Getter;
import wxdgaming.game.bean.goods.Item;

import java.util.Objects;

/**
 * 变化
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-07 11:47
 **/
@Getter
public class ItemGrid {

    private final int grid;
    private final Item item;

    public ItemGrid(int grid, Item item) {
        this.grid = grid;
        this.item = item;
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ItemGrid that = (ItemGrid) o;
        return Objects.equals(getItem(), that.getItem());
    }

    @Override public int hashCode() {
        return Objects.hashCode(getItem());
    }
}
