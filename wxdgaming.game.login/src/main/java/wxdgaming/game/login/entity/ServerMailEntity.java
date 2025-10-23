package wxdgaming.game.login.entity;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.IntRangeCheck;
import wxdgaming.boot2.core.lang.LongRangeCheck;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.batis.EntityIntegerUID;
import wxdgaming.boot2.starter.batis.ann.DbTable;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * 全服邮件
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-20 14:31
 **/
@Getter
@Setter
@DbTable
public class ServerMailEntity extends EntityIntegerUID implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    /** 有效期 */
    private LongRangeCheck timeRange = new LongRangeCheck();
    /** 如果是空表示所有区服，如果不为空表示特定区服 */
    private ArrayList<Integer> serverIdList = new ArrayList<>();
    private ArrayList<Long> roleIdList = new ArrayList<>();
    private ArrayList<String> accountList = new ArrayList<>();
    private IntRangeCheck lvRange = new IntRangeCheck();
    private IntRangeCheck vipLvRange = new IntRangeCheck();
    private String title;
    private String content;
    private String itemListString;
    /** 记录一下谁创建的 */
    private String createAdmin;
    private long createTime;
    /** 审核人员 */
    private String auditAdmin;
    /** 审核时间 */
    private long auditTime;

    public boolean checkServerId(int serverId) {
        return serverIdList.isEmpty() || serverIdList.contains(serverId);
    }

    public boolean validTime() {
        long millis = MyClock.millis();
        return timeRange.inRange(millis);
    }

}
