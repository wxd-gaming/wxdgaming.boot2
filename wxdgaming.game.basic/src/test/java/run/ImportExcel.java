package run;

import org.junit.jupiter.api.Test;
import wxdgaming.boot2.starter.excel.ExcelRepository;
import wxdgaming.boot2.starter.excel.store.CreateJavaCode;

import java.nio.file.Paths;

/**
 * 导出excel
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-09 10:42
 **/
public class ImportExcel {

    @Test
    public void s1() {
        ExcelRepository excelReader = ExcelRepository.getIns();
        excelReader.readExcel(Paths.get("src/main/cfg/"), "");
        excelReader.outJsonFile("../cfg_json").outLuaFile("../cfg_lua");
        excelReader.getTableInfoMap().values().forEach(tableInfo -> {
            // System.out.println(tableInfo.showData());
            CreateJavaCode.getIns().createCode(tableInfo, "src/main/java", "wxdgaming.game.cfg", "");
        });
    }

    @Test
    public void s2() {
        ExcelRepository excelReader = ExcelRepository.getIns();
        excelReader.readExcel(Paths.get("src/main/cfg/"), "");
//        excelReader.outJsonFile("../cfg_json").outLuaFile("../cfg_lua");
        excelReader.getTableInfoMap().values().forEach(tableInfo -> {
             System.out.println(tableInfo.showData());
//            CreateJavaCode.getIns().createCode(tableInfo, "src/main/java", "wxdgaming.game.cfg", "");
        });
    }

}
