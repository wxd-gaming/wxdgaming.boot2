package wxdgaming.boot2.starter.batis;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.batis.ann.DbColumn;

/**
 * long
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-15 13:02
 **/
@Getter
@Setter
public class EntityLongUID extends Entity implements EntityUID<Long> {

    @JSONField(ordinal = 1)
    @DbColumn(key = true)
    private Long uid;

    public int intUid() {
        return uid.intValue();
    }

}
