package wxdgaming.boot2.core.util;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串拼接 Scale
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-13 13:44
 **/
public class PatternUtil extends org.springframework.util.StringUtils {

    public static final Set<String> AdminSet = Set.of(
            "ROOT", "ADMIN", "NULL", "UNKNOWN", "USER", "GUEST", "SUPER", "UNDEFINED",
            "通过", "已通过", "拒绝", "已拒绝", "系统", "管理员", "账号", "审核"
    );

    /** 包含汉字 */
    public static final Pattern PATTERN_Have_UUU = Pattern.compile(".*[\\u4e00-\\u9fa5]+.*");
    /** 只保留数字 */
    public static final Pattern PATTERN_REPLACE_UUU_2 = Pattern.compile("[^0-9]");
    /** 验证必须是 数字 or 字母 or 下划线 */
    public static final Pattern PATTERN_ABC_0 = Pattern.compile("^[_\\s@a-zA-Z0-9]*$");

    /** 数字。字母，汉字 */
    public static final Pattern PATTERN_ACCOUNT = Pattern.compile("^[A-Za-z0-9\u4e00-\u9fa5]+$");


    /**
     * 检查是否能通过,正则表达式全匹配规则
     * <p>
     * {@link Pattern#matches}
     *
     * @param str  需要匹配的字符串
     * @param regx 正则表达式
     */
    public static boolean checkMatches(String str, Pattern regx) {
        return regx.matcher(str).matches();
    }

    /** 保留数值字 */
    public static String retainNumbers(String source) {
        return PATTERN_REPLACE_UUU_2.matcher(source).replaceAll("");
    }

    /** 空白字符 null " " */
    public boolean isBlank(String str) {
        return !hasText(str);
    }

    /** 非 空白字符 null " " */
    public boolean isNotBlank(String str) {
        return hasText(str);
    }

    /**
     * 统计字符串的hash计算方法
     */
    public static int hashcode(Object source) {
        return hashcode(source, false);
    }

    /**
     * 计算字符串的hash值
     *
     * @param source 需要计算hash值的字符串
     * @param abs    是否返回绝对值
     */
    public static int hashcode(Object source, boolean abs) {
        double h = 0.0f;
        final String valueOf = String.valueOf(source);
        char[] chars = valueOf.toCharArray();
        for (char aChar : chars) {
            double tmp;
            if (h < 1000) {
                tmp = (h * 0.238f);
            } else {
                tmp = (h * 0.00238f);
            }
            h = h + tmp + (int) aChar;
        }
        h *= 10000;
        int code;
        if (h < Integer.MAX_VALUE) {
            code = (int) h;
        } else {
            code = Double.hashCode(h);
        }
        if (abs) {
            return Math.abs(code);
        }
        return code;
    }

    /**
     * @param source
     * @param abs
     * @param hashFactor hash 算法因子
     * @return
     */
    public static int hashIndex(Object source, boolean abs, int hashFactor) {
        final int hashcode = hashcode(source, abs);
        return hashIndex(hashcode, hashFactor);
    }

    public static int hashIndex(long hashcode, int hashFactor) {
        return (int) (hashcode % (int) (hashFactor * 3.8f) % hashFactor);
    }

    /**
     * @param string
     * @return
     * @Title: unicodeEncode
     * @Description: unicode编码 将中文字符转换成Unicode字符
     */
    public static String unicodeEncode(String string) {
        char[] utfBytes = string.toCharArray();
        StringBuilder unicodeBytes = new StringBuilder();
        for (int i = 0; i < utfBytes.length; i++) {
            String hexB = Integer.toHexString(utfBytes[i]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes.append("\\u").append(hexB);
        }
        return unicodeBytes.toString();
    }

    /**
     * @param string
     * @return 转换之后的内容
     * @Title: unicodeDecode
     * @Description: unicode解码 将Unicode的编码转换为中文
     */
    public static String unicodeDecode(String string) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(string);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            string = string.replace(matcher.group(1), ch + "");
        }
        return string;
    }

    public static int hasLength(String str, char c) {
        int len = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c)
                len++;
        }
        return len;
    }

}
