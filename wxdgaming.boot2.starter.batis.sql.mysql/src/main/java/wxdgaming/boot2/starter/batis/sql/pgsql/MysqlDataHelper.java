package wxdgaming.boot2.starter.batis.sql.pgsql;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.batis.TableMapping;
import wxdgaming.boot2.starter.batis.sql.SqlConfig;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 数据集
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-15 12:36
 **/
@Slf4j
@Getter
@Setter
public class MysqlDataHelper extends SqlDataHelper<MySqlDDLBuilder> {

    public MysqlDataHelper(SqlConfig sqlConfig) {
        super(sqlConfig, new MySqlDDLBuilder());
    }

    @Override public void initBatch() {
        this.sqlDataBatch = new MysqlDataBatch(this);
    }

    @Override public void checkTable(Map<String, LinkedHashMap<String, JSONObject>> databseTableMap, TableMapping tableMapping, String tableName, String tableComment) {
        super.checkTable(databseTableMap, tableMapping, tableName, tableComment);

        /*TODO 处理索引*/
        LinkedHashMap<String, TableMapping.FieldMapping> columnMap = tableMapping.getColumns();
        for (TableMapping.FieldMapping fieldMapping : columnMap.values()) {
            if (fieldMapping.isIndex()) {
                String keyName = tableName + "_" + fieldMapping.getColumnName();
                /*pgsql 默认全小写*/
                keyName = keyName.toLowerCase();
                String checkIndexSql = """
                        SELECT 1 AS 'EXISTS'
                        FROM INFORMATION_SCHEMA.STATISTICS
                        WHERE TABLE_SCHEMA = '%s'
                        AND TABLE_NAME = '%s'
                        AND INDEX_NAME = '%s'
                        """.formatted(getDbName(), tableName, keyName);
                Integer scalar = executeScalar(checkIndexSql, Integer.class);
                if (scalar == null || scalar != 1) {
                    String alterColumn = ddlBuilder.buildAlterColumnIndex(tableName, fieldMapping);
                    executeUpdate(alterColumn);
                    log.warn("mysql 数据库 {}，新增索引：{}", getSqlConfig().dbName(), keyName);
                }
            }
        }
    }

    @Override protected void createTable(TableMapping tableMapping, String tableName, String comment) {
        super.createTable(tableMapping, tableName, comment);
    }

    public void addPartition(String tableName, String partitionExpr) {
        String sql = """
                SELECT
                1 AS 'EXISTS'
                FROM
                INFORMATION_SCHEMA.PARTITIONS
                WHERE
                TABLE_SCHEMA = '%s'
                AND TABLE_NAME = '%s'
                AND PARTITION_NAME = '%s'
                """.formatted(getDbName(), tableName, tableName + "_" + partitionExpr);
        Integer scalar = executeScalar(sql, Integer.class);
        if (scalar == null || scalar != 1) {
            String string = """
                    ALTER TABLE %s ADD PARTITION (PARTITION %s_%s VALUES LESS THAN (%s))
                    """.formatted(tableName, tableName, partitionExpr, partitionExpr);
            executeUpdate(string);
            log.info("数据库 {} 表 {} 创建分区 {}", sqlConfig.dbName(), tableName, partitionExpr);
        }
    }
}
