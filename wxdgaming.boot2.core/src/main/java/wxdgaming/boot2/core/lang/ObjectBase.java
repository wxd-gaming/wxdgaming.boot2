package wxdgaming.boot2.core.lang;

import wxdgaming.boot2.core.format.data.Data2Json;
import wxdgaming.boot2.core.format.data.Data2Size;
import wxdgaming.boot2.core.json.FastJsonUtil;

import java.io.Serial;
import java.io.Serializable;

/**
 * 基类
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-13 14:21
 **/
public abstract class ObjectBase implements Serializable, Cloneable, Data2Json, Data2Size {

    @Serial private static final long serialVersionUID = 1L;


    @Override public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }


    @Override public String toString() {
        return this.getClass().getSimpleName() + FastJsonUtil.toJSONString(this);
    }
}
