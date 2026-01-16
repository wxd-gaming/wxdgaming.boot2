package wxdgaming.boot2.core.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 字节格式化方案
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2022-11-05 10:34
 **/
public enum BytesUnit {
    B() {
        @Override public long bytes() {
            return 1L;
        }

        /** 传入 B */
        @Override public long toBytes(long uint) {
            return uint;
        }

        /** 传入 B */
        @Override public long toKb(long uint) {
            return uint / 1024;
        }

        /** 传入 B */
        @Override public long toMb(long uint) {
            return uint / 1024 / 1024;
        }

        /** 传入 B */
        @Override public long toGb(long uint) {
            return uint / 1024 / 1024 / 1024;
        }

        /** 传入 B */
        @Override public float toKbf(long uint) {
            return uint * 100 / 1024 / 100f;
        }

        /** 传入 B */
        @Override public float toMbf(long uint) {
            return uint * 100 / 1024 / 1024 / 100f;
        }

        /** 传入 B */
        @Override public float toGbf(long uint) {
            return uint * 100 / 1024 / 1024 / 1024 / 100f;
        }
    },
    KB() {
        @Override public long bytes() {
            return 1024L;
        }

        /** 传入 Kb */
        @Override public long toBytes(long uint) {
            return uint * 1024;
        }

        /** 传入 Kb */
        @Override public long toKb(long uint) {
            return uint;
        }

        /** 传入 Kb */
        @Override public long toMb(long uint) {
            return uint / 1024;
        }

        /** 传入 Kb */
        @Override public long toGb(long uint) {
            return uint / 1024 / 1024;
        }

        /** 传入 Kb */
        @Override public float toKbf(long uint) {
            return uint;
        }

        /** 传入 Kb */
        @Override public float toMbf(long uint) {
            return uint * 100 / 1024 / 100f;
        }

        /** 传入 Kb */
        @Override public float toGbf(long uint) {
            return uint * 100 / 1024 / 1024 / 100f;
        }
    },

    MB() {
        @Override public long bytes() {
            return 1024L * 1024;
        }

        /**传入 Mb */
        @Override public long toBytes(long uint) {
            return uint * bytes();
        }

        /**传入 Mb */
        @Override public long toKb(long uint) {
            return uint * 1024;
        }

        /** 传入 Mb */
        @Override public long toMb(long uint) {
            return uint;
        }

        /**传入 Mb */
        @Override public long toGb(long uint) {
            return uint / 1024;
        }

        /**传入 Mb */
        @Override public float toKbf(long uint) {
            return uint * 1024;
        }

        /**传入 Mb */
        @Override public float toMbf(long uint) {
            return uint;
        }

        /**传入 Mb */
        @Override public float toGbf(long uint) {
            return uint * 100 / 1024 / 100f;
        }
    },

    GB() {
        @Override public long bytes() {
            return 1024L * 1024 * 1024;
        }

        /** 传入 GB */
        @Override public long toBytes(long uint) {
            return uint * bytes();
        }

        /** 传入 GB */
        @Override public long toKb(long uint) {
            return uint * 1024 * 1024;
        }

        /** 传入 GB */
        @Override public long toMb(long uint) {
            return uint * 1024;
        }

        /** 传入 GB */
        @Override public long toGb(long uint) {
            return uint;
        }

        /** 传入 GB */
        @Override public float toKbf(long uint) {
            return uint * 1024 * 1024;
        }

        /** 传入 GB */
        @Override public float toMbf(long uint) {
            return uint * 1024;
        }

        /** 传入 GB */
        @Override public float toGbf(long uint) {
            return uint;
        }
    },
    TB() {
        @Override public long bytes() {
            return 1024L * 1024 * 1024 * 1024;
        }

        /** 传入 TB */
        @Override public long toBytes(long uint) {
            return uint * bytes();
        }

        /** 传入 TB */
        @Override public long toKb(long uint) {
            return uint * 1024 * 1024 * 1024;
        }

        /** 传入 TB */
        @Override public long toMb(long uint) {
            return uint * 1024 * 1024;
        }

        /** 传入 TB */
        @Override public long toGb(long uint) {
            return uint * 1024;
        }

        /** 传入 TB */
        @Override public float toKbf(long uint) {
            return uint * 1024 * 1024 * 1024;
        }

        /** 传入 TB */
        @Override public float toMbf(long uint) {
            return uint * 1024 * 1024;
        }

        /** 传入 TB */
        @Override public float toGbf(long uint) {
            return uint;
        }
    },
    ;

    /**
     * 将配置的缓冲区大小字符串转换为字节数
     *
     * @return 缓冲区大小（字节）
     */
    public static long stringToBytes(String str) {
        if (StringUtils.isBlank(str)) {
            return 0;
        }

        String sizeStr = str.trim().toUpperCase();

        // 提取数字部分和单位部分
        String numberPart = "";
        AtomicReference<String> unitPart = new AtomicReference<>("");

        for (int i = 0; i < sizeStr.length(); i++) {
            char c = sizeStr.charAt(i);
            if (Character.isDigit(c) || c == '.') {
                numberPart += c;
            } else {
                unitPart.set(unitPart.get() + c);
            }
        }

        long number = Double.valueOf(numberPart).longValue();
        BytesUnit bytesUnit = Arrays.stream(BytesUnit.values())
                .filter(unit -> Objects.equals(unit.name(), unitPart.get()))
                .findFirst()
                .orElse(null);
        if (bytesUnit == null)
            throw new IllegalArgumentException("不支持的单位: " + unitPart);
        return bytesUnit.toBytes(number);
    }

    /** 返回常量 */
    public abstract long bytes();

    public abstract long toBytes(long uint);

    public abstract long toKb(long uint);

    public abstract long toMb(long uint);

    public abstract long toGb(long uint);

    public abstract float toKbf(long uint);

    public abstract float toMbf(long uint);

    public abstract float toGbf(long uint);
}
