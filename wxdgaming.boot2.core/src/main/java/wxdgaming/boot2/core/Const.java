package wxdgaming.boot2.core;

import java.nio.charset.Charset;

/**
 * 常量
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-17 11:12
 **/
public interface Const {

    int SORT_DEFAULT = 99999;
    String authorization = "authorization";
    /** 使用 gzip 的最小值 */
    int USE_GZIP_MIN_LENGTH = 1024 * 8;

    Charset GB2313 = Charset.forName("GB2312");
    String EMPTY_STRING = "";
    byte[] EMPTY_BYTE_ARRAY = new byte[0];
    int[] EMPTY_INT_ARRAY = new int[0];
    int ZERO = 0;
    /** null 字符串 */
    String nullStr = "null";
    /** null 字符串的字节数组 */
    byte[] nullBytes = "null".getBytes();
    /** 换行符{@code \n} */
    String Line = "\n";
    /** 换行符{@code \n} */
    byte[] LineBytes = "\n".getBytes();
    char[] NUMBER_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '9'};

}
