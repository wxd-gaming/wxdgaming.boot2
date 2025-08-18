package wxdgaming.game.cfg.bean.mapping;


import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.starter.excel.store.DataKey;
import wxdgaming.boot2.starter.excel.store.DataMapping;

import java.io.Serializable;
import java.util.*;

/**
 * excel 构建 怪物表, src/main/cfg/怪物信息.xlsx, q_monster,
 *
 * @author wxd-gaming(無心道, 15388152619)
 **/
@Getter
@Setter
@DataMapping(name = "q_monster", comment = "怪物表", excelPath = "src/main/cfg/怪物信息.xlsx", sheetName = "q_monster")
public abstract class QMonsterMapping extends ObjectBase implements Serializable, DataKey {

    /** 主键id */
    protected int id;
    /** 怪物名称 */
    protected String name;
    /** 关联的地图 */
    protected int mapId;
    /** 等级 */
    protected int lv;
    /** 获得经验 */
    protected long exp;
    /** 属性 */
    protected wxdgaming.game.bean.attr.AttrInfo attr;
    /** 属性 */
    protected wxdgaming.game.bean.attr.AttrInfo attrPro;
    /** 击杀奖励 */
    protected final List<wxdgaming.game.bean.goods.ItemCfg> rewards = new ArrayList<>();

    public Object key() {
        return id;
    }

}
