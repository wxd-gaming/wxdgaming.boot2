package wxdgaming.boot2.starter.batis.sql.mysql;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.batis.TableMapping;
import wxdgaming.boot2.starter.batis.sql.SqlConfig;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据集
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-15 12:36
 **/
@Slf4j
@Getter
@Setter
public class MysqlDataHelper extends SqlDataHelper {

    public MysqlDataHelper(SqlConfig sqlConfig) {
        super(sqlConfig, new MySqlDDLBuilder());
    }

    @Override public void initDataBatch() {
        this.dataBatch = new MysqlDataBatch(this);
    }

    @Override public MySqlDDLBuilder ddlBuilder() {
        return (MySqlDDLBuilder) super.ddlBuilder();
    }

    @Override public MysqlQueryBuilder queryBuilder() {
        return new MysqlQueryBuilder(this);
    }

    @Override public void checkTable(Map<String, LinkedHashMap<String, JSONObject>> databseTableMap,
                                     TableMapping tableMapping, String tableName, String tableComment,
                                     boolean actionPartition) {
        super.checkTable(databseTableMap, tableMapping, tableName, tableComment, actionPartition);

        /*TODO 处理索引*/

        List<String> indexList = executeScalarList(
                "SELECT INDEX_NAME FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA =? AND TABLE_NAME = ?",
                String.class,
                getDbName(),
                tableName
        );

        LinkedHashMap<String, TableMapping.FieldMapping> columnMap = tableMapping.getColumns();
        for (TableMapping.FieldMapping fieldMapping : columnMap.values()) {
            if (fieldMapping.isIndex()) {
                String keyName = tableName + "_" + fieldMapping.getColumnName();
                /*pgsql 默认全小写*/
                keyName = keyName.toLowerCase();
                if (!indexList.contains(keyName)) {
                    String alterColumn = ddlBuilder().buildAlterColumnIndex(tableName, fieldMapping);
                    executeUpdate(alterColumn);
                }
            }
        }
    }

    @Override protected void createTable(TableMapping tableMapping, String tableName, String comment, boolean actionPartition) {
        super.createTable(tableMapping, tableName, comment, actionPartition);
    }

    /** 获取指定表的所有分区 */
    public List<String> queryPartition(String tableName) {
        return executeScalarList("""
                SELECT
                PARTITION_NAME
                FROM
                INFORMATION_SCHEMA.PARTITIONS
                WHERE
                TABLE_SCHEMA = '%s'
                AND TABLE_NAME = '%s'
                """.formatted(getDbName(), tableName), String.class);
    }

    /** 对指定表添加分区信息 */
    public void addPartition(String tableName, String partitionExpr) {
        List<String> queryPartition = queryPartition(tableName);
        addPartition(queryPartition, tableName, partitionExpr);
    }

    /** 对指定表添加分区信息 */
    public void addPartition(List<String> partitionNames, String tableName, String partitionExpr) {
        String partition_name = tableName + "_" + partitionExpr;
        if (!partitionNames.contains(partition_name)) {
            String string = buildPartition(tableName, partitionExpr);
            executeUpdate(string);
        }
    }

    public String buildPartition(String tableName, String partitionExpr) {
        return "ALTER TABLE %s ADD PARTITION (PARTITION %s_%s VALUES LESS THAN (%s));"
                .formatted(tableName, tableName, partitionExpr, partitionExpr);
    }

}
