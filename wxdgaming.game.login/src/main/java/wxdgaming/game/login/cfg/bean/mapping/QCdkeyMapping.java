package wxdgaming.game.login.cfg.bean.mapping;


import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.starter.excel.store.DataKey;
import wxdgaming.boot2.starter.excel.store.DataMapping;

import java.io.Serializable;
import java.util.*;

/**
 * excel 构建 任务集合, src/main/cfg/激活码.xlsx, q_cdkey,
 *
 * @author wxd-gaming(無心道, 15388152619)
 **/
@Getter
@Setter
@DataMapping(name = "q_cdkey", comment = "任务集合", excelPath = "src/main/cfg/激活码.xlsx", sheetName = "q_cdkey")
public abstract class QCdkeyMapping extends ObjectBase implements Serializable, DataKey {

    /** 主键id */
    protected int id;
    /** 通用码 */
    protected String code;
    /** 备注说明 */
    protected String comment;
    /** 条件 */
    protected String validation;
    /** 奖励 */
    protected final List<wxdgaming.game.server.bean.goods.ItemCfg> rewards = new ArrayList<>();

    public Object key() {
        return id;
    }

}
