package wxdgaming.boot2.core.format;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 输出格式化
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-11-06 11:30
 **/
public class TableFormatter {

    private List<String[]> rows;
    private int[] columnWidths;
    private boolean[] columnTs;

    public TableFormatter() {
        this.rows = new ArrayList<>();
    }

    public void addRow(Object... columns) {
        String[] row = new String[columns.length];
        for (int i = 0, columnsLength = columns.length; i < columnsLength; i++) {
            Object column = columns[i];
            row[i] = String.valueOf(column);
        }
        rows.add(row);
    }

    public String generateTable() {
        // Calculate the maximum width for each column
        columnWidths = new int[rows.get(0).length];
        columnTs = new boolean[rows.get(0).length];
        for (String[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                String str = row[i];
                int width = calculateWidth(str);
                if (str.length() != width) {
                    width = width + 2;
                    columnTs[i] = true;
                } else {
                    width = width + 1;
                }
                if (width > columnWidths[i]) {
                    columnWidths[i] = width;
                }
            }
        }

        StringBuilder table = new StringBuilder();

        for (String[] row : rows) {
            if (!table.isEmpty()) {
                table.append("\n");
            }
            for (int i = 0; i < row.length; i++) {
                int columnWidth = columnWidths[i];
                String cell = row[i];
                int calculated = calculateWidth(cell);
                int diff = columnWidth - calculated + cell.length();
                cell = StringUtils.rightPad(cell, diff, " ");
                table.append("| ").append(cell);
                if (columnTs[i]) table.append("\t");
            }
        }
        return table.toString();
    }

    private int calculateWidth(String str) {
        int width = 0;
        for (char c : str.toCharArray()) {
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_RADICALS_SUPPLEMENT
                || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.KANGXI_RADICALS
                || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.IDEOGRAPHIC_DESCRIPTION_CHARACTERS) {
                width += 2; // CJK characters are considered to be twice as wide
            } else {
                width += 1; // Other characters are considered to be one unit wide
            }
        }
        return width;
    }

    public static void main(String[] args) {
        TableFormatter formatter = new TableFormatter();
        formatter.addRow("index", "online", "account", "rid", "角色名字", "lv", "(地图)(坐标)", "mainTask");
        formatter.addRow("2", "true", "robotd72", "19123176664756225", "希尔顿蓓尔希尔顿3A", "20", "(1003-太阳)(143,87)", "30010-击杀大魔王-(10/10, 8/10)-已接受");
        formatter.addRow("2", "true", "robotd72", "19123176664756225", "希尔顿尔顿3A", "20", "(1003-太阳)(143,87)", "30010-击杀大魔王-(10/10, 8/10)-已接受");
        formatter.addRow("2", "true", "robotd72", "19123176664756225", "希尔3A", "20", "(1003-太阳)(143,87)", "30010-击杀大魔王-(10/10, 8/10)-已接受");
        System.out.println(formatter.generateTable());
    }
}
