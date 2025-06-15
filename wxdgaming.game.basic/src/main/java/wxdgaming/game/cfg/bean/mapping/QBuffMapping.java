package wxdgaming.game.cfg.bean.mapping;


import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.starter.excel.store.DataKey;
import wxdgaming.boot2.starter.excel.store.DataMapping;

import java.io.Serializable;
import java.util.*;

/**
 * excel 构建 buff, src/main/cfg/buff.xlsx, q_buff,
 *
 * @author: wxd-gaming(無心道, 15388152619)
 **/
@Getter
@Setter
@DataMapping(name = "q_buff", comment = "buff", excelPath = "src/main/cfg/buff.xlsx", sheetName = "q_buff")
public abstract class QBuffMapping extends ObjectBase implements Serializable, DataKey {

    /** 主键id 唯一id */
    protected String id;
    /** buff的id */
    protected int group;
    /** 等级 */
    protected int lv;
    /** 类型 */
    protected int type;
    /** 添加的操作，0是覆盖，1是如果已经有忽略，2叠加层级 */
    protected int addType;
    /** 是否在添加buff立即执行一次 */
    protected boolean addExecutor;
    /** buff的清理机制，死亡删除，切换地图删除，下线删除 */
    protected int clearType;
    /** 添加buff的时候检查是部分保护其他buff */
    protected ArrayList<Integer> checkGroup;
    /** 添加buff 的时候删除的其他的buff */
    protected ArrayList<Integer> clearGroup;
    /** 持续时间 */
    protected int duration;
    /** 间隔时间 */
    protected int interval;
    /** 参数1 */
    protected int paramInt1;
    /** 参数2 */
    protected int paramInt2;
    /** 参数3 */
    protected int paramInt3;
    /** 特殊配置1 */
    protected wxdgaming.boot2.core.lang.ConfigString paramString1;
    /** 特殊配置2 */
    protected wxdgaming.boot2.core.lang.ConfigString paramString2;
    /** 特殊配置3 */
    protected wxdgaming.boot2.core.lang.ConfigString paramString3;

    public Object key() {
        return id;
    }

}
