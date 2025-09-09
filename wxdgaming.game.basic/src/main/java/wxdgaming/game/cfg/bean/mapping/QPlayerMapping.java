package wxdgaming.game.cfg.bean.mapping;


import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.starter.excel.store.DataKey;
import wxdgaming.boot2.starter.excel.store.DataMapping;
import wxdgaming.game.server.bean.attr.AttrInfo;

import java.io.Serializable;

/**
 * excel 构建 怪物表, src/main/cfg/玩家信息.xlsx, q_player,
 *
 * @author wxd-gaming(無心道, 15388152619)
 **/
@Getter
@Setter
@DataMapping(name = "q_player", comment = "怪物表", excelPath = "src/main/cfg/玩家信息.xlsx", sheetName = "q_player")
public abstract class QPlayerMapping extends ObjectBase implements Serializable, DataKey {

    /** 主键id/等级 */
    protected int id;
    /** 升级所需要的经验值 */
    protected int exp;
    /** 属性 */
    protected AttrInfo attr;
    /** 属性 */
    protected AttrInfo attrPro;

    public Object key() {
        return id;
    }

}
