package wxdgaming.boot2.core.chatset;

import org.checkerframework.checker.units.qual.A;
import wxdgaming.boot2.core.util.RandomUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串拼接 Scale
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-13 13:44
 **/
public class StringUtils {

    public static final Charset GB2313 = Charset.forName("GB2312");
    public static final String EMPTY_STRING = "";
    public static final int ZERO = 0;
    /** null 字符串 */
    public static final String nullStr = "null";
    /** null 字符串的字节数组 */
    public static final byte[] nullBytes = "null".getBytes();
    /** 换行符{@code \n} */
    public static final String Line = "\n";
    /** 换行符{@code \n} */
    public static final byte[] LineBytes = "\n".getBytes();
    /** 包含汉字 */
    public static final Pattern PATTERN_Have_UUU = Pattern.compile(".*[\\u4e00-\\u9fa5]+.*");
    /** 只保留数字 */
    public static final Pattern PATTERN_REPLACE_UUU_2 = Pattern.compile("[^0-9]");
    /** 验证必须是 数字 or 字母 or 下划线 */
    public static final Pattern PATTERN_ABC_0 = Pattern.compile("^[_\\s@a-zA-Z0-9]*$");

    /** 数字。字母，汉字 */
    public static final Pattern PATTERN_ACCOUNT = Pattern.compile("^[A-Za-z0-9\u4e00-\u9fa5]+$");

    public static final char[] NUMBER_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '9'};

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

    public static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    public static boolean isNotBlank(String s) {
        return !isBlank(s);
    }


    /**
     * 将字符串的首字母转大写
     *
     * @param str 需要转换的字符串
     * @return
     */
    public static String upperFirst(String str) {
        // 进行字母的ascii编码前移，效率要高于截取字符串进行转换的操作
        char[] cs = str.toCharArray();
        cs[0] = Character.toUpperCase(cs[0]);
        return String.valueOf(cs);
    }

    /**
     * 将字符串的首字母转大写
     *
     * @param str 需要转换的字符串
     * @return
     */
    public static String lowerFirst(String str) {
        // 进行字母的ascii编码前移，效率要高于截取字符串进行转换的操作
        char[] cs = str.toCharArray();
        cs[0] = Character.toLowerCase(cs[0]);
        return String.valueOf(cs);
    }

    /**
     * String左对齐
     *
     * @param source 需要补齐的字符串
     * @param len    需要补齐的长度
     * @param ch     补齐的字符
     * @return 对齐后的字符串
     * @author 尧
     */
    public static String padLeft(Object source, int len, char ch) {
        String valueOf = String.valueOf(source);
        int oldLength = valueOf.length();
        if (PATTERN_Have_UUU.matcher(valueOf).matches()) {
            /*有汉字，转化汉字*/
            oldLength = new String(valueOf.getBytes(GB2313), StandardCharsets.ISO_8859_1).length() - 1;
        }
        int diff = len - oldLength;
        if (diff <= 0) {
            return valueOf;
        }
        char[] oldChars = valueOf.toCharArray();
        char[] newChars = new char[oldChars.length + diff];
        for (int i = 0; i < diff; i++) {
            newChars[i] = ch;
        }
        System.arraycopy(oldChars, 0, newChars, diff, oldChars.length);
        return new String(newChars);
    }

    /**
     * String右对齐
     *
     * @param source 需要补齐的字符串
     * @param len    需要补齐的长度
     * @param ch     补齐的字符
     * @return 对齐后的字符串
     * @author 尧
     */
    public static String padRight(Object source, int len, char ch) {
        String valueOf = String.valueOf(source);
        int oldLength = valueOf.length();
        if (PATTERN_Have_UUU.matcher(valueOf).matches()) {
            /*有汉字，转化汉字*/
            oldLength = new String(valueOf.getBytes(GB2313), StandardCharsets.ISO_8859_1).length() - 1;
        }
        int diff = len - oldLength;
        if (diff <= 0) {
            return valueOf;
        }
        char[] oldChars = valueOf.toCharArray();
        char[] newChars = new char[oldChars.length + diff];
        System.arraycopy(oldChars, 0, newChars, 0, oldChars.length);
        for (int i = oldChars.length; i < newChars.length; i++) {
            newChars[i] = ch;
        }
        return new String(newChars);
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
     * @return
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
        String unicodeBytes = "";
        for (int i = 0; i < utfBytes.length; i++) {
            String hexB = Integer.toHexString(utfBytes[i]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
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


    public static String randomString(int length) {
        // 产生随机数
        StringBuilder sb = new StringBuilder();
        // 循环length次
        for (int i = 0; i < length; i++) {
            // 产生0-2个随机数，既与a-z，A-Z，0-9三种可能
            int number = ThreadLocalRandom.current().nextInt(3);
            long result = 0;
            switch (number) {
                // 如果number产生的是数字0；
                case 0:
                    // 产生A-Z的ASCII码
                    result = Math.round(Math.random() * 25 + 65);
                    // 将ASCII码转换成字符
                    sb.append((char) result);
                    break;
                case 1:
                    // 产生a-z的ASCII码
                    result = Math.round(Math.random() * 25 + 97);
                    sb.append((char) result);
                    break;
                case 2:
                    // 产生0-9的数字
                    sb.append(new Random().nextInt(10));
                    break;
            }
        }
        return sb.toString();
    }

    /** 根据固定字符 生成一个随机字符串 */
    public static String randomString(char[] chars, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(chars[RandomUtils.random(chars.length)]);
        }
        return sb.toString();
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
