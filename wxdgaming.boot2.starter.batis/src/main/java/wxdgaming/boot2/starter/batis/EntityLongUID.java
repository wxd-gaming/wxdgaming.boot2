package wxdgaming.boot2.starter.batis;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.starter.batis.ann.DbColumn;

/**
 * long
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-15 13:02
 **/
@Getter
@Setter
@Accessors(chain = true)
public class EntityLongUID extends Entity {

    @DbColumn(key = true)
    @JSONField(ordinal = -9999)
    private long uid = 0;

    public int intUid() {
        return (int) uid;
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        EntityLongUID that = (EntityLongUID) o;
        return getUid() == that.getUid();
    }

    @Override public int hashCode() {
        return Long.hashCode(getUid());
    }
}
