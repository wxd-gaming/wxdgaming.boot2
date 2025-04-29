package wxdgaming.game.test.bean.entity;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.batis.Entity;
import wxdgaming.boot2.starter.batis.ann.DbColumn;
import wxdgaming.boot2.starter.batis.ann.DbTable;

/**
 * cdkey使用记录
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-29 15:24
 **/
@Getter
@Setter
@DbTable
public class UseCDKeyRecord extends Entity {

    /** cdkey */
    @DbColumn(key = true, length = 32)
    private String cdkey;
    private long sid;
    /** 使用次数 */
    private int useCount;
    /** 使用时间 */
    private long lastUseTime;
}
