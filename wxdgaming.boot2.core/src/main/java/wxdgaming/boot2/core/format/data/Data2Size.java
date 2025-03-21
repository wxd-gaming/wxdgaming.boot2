package wxdgaming.boot2.core.format.data;

import org.openjdk.jol.info.GraphLayout;
import wxdgaming.boot2.core.format.ByteFormat;

/**
 * 数据大小
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2023-08-29 15:39
 **/
public interface Data2Size {

    /** 内存大小 注意特别耗时，并且可能死循环 */
    default String totalSizes() {
        return totalSizes0(this);
    }

    /** 内存大小 注意特别耗时，并且可能死循环 */
    default long totalSize() {
        return totalSize0(this);
    }

    /** 计算内存大小 注意特别耗时，并且可能死循环 */
    public static String totalSizes0(Object obj) {
        return ByteFormat.format(totalSize0(obj));
    }

    /** 计算内存大小 注意特别耗时，并且可能死循环 */
    public static long totalSize0(Object obj) {
        GraphLayout graphLayout = GraphLayout.parseInstance(obj);
        return graphLayout.totalSize();
    }
}
