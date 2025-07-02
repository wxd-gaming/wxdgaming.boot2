package wxdgaming.game.bean.goods;

import lombok.Getter;

/**
 * 道具类型
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-09 14:40
 **/
@Getter
public abstract class ItemTypeConst {

    /** 默认处理 */
    public static final ItemTypeConst NONE = new ItemTypeConst(0, 0, 0) {};
    /** 货币 */
    public static final ItemTypeConst CurrencyType = new ItemTypeConst(1, 0, 0) {};
    /** 钻石 */
    public static final ItemTypeConst Gold = new ItemTypeConst(1, 1, 0) {};
    /** 绑定钻石 */
    public static final ItemTypeConst BindGold = new ItemTypeConst(1, 2, 0) {};
    /** 金币 */
    public static final ItemTypeConst Money = new ItemTypeConst(1, 3, 0) {};
    /** 绑定金币 */
    public static final ItemTypeConst BindMoney = new ItemTypeConst(1, 4, 0) {};
    /** 经验值 */
    public static final ItemTypeConst EXP = new ItemTypeConst(1, 5, 0) {};
    /** 公会货币 */
    public static final ItemTypeConst GuildCurrencyType = new ItemTypeConst(1, 2, 0) {};
    /** 装备类型 */
    public static final ItemTypeConst EquipType = new ItemTypeConst(2, 0, 0) {};
    /** 消耗类型 */
    public static final ItemTypeConst ConsumeType = new ItemTypeConst(10, 0, 0) {};

    /** 等级丹 */
    public static final ItemTypeConst LVUP = new ItemTypeConst(10, 1, 0) {};
    /** 回复血量 */
    public static final ItemTypeConst HPADD = new ItemTypeConst(10, 2, 0) {};

    private final int type;
    private final int subType;
    private final int cfgId;

    public ItemTypeConst(int type, int subType, int cfgId) {
        this.type = type;
        this.subType = subType;
        this.cfgId = cfgId;
    }

}