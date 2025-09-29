package wxdgaming.game.server.bean.goods;

import lombok.Getter;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 格子，
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-07 11:47
 **/
@Getter
public class ItemGrid extends ObjectBase {

    private final int grid;
    private final Item item;

    public ItemGrid(int grid, Item item) {
        this.grid = grid;
        this.item = item;
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ItemGrid itemGrid = (ItemGrid) o;
        return getGrid() == itemGrid.getGrid();
    }

    @Override public int hashCode() {
        return getGrid();
    }

}
