package wxdgaming.boot2.starter.excel;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import wxdgaming.boot2.core.util.AssertUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 写入excel文件
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-24 09:28
 **/
@Slf4j
public class WriteExcel {

    final Path path;
    final Workbook workbook;
    Sheet sheet;
    List<String> titleList = new ArrayList<>();
    Row titleRow;

    public WriteExcel(Path path) {
        this.path = path;
        String fileName = path.getFileName().toString();
        if (fileName.endsWith(".xls")) {
            workbook = new HSSFWorkbook();
        } else if (fileName.endsWith(".xlsx")) {
            workbook = new XSSFWorkbook();
        } else {
            throw new IllegalArgumentException("无法识别的文件：" + path);
        }
    }

    public void createSheet(String sheetName) {
        sheet = workbook.createSheet(sheetName);
        titleList = new ArrayList<>();
        titleRow = sheet.createRow(0);
    }

    public void addTitle(List<String> titles) {
        for (String title : titles) {
            addTitle(title);
        }
    }

    public void addTitle(String name) {
        titleRow.createCell(titleList.size(), CellType.STRING).setCellValue(name);
        titleList.add(name);
    }

    public void addRow(Map<String, ?> rowData) {
        AssertUtil.isTrue(!titleList.isEmpty(), "未设置标题");
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            Object o = rowData.get(title);
            if (o == null) {
                o = "";
            }
            String value = "";
            if (o instanceof String str) {
                value = str;
            } else if (o instanceof Number) {
                value = o.toString();
            } else {
                value = JSON.toJSONString(o);
            }
            row.createCell(i, CellType.STRING).setCellValue(value);
        }
    }

    public void saveFile() {
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                throw ExceptionUtils.asRuntimeException(e);
            }
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(path.toFile(), false)) {
            workbook.write(fileOutputStream);
        } catch (IOException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

}
