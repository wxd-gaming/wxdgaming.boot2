package code.string;

import java.util.ArrayList;
import java.util.List;

/**
 * 字符串切割
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-24 19:42
 */
public class StringSplit {

    public static List<String> split(String source, String delimiter) {
        List<String> list = new ArrayList<>();
        String result;
        while (source != null) {
            int index = source.indexOf(delimiter);
            if (index < 0) {
                result = source;
                source = null;
            } else {
                result = source.substring(0, index);
                source = source.substring(index + delimiter.length());
            }
            list.add(result);
        }
        return list;
    }

    private String source;
    /** 用于切割的字符串 */
    private final String delimiter;
    private boolean over = false;
    private final List<String> list = new ArrayList<>();

    public StringSplit(String source, String delimiter) {
        this.source = source;
        this.delimiter = delimiter;
    }

    public String first() {
        if (list.isEmpty()) {
            next();
        }
        return list.getFirst();
    }

    public String last() {
        return getList().getLast();
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
        list.add(result);
        return result;
    }

    public List<String> getList() {
        while (hasNext()) {
            next();
        }
        return list;
    }
}
