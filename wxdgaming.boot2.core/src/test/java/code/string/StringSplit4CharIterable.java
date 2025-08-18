package code.string;

/**
 * 字符串切割
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-24 19:42
 */
public class StringSplit4CharIterable {

    private final char[] sourceChars;
    /** 用于切割的字符串 */
    private final char[] delimiter;
    private int fromIndex = 0;
    private boolean over = false;

    public StringSplit4CharIterable(String source, String delimiter) {
        this.sourceChars = source.toCharArray();
        this.delimiter = delimiter.toCharArray();
    }

    public boolean hasNext() {
        return !over;
    }

    public String next() {
        int index = indexOf();
        if (index < 0) {
            over = true;
            return new String(sourceChars, fromIndex, sourceChars.length - fromIndex);
        } else {
            String string = new String(sourceChars, fromIndex, index - fromIndex);
            fromIndex = index + delimiter.length;
            return string;
        }
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

}
