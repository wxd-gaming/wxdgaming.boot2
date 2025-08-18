package code.string;

import java.util.ArrayList;
import java.util.List;

/**
 * 字符串切割
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-24 19:42
 */
public class StringSplit4Char {

    public static List<String> manualSplit(String input, char delimiter) {
        List<String> parts = new ArrayList<>();
        int start = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == delimiter) {
                parts.add(input.substring(start, i));
                start = i + 1;
            }
        }
        parts.add(input.substring(start));
        return parts;
    }

    private final char[] sourceChars;
    /** 用于切割的字符串 */
    private final char[] delimiter;
    private int fromIndex = 0;
    private boolean over = false;
    private final List<String> list = new ArrayList<>();

    public StringSplit4Char(String source, String delimiter) {
        this.sourceChars = source.toCharArray();
        this.delimiter = delimiter.toCharArray();
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
        int index = indexOf();
        if (index < 0) {
            list.add(new String(sourceChars, fromIndex, sourceChars.length - fromIndex));
            over = true;
        } else {
            list.add(new String(sourceChars, fromIndex, index - fromIndex));
            fromIndex = index + delimiter.length;
        }
        return list.getLast();
    }

    private int indexOf() {
        for (int i = fromIndex; i < sourceChars.length; i++) {
            if (sourceChars[i] == delimiter[0]) {
                boolean flag2 = true;
                for (int j = 1; j < delimiter.length; j++) {
                    if (sourceChars[i + j] != delimiter[j]) {
                        flag2 = false;
                        break;
                    }
                }
                if (flag2) {
                    return i;
                }
            }
        }
        return -1;
    }

    public List<String> getList() {
        while (hasNext()) {
            next();
        }
        return list;
    }
}
