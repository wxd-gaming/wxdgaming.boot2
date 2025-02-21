package wxdgaming.boot2.starter.batis;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.batis.ann.DbColumn;

/**
 * Integer
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-15 13:02
 **/
@Getter
@Setter
public class EntityIntegerUID extends Entity {

    @DbColumn(key = true)
    @JSONField(ordinal = 1)
    private int uid = 0;

}
