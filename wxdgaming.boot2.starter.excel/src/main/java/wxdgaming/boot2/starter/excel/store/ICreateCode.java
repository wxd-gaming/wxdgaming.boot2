package wxdgaming.boot2.starter.excel.store;


import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.util.PatternUtil;
import wxdgaming.boot2.core.format.TemplatePack;
import wxdgaming.boot2.core.io.FileUtil;
import wxdgaming.boot2.starter.excel.CellInfo;
import wxdgaming.boot2.starter.excel.TableData;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 * 代码生成器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-14 09:28
 */
@Slf4j
public abstract class ICreateCode {

    abstract TypeString typeString();

    /**
     * 生成字符串
     *
     * @param tableData   表数据
     * @param outPath     输出路径
     * @param packageName 包名
     * @param belong      归属
     * @author wxd-gaming(無心道, 15388152619)
     * @version 2025-04-14 09:26
     */
    public void createCode(TableData tableData, String outPath, String packageName, String belong) {
        if (!outPath.endsWith("/")) {
            outPath += "/";
        }
        JSONObject parse = new JSONObject();
        parse.put("packageName", packageName);
        parse.put("tableName", tableData.getTableName());
        parse.put("tableComment", tableData.getTableComment());
        parse.put("filePath", tableData.getFilePath());
        parse.put("codeClassName", tableData.getCodeClassName());
        if (tableData.getCellInfo4IndexMap().values().stream().anyMatch(ci -> ci.getFieldName().equals("q_id"))) {
            parse.put("keyColumn", "q_id");
        } else if (tableData.getCellInfo4IndexMap().values().stream().anyMatch(ci -> ci.getFieldName().equals("id"))) {
            parse.put("keyColumn", "id");
        } else {
            throw new RuntimeException("未找到主键列 q_id or id");
        }
        ArrayList<Map<String, Object>> columns = new ArrayList<>();
        for (CellInfo cellInfo : tableData.getCellInfo4IndexMap().values()) {
            if (!StringUtils.isBlank(belong) && !cellInfo.getFieldBelong().equals(belong)) continue;
            Map<String, Object> column = new JSONObject();
            column.put("fieldNameLower", PatternUtil.uncapitalize(cellInfo.getFieldName()));
            column.put("columnComment", cellInfo.getFieldComment());
            column.put("fieldTypeString", cellInfo.getFieldTypeString());
            column.put("fieldBelong", cellInfo.getFieldBelong());
            columns.add(column);
        }
        parse.put("columns", columns);

        TemplatePack templatePack = TemplatePack.build(this.getClass().getClassLoader(), "excel-code/" + typeString().getValue());
        String tmpPath = outPath + packageName.replace(".", "/") + "/";

        {
            File file = new File(tmpPath + "bean/mapping/" + tableData.getCodeClassName() + "Mapping.java");
            templatePack.ftl2File("mapping.ftl", parse, file.getPath());
            log.info("生成 映射 文件：{}, {}, {}", tableData.getTableComment(), tableData.getTableName(), FileUtil.getCanonicalPath(file));
        }

        {
            File file = new File(tmpPath + "bean/" + tableData.getCodeClassName() + ".java");
            if (!file.exists()) {
                templatePack.ftl2File("bean.ftl", parse, file.getPath());
                log.info("生成 bean 文件：{}, {}, {}", tableData.getTableComment(), tableData.getTableName(), FileUtil.getCanonicalPath(file));
            }
        }

        {
            File file = new File(tmpPath + "/" + tableData.getCodeClassName() + "Table.java");
            if (!file.exists()) {
                templatePack.ftl2File("table.ftl", parse, file.getPath());
                log.info("生成 table 文件：{}, {}, {}", tableData.getTableComment(), tableData.getTableName(), FileUtil.getCanonicalPath(file));
            }
        }
    }

}
