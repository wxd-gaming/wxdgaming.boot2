package wxdgaming.boot2.core.lang.condition;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 完成条件
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2022-10-10 15:36
 **/
@Getter
@Setter
@Accessors(chain = true)
public class ConditionProgress implements Serializable {

    /** 配置ID */
    private int cfgId;
    /** 当前进度 */
    private long progress;

    @JSONField(serialize = false, deserialize = false)
    public boolean isFinish() {
        return false;
    }
}
