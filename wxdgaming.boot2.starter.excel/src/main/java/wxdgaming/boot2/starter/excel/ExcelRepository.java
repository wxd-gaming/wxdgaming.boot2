package wxdgaming.boot2.starter.excel;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import wxdgaming.boot2.core.Throw;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.util.PatternUtil;
import wxdgaming.boot2.core.format.string.*;
import wxdgaming.boot2.core.io.FileUtil;
import wxdgaming.boot2.core.io.FileWriteUtil;
import wxdgaming.boot2.core.lang.ConfigString;
import wxdgaming.boot2.core.util.ConvertUtil;
import wxdgaming.boot2.starter.excel.store.ICreateCode;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * excel 仓储
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2021-10-14 11:20
 **/
@Slf4j
@Getter
public class ExcelRepository {

    private static final class Lazy {
        private static final ExcelRepository ins = new ExcelRepository();
    }

    public static ExcelRepository getIns() {
        return Lazy.ins;
    }

    private final String[] String_Split = {"[,，]", "[:：]"};

    private final Map<String, TableData> tableInfoMap = new ConcurrentHashMap<>();

    private ExcelRepository() {
    }

    public Optional<TableData> tableData(String tableName) {
        return Optional.ofNullable(tableInfoMap.get(tableName));
    }

    public ExcelRepository outJsonFile(String outPath) {
        tableInfoMap.values().forEach(tableData -> {
            FileWriteUtil.writeString(outPath + "/" + tableData.getTableName() + ".json", tableData.data2Json());
        });
        return this;
    }

    public ExcelRepository outLuaFile(String outPath) {
        tableInfoMap.values().forEach(tableData -> {
            FileWriteUtil.writeString(outPath + "/" + tableData.getTableName() + ".lua", tableData.data2Lua());
        });
        return this;
    }

    public ExcelRepository createCode(ICreateCode iCreateCode, String outPath, String packageName, String belong) {
        tableInfoMap.values().forEach(tableData -> {
            iCreateCode.createCode(tableData, outPath, packageName, belong);
        });
        return this;
    }

    public ExcelRepository createCode(ICreateCode iCreateCode, TableData tableData, String outPath, String packageName, String belong) {
        iCreateCode.createCode(tableData, outPath, packageName, belong);
        return this;
    }

    public final ExcelRepository readExcel(Path path, String belong) {
        FileUtil.walkFiles(path)
                .forEach(filePath -> readExcel0(filePath, belong));
        return this;
    }

