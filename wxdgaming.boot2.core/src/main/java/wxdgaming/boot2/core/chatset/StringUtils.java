package wxdgaming.boot2.core.chatset;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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

}
