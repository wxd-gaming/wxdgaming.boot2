package wxdgaming.game.login.entity;


import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.batis.EntityIntegerUID;
import wxdgaming.boot2.starter.batis.ann.DbColumn;
import wxdgaming.boot2.starter.batis.ann.DbTable;

import java.io.Serial;
import java.io.Serializable;

/**
 * excel 构建 公告gog, src/main/cfg/公告.xlsx, q_notice,
 *
 * @author wxd-gaming(無心道, 15388152619)
 **/
@Getter
@Setter
@DbTable
public class NoticeEntity extends EntityIntegerUID implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    @DbColumn(index = true)
    protected long createTime;
    /** 开始时间 */
    protected long startTime;
    /** 结束时间 */
    protected long endTime;
    /** 标题 */
    protected String title;
    /** 内容 */
    protected String content;

}
