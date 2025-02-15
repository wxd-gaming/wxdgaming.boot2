package wxdgaming.boot2.starter.batis;

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
public class EntityLongUID implements EntityUID<Long> {

    @DbColumn(key = true)
    private Long uid;


}
