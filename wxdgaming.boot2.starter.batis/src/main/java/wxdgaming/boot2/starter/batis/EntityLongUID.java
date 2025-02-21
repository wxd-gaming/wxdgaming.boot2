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
public class EntityLongUID extends Entity {

    @DbColumn(key = true)
    @JSONField(ordinal = 1)
    private long uid = 0;

    public int intUid() {
        return (int) uid;
    }

}
