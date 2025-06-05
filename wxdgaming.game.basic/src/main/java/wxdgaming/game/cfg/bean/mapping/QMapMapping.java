package wxdgaming.game.cfg.bean.mapping;


import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.starter.excel.store.DataKey;
import wxdgaming.boot2.starter.excel.store.DataMapping;

import java.io.Serializable;
import java.util.*;

/**
 * excel 构建 怪物表, src/main/cfg/地图信息.xlsx, q_map,
 *
 * @author: wxd-gaming(無心道, 15388152619)
 **/
@Getter
@Setter
@DataMapping(name = "q_map", comment = "怪物表", excelPath = "src/main/cfg/地图信息.xlsx", sheetName = "q_map")
public abstract class QMapMapping extends ObjectBase implements Serializable, DataKey {

    /** 主键id */
    protected int id;
    /** 1是本服，2是跨服 */
    protected int crossType;
    /** 地图类型1常规，2是副本 */
    protected int type;
    /** 怪物名称 */
    protected String name;
    /** 等级 */
    protected int min_lv;
    /** 等级 */
    protected int max_lv;
    /** buff配置 */
    protected ArrayList<Integer> buffIds;

    public Object key() {
        return id;
    }

}
