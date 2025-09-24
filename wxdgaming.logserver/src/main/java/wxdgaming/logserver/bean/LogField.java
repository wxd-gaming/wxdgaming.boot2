package wxdgaming.logserver.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 日志自动
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-07 19:50
 **/
@Getter
@Setter
@Accessors(chain = true)
public class LogField extends ObjectBase {

    private String fieldName;
    private String fieldComment;
    private String fieldType;
    private String fieldHtmlStyle;
    private String fieldHtmlTips;

}
