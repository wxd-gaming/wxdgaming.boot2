package wxdgaming.boot2.core.lang.bit;

import lombok.Getter;
import wxdgaming.boot2.core.util.AssertUtil;

import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 状态
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-15 19:12
 **/
@Getter
public class BitFlagGroup {

    private static final ConcurrentSkipListMap<Integer, BitFlagGroup> flagMap = new ConcurrentSkipListMap<>();

    public static BitFlagGroup of(int flag) {
        BitFlagGroup bitFlagGroup = flagMap.get(flag);
        AssertUtil.assertNull(bitFlagGroup, "状态不存在 flag=%d", flag);
        return bitFlagGroup;
    }

    private final int flag;
    private final int index;
    private final int end;

    public BitFlagGroup(int flag) {
        this(flag, flag, 1);
    }

    public BitFlagGroup(int flag, int index, int len) {
        this.index = index;
        this.flag = flag;
        this.end = index + len - 1;
        AssertUtil.assertTrue(index <= flag && flag <= end, "状态范围错问 index <= flag && flag <= end");
        AssertUtil.assertTrue(flagMap.putIfAbsent(flag, this) == null, "重复定义状态");
    }

    @Override public String toString() {
        BitFlag bitFlag = new BitFlag().addFlag(flag);
        BitFlag bitFlagGroup = new BitFlag().addFlagRange(index, end);
        return """
                StatusConst
                index=%d, flag=%d, end=%d,
                %s
                %s"""
                .formatted(index, flag, end, bitFlag.toString(), bitFlagGroup);
    }

    public static void main(String[] args) {
        BitFlagGroup statusConst = new BitFlagGroup(1, 1, 4);
        System.out.println(statusConst);
    }

}
