package wxdgaming.boot2.core.format.string;

import wxdgaming.boot2.core.chatset.StringUtils;

import java.util.function.Function;

/**
 * 转化器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-03 17:15
 */
public class StringParseUtil {

    public static final Function<String, Boolean> parseBoolean = new Function<String, Boolean>() {
        @Override
        public Boolean apply(String s) {
            if (StringUtils.isBlank(s)) {
                return null;
            }
            if ("1".equals(s)) {
                return true;
            }
            if ("0".equals(s)) {
                return false;
            }
            return Boolean.valueOf(s);
        }
    };

    public static final Function<String, Integer> parseInteger = new Function<String, Integer>() {
        @Override
        public Integer apply(String s) {
            if (StringUtils.isBlank(s)) {
                return null;
            }
            return Integer.valueOf(s);
        }
    };

    public static final Function<String, Long> parseLong = new Function<String, Long>() {
        @Override
        public Long apply(String s) {
            if (StringUtils.isBlank(s)) {
                return null;
            }
            return Long.valueOf(s);
        }
    };

    public static final Function<String, Float> parseFloat = new Function<String, Float>() {
        @Override
        public Float apply(String s) {
            if (StringUtils.isBlank(s)) {
                return null;
            }
            return Float.valueOf(s);
        }
    };

    public static final Function<String, Double> parseDouble = new Function<String, Double>() {
        @Override
        public Double apply(String s) {
            if (StringUtils.isBlank(s)) {
                return null;
            }
            return Double.valueOf(s);
        }
    };

}
