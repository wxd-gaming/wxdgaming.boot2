package wxdgaming.boot2.starter.batis;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.starter.batis.ann.DbColumn;

/**
 * 实体类基类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-16 01:25
 **/
@Getter
@Setter
public abstract class Entity extends ObjectBase {

    /** null 未知， true 表示不存在数据库 false 表示在数据库 */
    @DbColumn(ignore = true)
    @JSONField(serialize = false, deserialize = false)
    private transient Boolean newEntity = null;

}
