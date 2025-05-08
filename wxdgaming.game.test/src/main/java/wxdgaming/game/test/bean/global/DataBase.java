package wxdgaming.game.test.bean.global;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 全局数据
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-29 13:18
 **/
@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,  // 使用类全名作为类型标识
        include = JsonTypeInfo.As.PROPERTY, // 作为独立字段（默认字段名为 "@class"）
        property = "@class"           // 自定义字段名（可选，默认是 "@class"）
)
public abstract class DataBase extends ObjectBase {


}
