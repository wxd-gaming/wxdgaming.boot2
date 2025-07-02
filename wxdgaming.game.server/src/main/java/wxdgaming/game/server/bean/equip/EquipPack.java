package wxdgaming.game.server.bean.equip;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.collection.Table;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.game.bean.goods.Equipment;

/**
 * 装备拦
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-07-02 11:29
 **/
@Getter
@Setter
public class EquipPack extends ObjectBase {

    private Table<EquipConst.EquipPanel, EquipConst.EquipPost, Equipment> equipTable = new Table<>();

}
