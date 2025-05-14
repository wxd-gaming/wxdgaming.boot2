package wxdgaming.boot2.core.lang;

import lombok.Getter;
import lombok.Setter;

/**
 * 基类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-13 14:21
 **/
@Getter
@Setter
public abstract class ObjectLong extends ObjectBase {

    private long uid;

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ObjectLong that = (ObjectLong) o;
        return getUid() == that.getUid();
    }

    @Override public int hashCode() {
        return Long.hashCode(getUid());
    }

}