    public final ExcelRepository readExcel0(Path path, String belong) {
        if (path == null) {
            log.info("Excel文件不能解析：{}", path);
            return this;
        }
        String pathString = path.toString();
        if (StringUtils.isBlank(pathString) || pathString.contains("@") || pathString.contains("$")) {
            log.info("Excel文件不能解析：{}", pathString);
            return this;
        }
        try {
            String fileName = path.getFileName().toString().toLowerCase();
            Workbook workbook;
            InputStream is = Files.newInputStream(path);
            if (fileName.endsWith(".xls")) {
                workbook = new HSSFWorkbook(is);
            } else if (fileName.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(is);
            } else {
                log.info("无法识别的文件：{}", path);
                return this;
            }
            if (workbook.getNumberOfSheets() < 1) {
                log.info("文件空的：{}", path);
                return this;
            }
            /*多少页签*/
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName().trim().toLowerCase();

                if (StringUtils.isBlank(sheetName)
                    || sheetName.startsWith("sheet")
                    || sheetName.contains("@")
                    || sheetName.contains("$")
                    || !sheetName.startsWith("q_")) {
                    log.debug("Excel文件不能解析：{}, sheetName={} - 需要是 q_ 开头 sheet name 才能解析", path, sheetName);
                    continue;
                }

                Cell tableCommentCall = sheet.getRow(0).getCell(0);
                String tableComment = readCellString(tableCommentCall, false);

                TableData tableData = new TableData(
                        path.toString().replace("\\", "/"),
                        path.getFileName().toString(),
                        sheet.getSheetName(),
                        sheetName,
                        tableComment
                );

                Row fieldBelongRow = sheet.getRow(1);/*归属*/
                Row fieldNameRow = sheet.getRow(2);/*字段名字*/
                Row fieldTypeRow = sheet.getRow(3);/*字段类型*/
                Row fieldCommentRow = sheet.getRow(4);/*字段含义*/

                TreeMap<Integer, CellInfo> cellInfoMap = new TreeMap<>();

                short lastCellNum = fieldNameRow.getLastCellNum();
                for (int cellIndex = 0; cellIndex <= lastCellNum; cellIndex++) {
                    String fieldBelongCell = readCellString(fieldBelongRow.getCell(cellIndex), false);/*字段归属*/
                    String fieldNameCell = readCellString(fieldNameRow.getCell(cellIndex), true);/*字段名字*/
                    String fieldTypeCell = readCellString(fieldTypeRow.getCell(cellIndex), false);/*字段类型*/
                    String fieldCommentCell = readCellString(fieldCommentRow.getCell(cellIndex), false);/*字段含义*/
                    if (!StringUtils.isBlank(belong) && !Objects.equals(fieldBelongCell, belong)) continue;/*排除的归属*/
                    CellInfo cellInfo = new CellInfo()
                            .setCellIndex(cellIndex)
                            .setFieldBelong(fieldBelongCell)
                            .setFieldName(fieldNameCell)
                            .setCellType(fieldTypeCell)
                            .setFieldComment(fieldCommentCell);

                    buildFieldType(fileName, sheetName, cellInfo, fieldTypeCell);

                    if (StringUtils.isBlank(fieldBelongCell)
                        && StringUtils.isBlank(fieldNameCell)
                        && StringUtils.isBlank(fieldTypeCell)) {
                        break;
                    }

                    if (StringUtils.isBlank(fieldNameCell))
                        continue;

                    if (cellInfoMap.put(cellIndex, cellInfo) != null) {
                        throw new RuntimeException("Excel文件不能解析：" + path + ", sheetName=" + sheetName + " 存在重复的字段：" + cellInfo.getFieldName());
                    }
                }

                tableData.cellInfo4IndexMap = cellInfoMap;

                final Map<Object, RowData> rows = new LinkedHashMap<>();
                int lastRowNum = sheet.getLastRowNum();
                for (int rowIndex = 5; rowIndex <= lastRowNum; rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    RowData rowData = new RowData(true);
                    for (int cellIndex = 0; cellIndex < lastCellNum; cellIndex++) {
                        CellInfo cellInfo = tableData.getCellInfo4IndexMap().get(cellIndex);
                        if (cellInfo == null) continue;
                        Cell cell = row.getCell(cellIndex);
                        Object object = readCellValue(tableData, cellIndex, cellInfo, cell);
                        rowData.put(cellInfo.getFieldName(), object);
                    }
                    if (rowData.values().stream().allMatch(v -> v == null || (v instanceof String && StringUtils.isBlank(String.valueOf(v))))) {
                        continue;
                    }
                    Object row_id = rowData.getOrDefault("q_id", rowData.get("id"));
                    if (row_id == null) {
                        throw new RuntimeException("Excel文件不能解析：" + path + ", sheetName=" + sheetName + " 字段内容异常：" + rowIndex);
                    }
                    RowData oldData = rows.put(row_id, rowData);
                    if (oldData != null) {
                        throw new RuntimeException("Excel文件不能解析：" + path + ", sheetName=" + sheetName + " 行: " + rowIndex + " id重复: " + row_id);
                    }
                }

                tableData.rows = Collections.unmodifiableMap(rows);

                tableInfoMap.put(tableData.getTableName(), tableData);
            }
            return this;
        } catch (Throwable throwable) {
            throw Throw.of(path.toString(), throwable);
        }
    }

    /**
     * 读取单元格
     *
     * @param tableData   表格信息
     * @param rowNumber   所在行
     * @param entityField 单元格
     * @param cellData    单元格数据
     */
    private Object readCellValue(TableData tableData, int rowNumber, CellInfo entityField, Cell cellData) {
        /*空白的话，根据传入的类型返回默认值*/
        String trim = "";
        try {
            if (cellData != null) {

                /*以下是判断数据的类型*/
                switch (cellData.getCellType()) {
                    case NUMERIC: /*数字*/
                        DecimalFormat df = new DecimalFormat("0");
                        trim = df.format(cellData.getNumericCellValue());
                        break;
                    case STRING: /*字符串*/
                        trim = cellData.getStringCellValue();
                        break;
                    case BOOLEAN: /*Boolean*/
                        trim = String.valueOf(cellData.getBooleanCellValue());
                        break;
                    case FORMULA: /*公式*/ {
                        /*得到对应单元格的字符串*/
                        try {
                            trim = String.valueOf(cellData.getNumericCellValue());
                        } catch (IllegalStateException e) {
                            trim = String.valueOf(cellData.getRichStringCellValue());
                        }
                    }
                    break;
                    case BLANK: /*空值*/
                        trim = "";
                        break;
                    case ERROR: /*故障*/
                        trim = "非法字符";
                        break;
                    default:
                        trim = "未知类型";
                        break;
                }

                switch (entityField.getFieldTypeString().toLowerCase()) {
                    case "byte[]": {
                        return String2ByteArray.parse(trim);
                    }
                    case "byte[][]": {
                        return String2ByteArray2.parse(trim);
                    }
                    case "int[]": {
                        return String2IntArray.parse.apply(trim);
                    }
                    case "int[][]": {
                        return String2IntArray2.parse.apply(trim);
                    }
                    case "long[]": {
                        return String2LongArray.parse.apply(trim);
                    }
                    case "long[][]": {
                        return String2LongArray2.parse.apply(trim);
                    }
                    case "float[]": {
                        return String2FloatArray.parse.apply(trim);
                    }
                    case "float[][]": {
                        return String2FloatArray2.parse.apply(trim);
                    }
                    case "string[]": {
                        return String2StringArray.parse.apply(trim);
                    }
                    case "string[][]": {
                        return String2StringArray2.parse.apply(trim);
                    }
                    case "list<bool>":
                    case "list<boolean>":
                    case "arraylist<boolean>": {
                        return String2BoolList.parse(trim);
                    }
                    case "list<byte>":
                    case "arraylist<byte>": {
                        return String2ByteList.parse(trim);
                    }
                    case "list<int>":
                    case "list<integer>":
                    case "arraylist<int>":
                    case "arraylist<integer>": {
                        return String2IntList.parse.apply(trim);
                    }
                    case "list<int[]>":
                    case "list<integer[]>":
                    case "arraylist<int[]>":
                    case "arraylist<integer[]>": {
                        return String2IntArrayList.parse.apply(trim);
                    }
                    case "list<long>":
                    case "arraylist<long>": {
                        return String2LongList.parse.apply(trim);
                    }
                    case "list<long[]>":
                    case "arraylist<long[]>": {
                        return String2LongArrayList.parse.apply(trim);
                    }
                    case "list<string>":
                    case "arraylist<string>": {
                        return String2StringList.parse.apply(trim);
                    }
                    case "list<string[]>":
                    case "arraylist<string[]>": {
                        return String2StringArrayList.parse.apply(trim);
                    }
                    case "set<byte>": {
                        return new LinkedHashSet<>(String2ByteList.parse(trim));
                    }
                    case "set<int>": {
                        return new LinkedHashSet<>(String2IntList.parse.apply(trim));
                    }
                    case "set<long>": {
                        return new LinkedHashSet<>(String2LongList.parse.apply(trim));
                    }
                    case "set<string>": {
                        return new LinkedHashSet<>(String2StringList.parse.apply(trim));
                    }
                    default: {
                        try {
                            return ConvertUtil.changeType(trim, entityField.getFieldType());
                        } catch (Exception e) {
                            if (ConfigString.class.equals(entityField.getFieldType()))
                                return new ConfigString(trim);
                            else
                                return ConvertUtil.changeType(trim, entityField.getFieldType());
                        }
                    }
                }
            }
            if (ConfigString.class.equals(entityField.getFieldType()))
                return new ConfigString(trim);
            return ConvertUtil.defaultValue(entityField.getFieldType());
        } catch (Exception ex) {
            final RuntimeException runtimeException = new RuntimeException(
                    ex.getMessage()
                    + "\n文件：" + tableData.getTableComment()
                    + ";\nsheet：" + tableData.getTableName()
                    + ";\n列：" + entityField.getFieldName()
                    + ";\n行：" + rowNumber
                    + ";\n数据类型：" + entityField.getFieldTypeString()
                    + ";\n数据：" + trim + "----无法转换");
            runtimeException.setStackTrace(ex.getStackTrace());
            throw runtimeException;
        }
    }

    private boolean notNullOrEmpty(String source) {
        if (StringUtils.isNotBlank(source)) {
            return !"#null".equalsIgnoreCase(source);
        }
        return false;
    }

    /**
     * 获取一列的字符
     *
     * @param data 单元格
     */
    private String readCellString(Cell data, boolean isColumnName) {
        String trim = "";
        if (data != null) {
            /*空白的话，根据传入的类型返回默认值*/
            /*默认类型*/
            if (data.getCellType() == CellType.STRING
                || (data.getCellType() == CellType.FORMULA && data.getCachedFormulaResultType() == CellType.STRING)) {
                /*字符类型*/
                trim = data.getStringCellValue().trim();
            }
            if (StringUtils.isBlank(trim)) {
                trim = data.toString().trim();
            }
        }
        if (StringUtils.isNotBlank(trim)) {
            trim = trim
                    .replace("class", "clazz")
                    .replace("-", "_");
            if (isColumnName) {
                trim = PatternUtil.lowerFirst(trim);
            }
        }
        return trim.trim();
    }

    private void buildFieldType(String fileName, String sheetName, CellInfo entityField, String fieldTypeName) throws Exception {
        if (StringUtils.isBlank(fieldTypeName)) return;
        final String typeString = typeString(fieldTypeName);
        switch (typeString.toLowerCase()) {
            case "bool":
            case "boolean":
                entityField.setFieldType(boolean.class);
                entityField.setFieldTypeString("boolean");
                break;
            case "java.lang.boolean":
                entityField.setFieldType(Boolean.class);
                entityField.setFieldTypeString("Boolean");
                break;
            case "byte":
                entityField.setFieldType(byte.class);
                entityField.setFieldTypeString("byte");
                break;
            case "java.lang.byte":
                entityField.setFieldType(Byte.class);
                entityField.setFieldTypeString("Byte");
                break;
            case "short":
                entityField.setFieldType(short.class);
                entityField.setFieldTypeString("short");
                break;
            case "java.lang.short":
                entityField.setFieldType(Short.class);
                entityField.setFieldTypeString("Short");
                break;
            case "int":
                entityField.setFieldType(int.class);
                entityField.setFieldTypeString("int");
                break;
            case "java.lang.integer":
                entityField.setFieldType(Integer.class);
                entityField.setFieldTypeString("Integer");
                break;
            case "long":
                entityField.setFieldType(long.class);
                entityField.setFieldTypeString("long");
                break;
            case "java.lang.long":
                entityField.setFieldType(Long.class);
                entityField.setFieldTypeString("Long");
                break;
            case "float":
                entityField.setFieldType(float.class);
                entityField.setFieldTypeString("float");
                break;
            case "java.lang.float":
                entityField.setFieldType(Float.class);
                entityField.setFieldTypeString("Float");
                break;
            case "double":
                entityField.setFieldType(double.class);
                entityField.setFieldTypeString("double");
                break;
            case "java.lang.double":
                entityField.setFieldType(Double.class);
                entityField.setFieldTypeString("Double");
                break;
            case "java.util.date":
                entityField.setFieldType(Date.class);
                entityField.setFieldTypeString("Date");
                break;
            case "java.math.biginteger":
                entityField.setFieldType(BigInteger.class);
                entityField.setFieldTypeString("BigInteger");
                break;
            case "java.math.bigdecimal":
                entityField.setFieldType(BigDecimal.class);
                entityField.setFieldTypeString("BigDecimal");
                break;
            case "string[]":
            case "java.lang.string[]":
                /*String[]*/
                entityField.setFieldType(String[].class);
                entityField.setFieldTypeString("String[]");
                break;
            case "list<string>":
                /*String[]*/
                entityField.setFieldType(ArrayList.class);
                entityField.setFieldTypeString("ArrayList<String>");
                break;
            case "string[][]":
            case "java.lang.string[][]":
                /*String[][]*/
                entityField.setFieldType(String[][].class);
                entityField.setFieldTypeString("String[][]");
                break;
            case "bool[]":
            case "boolean[]":
                entityField.setFieldType(boolean[].class);
                entityField.setFieldTypeString("boolean[]");
                break;
            case "java.lang.boolean[]":
                entityField.setFieldType(Boolean[].class);
                entityField.setFieldTypeString("Boolean[]");
                break;
            case "list<bool>":
            case "list<boolean>":
                entityField.setFieldType(ArrayList.class);
                entityField.setFieldTypeString("ArrayList<Boolean>");
                break;
            case "bool[][]":
            case "boolean[][]":
                entityField.setFieldType(boolean[][].class);
                entityField.setFieldTypeString("boolean[][]");
                break;
            case "java.lang.boolean[][]":
                entityField.setFieldType(Boolean[][].class);
                entityField.setFieldTypeString("Boolean[][]");
                break;
            case "byte[]":
                entityField.setFieldType(byte[].class);
                entityField.setFieldTypeString("byte[]");
                break;
            case "list<byte>":
                entityField.setFieldType(ArrayList.class);
                entityField.setFieldTypeString("ArrayList<Byte>");
                break;
            case "byte[][]":
                entityField.setFieldType(byte[][].class);
                entityField.setFieldTypeString("byte[][]");
                break;
            case "java.lang.byte[]":
                entityField.setFieldType(Byte[].class);
                entityField.setFieldTypeString("Byte[]");
                break;
            case "java.lang.byte[][]":
                entityField.setFieldType(Byte[][].class);
                entityField.setFieldTypeString("Byte[][]");
                break;
            case "int[]":
                entityField.setFieldType(int[].class);
                entityField.setFieldTypeString("int[]");
                break;
            case "java.lang.integer[]":
                entityField.setFieldType(Integer[].class);
                entityField.setFieldTypeString("Integer[]");
                break;
            case "list":
            case "list<int>":
            case "list<integer>":
            case "arraylist<int>":
            case "arraylist<integer>":
                entityField.setFieldType(ArrayList.class);
                entityField.setFieldTypeString("ArrayList<Integer>");
                break;
            case "int[][]":
                entityField.setFieldType(int[][].class);
                entityField.setFieldTypeString("int[][]");
                break;
            case "java.lang.integer[][]":
                entityField.setFieldType(Integer[][].class);
                entityField.setFieldTypeString("Integer[][]");
                break;
            case "list<int[][]>":
            case "arraylist<int[][]>":
                entityField.setFieldType(ArrayList.class);
                entityField.setFieldTypeString("ArrayList<int[][]>");
                break;
            case "list<java.lang.integer[][]>":
            case "arraylist<java.lang.integer[][]>":
                entityField.setFieldType(ArrayList.class);
                entityField.setFieldTypeString("ArrayList<Integer[][]>");
                break;
            case "long[]":
                entityField.setFieldType(long[].class);
                entityField.setFieldTypeString("long[]");
                break;
            case "java.lang.long[]":
                entityField.setFieldType(Long[].class);
                entityField.setFieldTypeString("Long[]");
                break;
            case "list<long>":
            case "list<java.lang.long>":
            case "arraylist<long>":
            case "arraylist<java.lang.long>":
                entityField.setFieldType(ArrayList.class);
                entityField.setFieldTypeString("ArrayList<Long>");
                break;
            case "[[j":
            case "long[][]":
                entityField.setFieldType(long[][].class);
                entityField.setFieldTypeString("long[][]");
                break;
            case "java.lang.long[][]":
                entityField.setFieldType(Long[][].class);
                entityField.setFieldTypeString("Long[][]");
                break;
            case "list<long[]>":
            case "arraylist<long[]>":
                entityField.setFieldType(ArrayList.class);
                entityField.setFieldTypeString("ArrayList<long[]>");
                break;
            case "list<java.lang.long[]>":
            case "arraylist<java.lang.long[]>":
                entityField.setFieldType(ArrayList.class);
                entityField.setFieldTypeString("ArrayList<Long[][]>");
                break;
            case "float[]":
                entityField.setFieldType(float[].class);
                entityField.setFieldTypeString("float[]");
                break;
            case "java.lang.float[]":
                entityField.setFieldType(Float[].class);
                entityField.setFieldTypeString("Float[]");
                break;
            case "list<float>":
            case "list<java.lang.float>":
            case "arraylist<float>":
            case "arraylist<java.lang.float>":
                entityField.setFieldType(ArrayList.class);
                entityField.setFieldTypeString("ArrayList<Float>");
                break;
            case "float[][]":
                entityField.setFieldType(float[][].class);
                entityField.setFieldTypeString("float[][]");
                break;
            case "java.lang.float[][]":
                entityField.setFieldType(Float[][].class);
                entityField.setFieldTypeString("Float[][]");
                break;
            case "list<float[]>":
            case "arraylist<float[]>":
                entityField.setFieldType(ArrayList.class);
                entityField.setFieldTypeString("ArrayList<float[]>");
                break;
            case "list<java.lang.float[]>":
            case "arraylist<java.lang.float[]>":
                entityField.setFieldType(ArrayList.class);
                entityField.setFieldTypeString("ArrayList<Float[]>");
                break;
            case "string":
            case "java.lang.string":
                entityField.setFieldType(String.class);
                entityField.setFieldTypeString("String");
                break;
            default:
                entityField.setFieldType(String.class);
                try {
                    Class<?> aClass = this.getClass().getClassLoader().loadClass(fieldTypeName);
                    entityField.setFieldType(aClass);
                } catch (ClassNotFoundException e) {
                    if (fieldTypeName.toLowerCase().contains("list<")) {
                        entityField.setFieldType(List.class);
                    } else if (fieldTypeName.toLowerCase().contains("map<")) {
                        entityField.setFieldType(Map.class);
                    } else {
                        log.error("{} - {} - {} - {}", fileName, sheetName, entityField.getFieldName(), fieldTypeName, e);
                    }
                }
                entityField.setFieldTypeString(fieldTypeName);
                break;
        }
    }

    /**
     * 把数组类型还原成代码 字符串
     *
     * @param source
     * @return
     */
    private static String typeString(String source) {
        if (source.startsWith("[[L")) {
            source = source.substring(2, source.length() - 1) + "[][]";
        } else if (source.startsWith("[L")) {
            source = source.substring(2, source.length() - 1) + "[]";
        } else if (source.startsWith("[[Z")) {
            source = "boolean[][]";
        } else if (source.startsWith("[Z")) {
            source = "boolean[]";
        } else if (source.startsWith("[[B")) {
            source = "byte[][]";
        } else if (source.startsWith("[B")) {
            source = "byte[]";
        } else if (source.startsWith("[[C")) {
            source = "char[][]";
        } else if (source.startsWith("[C")) {
            source = "char[]";
        } else if (source.startsWith("[[S")) {
            source = "short[][]";
        } else if (source.startsWith("[S")) {
            source = "short[]";
        } else if (source.startsWith("[[I")) {
            source = "int[][]";
        } else if (source.startsWith("[I")) {
            source = "int[]";
        } else if (source.startsWith("[[J")) {
            source = "long[][]";
        } else if (source.startsWith("[J")) {
            source = "long[]";
        } else if (source.startsWith("[[F")) {
            source = "float[][]";
        } else if (source.startsWith("[F")) {
            source = "float[]";
        } else if (source.startsWith("[[D")) {
            source = "double[][]";
        } else if (source.startsWith("[D")) {
            source = "double[]";
        }
        source = source.replace("$", ".");
        return source;
    }

}
