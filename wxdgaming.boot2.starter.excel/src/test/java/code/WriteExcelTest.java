package code;

import org.junit.jupiter.api.Test;
import wxdgaming.boot2.starter.excel.WriteExcel;

import java.nio.file.Path;
import java.util.Map;

public class WriteExcelTest {

    @Test
    public void w1() {

        WriteExcel writeExcel = new WriteExcel(Path.of("./target/out/xx.xlsx"));
        writeExcel.createSheet("sheet1");
        writeExcel.addTitle("a");
        writeExcel.addTitle("b");
        writeExcel.addRow(Map.of("a", "ID", "b", "等级"));
        writeExcel.addRow(Map.of("a", "1", "b", "1"));
        writeExcel.addRow(Map.of("a", "2", "b", "2"));
        writeExcel.saveFile();

    }

}
