package wxdgaming.game.login.cfg.bean.mapping;


import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.starter.excel.store.DataKey;
import wxdgaming.boot2.starter.excel.store.DataMapping;

import java.io.Serializable;
import java.util.*;

/**
 * excel 构建 公告gog, src/main/cfg/公告.xlsx, q_notice,
 *
 * @author wxd-gaming(無心道, 15388152619)
 **/
@Getter
@Setter
@DataMapping(name = "q_notice", comment = "公告gog", excelPath = "src/main/cfg/公告.xlsx", sheetName = "q_notice")
public abstract class QNoticeMapping extends ObjectBase implements Serializable, DataKey {

    /** 主键id */
    protected int id;
    /** 开始时间 */
    protected String startTime;
    /** 结束时间 */
    protected String endTime;
    /** 标题 */
    protected String title;
    /** 内容 */
    protected String content;

    public Object key() {
        return id;
    }

}
