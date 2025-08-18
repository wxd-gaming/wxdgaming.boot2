package code.string;

/**
 * 字符串切割
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-24 19:42
 */
public class StringSplitIterable {

    private String source;
    /** 用于切割的字符串 */
    private final String delimiter;
    private boolean over = false;

    public StringSplitIterable(String source, String delimiter) {
        this.source = source;
        this.delimiter = delimiter;
    }

    public boolean hasNext() {
        return !over;
    }

    public String next() {
        if (source == null) return null;
        String result;
        int index = source.indexOf(delimiter);
        if (index < 0) {
            result = source;
            source = null;
            over = true;
        } else {
            result = source.substring(0, index);
            source = source.substring(index + delimiter.length());
        }
        return result;
    }

}
