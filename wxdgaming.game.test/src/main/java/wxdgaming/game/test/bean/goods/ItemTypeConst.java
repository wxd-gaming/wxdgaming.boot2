package wxdgaming.game.test.bean.goods;

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
    public static final ItemTypeConst Gold = new ItemTypeConst(1, 0, 1) {};
    /** 绑定钻石 */
    public static final ItemTypeConst BindGold = new ItemTypeConst(1, 0, 2) {};
    /** 金币 */
    public static final ItemTypeConst Money = new ItemTypeConst(1, 0, 3) {};
    /** 绑定金币 */
    public static final ItemTypeConst BindMoney = new ItemTypeConst(1, 0, 4) {};
    /** 经验值 */
    public static final ItemTypeConst EXP = new ItemTypeConst(1, 1, 5) {};
    /** 公会货币 */
    public static final ItemTypeConst GuildCurrencyType = new ItemTypeConst(1, 2, 0) {};

    private final int type;
    private final int subType;
    private final int cfgId;

    public ItemTypeConst(int type, int subType, int cfgId) {
        this.type = type;
        this.subType = subType;
        this.cfgId = cfgId;
    }
}