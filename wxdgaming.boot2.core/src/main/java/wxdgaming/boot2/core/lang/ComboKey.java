package wxdgaming.boot2.core.lang;

import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

/**
 * 组合key信息
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-12-10 16:00
 **/
@Getter
@ToString
public final class ComboKey {

    private final Object[] ks;

    public ComboKey(Object... ks) {
        this.ks = ks;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ComboKey) obj;
        return equals(this.ks, that.ks);
    }

    private boolean equals(Object[] o1, Object[] o2) {
        if (o1 == null || o2 == null) return false;
        if (o1.length != o2.length) return false;
        for (int i = 0; i < o1.length; i++) {
            if (!equals(o1[i], o2[i]))
                return false;
        }
        return true;
    }

    private boolean equals(Object o1, Object o2) {
        if (o1 == null || o2 == null) return false;
        if (o1.getClass() != o2.getClass()) return false;
        if (o1.getClass().isArray()) {
            switch (o1) {
                case boolean[] booleans -> {
                    return Arrays.equals(booleans, (boolean[]) o2);
                }
                case byte[] bytes -> {
                    return Arrays.equals(bytes, (byte[]) o2);
                }
                case short[] bytes -> {
                    return Arrays.equals(bytes, (short[]) o2);
                }
                case int[] ints -> {
                    return Arrays.equals(ints, (int[]) o2);
                }
                case long[] longs -> {
                    return Arrays.equals(longs, (long[]) o2);
                }
                case float[] floats -> {
                    return Arrays.equals(floats, (float[]) o2);
                }
                case double[] doubles -> {
                    return Arrays.equals(doubles, (double[]) o2);
                }
                default -> {
                    return equals((Object[]) o1, (Object[]) o2);
                }
            }
        }
        return o1 == o2 || o1.equals(o2);
    }

    @Override public int hashCode() {
        return Arrays.hashCode(getKs());
    }

}
