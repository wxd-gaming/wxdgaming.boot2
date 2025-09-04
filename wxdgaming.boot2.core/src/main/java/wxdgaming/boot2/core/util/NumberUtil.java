package wxdgaming.boot2.core.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 数字辅助
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-21 11:35
 **/
public class NumberUtil {

    /** 去掉其它符号保留数字 */
    public static int retainNumber(String source) {
        return parseInt(PatternUtil.retainNumbers(source), 0);
    }


    /** 整数 */
    private static boolean isInteger(String str) {
        if (str.isEmpty()) return false;
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static byte parseByte(Object source, int defaultValue) {
        if (source == null) {
            return (byte) defaultValue;
        }
        try {
            if (source instanceof Number number) {
                return number.byteValue();
            }
            String sourceString = String.valueOf(source);
            if (StringUtils.isBlank(sourceString)) return (byte) defaultValue;
            return Byte.parseByte(sourceString);
        } catch (Exception e) {
            throw new RuntimeException("source = " + source, e);
        }
    }

    public static short parseShort(Object source, int defaultValue) {
        if (source == null) {
            return (short) defaultValue;
        }
        try {
            if (source instanceof Number number) {
                return number.shortValue();
            }
            String sourceString = String.valueOf(source);
            if (StringUtils.isBlank(sourceString)) return (short) defaultValue;
            return Short.parseShort(String.valueOf(source));
        } catch (Exception e) {
            throw new RuntimeException("source = " + source, e);
        }
    }


    public static int parseInt(Object source, int defaultValue) {
        if (source == null) {
            return defaultValue;
        }
        try {
            if (source instanceof Number number) {
                return number.intValue();
            }
            String sourceString = String.valueOf(source);
            if (StringUtils.isBlank(sourceString)) return defaultValue;
            return Integer.parseInt(String.valueOf(source));
        } catch (Exception e) {
            throw new RuntimeException("source = " + source, e);
        }
    }

    public static long parseLong(Object source, long defaultValue) {
        if (source == null) {
            return defaultValue;
        }
        try {
            if (source instanceof Number number) {
                return number.longValue();
            }
            String sourceString = String.valueOf(source);
            if (StringUtils.isBlank(sourceString)) return defaultValue;
            return Long.parseLong(String.valueOf(source));
        } catch (Exception e) {
            throw new RuntimeException("source = " + source, e);
        }
    }

    public static float parseFloat(Object source, float defaultValue) {
        if (source == null) {
            return defaultValue;
        }
        try {
            if (source instanceof Number number) {
                return number.floatValue();
            }
            String sourceString = String.valueOf(source);
            if (StringUtils.isBlank(sourceString)) return defaultValue;
            return Float.parseFloat(String.valueOf(source));
        } catch (Exception e) {
            throw new RuntimeException("source = " + source, e);
        }
    }

    public static double parseDouble(Object source, double defaultValue) {
        if (source == null) {
            return defaultValue;
        }
        try {
            if (source instanceof Number number) {
                return number.doubleValue();
            }
            String sourceString = String.valueOf(source);
            if (StringUtils.isBlank(sourceString)) return defaultValue;
            return Double.parseDouble(String.valueOf(source));
        } catch (Exception e) {
            throw new RuntimeException("source = " + source, e);
        }
    }

}
