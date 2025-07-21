package wxdgaming.boot2.core.lang;

import org.openjdk.jol.info.GraphLayout;
import wxdgaming.boot2.core.format.data.Data2Json;

/**
 * 实现一些序列号接口，重写 hashcode
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2020-09-12 15:39
 **/
public abstract class ObjectBaseLock extends LockBase implements Data2Json {

    public ObjectBaseLock() {
    }

    /** 内存大小,很消耗内存，谨慎使用 */
    public long totalSize() {
        return GraphLayout.parseInstance(this).totalSize();
    }

    @Override
    public String toString() {
        return toJson();
    }
}
