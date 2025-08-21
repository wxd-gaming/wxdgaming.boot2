package code;

import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.util.JvmUtil;
import wxdgaming.boot2.starter.excel.ExcelRepository;
import wxdgaming.boot2.starter.excel.store.CreateJavaCode;

import java.nio.file.Paths;
import java.util.Arrays;

/**
 * 读取测试
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-08-09 19:29
 **/
public class ReadTest {

    @Test
    public void t10() {
        ExcelRepository excelReader = ExcelRepository.getIns();
        excelReader.readExcel(Paths.get("src/main/resources/范例.xlsx"), "");
        excelReader.outJsonFile("target/out/json");
        excelReader.getTableInfoMap().values().forEach(tableInfo -> {
            System.out.println(tableInfo.showData());
            System.out.println(tableInfo.getString(2, "shop_item"));
            System.out.println(Arrays.toString(tableInfo.getIntArray(2, "price")));

            String x = tableInfo.data2Json();
            System.out.println(x);
            // List<QItemshopVip> qItemshopVipMappings = FastJsonUtil.parseArray(x, QItemshopVip.class);
            // System.out.println(qItemshopVipMappings);

        });
    }

    //
    // @Test
    // public void t11() {
    //     String json = "{\"value\":\"2:102010001:1,2:102112503:2,2:102011001:2,2:102011005:2\"}";
    //     System.out.println(FastJsonUtil.parse(json, ConfigString.class));
    // }
    //
    // @Test
    // public void t12() {
    //     String json = "{\"id\":2,\"shop_item\":{\"value\":\"2:102010001:1,2:102111011:1,2:102011001:5,2:102011005:5\"},\"name_1\":\"VIP1级礼包\",\"gift_name\":\"VIP1级礼包\",\"show_viplv\":0,\"show_time\":{\"cron\":\"0 0\",\"timeUnit\":\"SECONDS\",\"duration\":500},\"conditions_viplv\":false,\"limit_num\":1.0,\"price\":[1,101011001,500]}";
    //     System.out.println(FastJsonUtil.parse(json, QItemshopVip.class).toJsonFmt());
    // }
    //
    @Test
    public void createExcelCode() {
        System.out.println(JvmUtil.userHome());
        ExcelRepository excelReader = ExcelRepository.getIns();
        excelReader.readExcel(Paths.get("src/main/resources/范例.xlsx"), "");
        excelReader.getTableInfoMap().values().forEach(tableInfo -> {
            CreateJavaCode.getIns().createCode(tableInfo, "src/test/java", "wxdgaming.entity", "");
        });
    }
    //
    // @Test
    // public void loadExcelCode() {
    //     DataRepository dataRepository = new DataRepository()
    //             .setClassLoader(this.getClass().getClassLoader())
    //             .setScanPackageName("wxdgaming.entity")
    //             .setJsonPath("target/out/json");
    //     dataRepository.load();
    //     QItemshopVipTable qItemshopVipTable = dataRepository.dataTable(QItemshopVipTable.class);
    //     System.out.println(qItemshopVipTable.get(1));
    // }

}
