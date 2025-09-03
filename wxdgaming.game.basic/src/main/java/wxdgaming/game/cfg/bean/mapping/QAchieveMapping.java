package wxdgaming.game.cfg.bean.mapping;


import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.starter.excel.store.DataKey;
import wxdgaming.boot2.starter.excel.store.DataMapping;

import java.io.Serializable;
import java.util.*;

/**
 * excel 构建 成就集合, src/main/cfg/任务成就.xlsx, q_achieve,
 *
 * @author wxd-gaming(無心道, 15388152619)
 **/
@Getter
@Setter
@DataMapping(name = "q_achieve", comment = "成就集合", excelPath = "src/main/cfg/任务成就.xlsx", sheetName = "q_achieve")
public abstract class QAchieveMapping extends ObjectBase implements Serializable, DataKey {

    /** 主键id */
    protected int id;
    /** 成就类型 */
    protected int type;
    /** 成就名称 */
    protected String name;
    /** 成就说明 */
    protected String description;
    /** 等级 */
    protected int min_lv;
    /** 等级 */
    protected int max_lv;
    /** 任务条件 */
    protected wxdgaming.boot2.core.lang.condition.Condition condition;
    /** 任务奖励 */
    protected wxdgaming.boot2.core.lang.ConfigString rewards;

    public Object key() {
        return id;
    }

}
