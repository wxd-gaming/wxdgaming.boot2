package wxdgaming.game.login.entity;


import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.batis.EntityIntegerUID;
import wxdgaming.boot2.starter.batis.ann.DbColumn;
import wxdgaming.boot2.starter.batis.ann.DbTable;

import java.io.Serial;
import java.io.Serializable;

/**
 * 礼包码
 *
 * @author wxd-gaming(無心道, 15388152619)
 **/
@Getter
@Setter
@DbTable
public class GiftCodeEntity extends EntityIntegerUID implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    @DbColumn(index = true)
    protected long createTime;
    /** 开始时间 */
    protected long startTime;
    /** 结束时间 */
    protected long endTime;
    /** 通用码 */
    protected String code;
    /** 备注说明 */
    protected String comment;
    /** 条件 DayCount|lt|1;WeekCount|lt|1*/
    protected String validation;
    /** 奖励 */
    protected String rewards = "";

}
