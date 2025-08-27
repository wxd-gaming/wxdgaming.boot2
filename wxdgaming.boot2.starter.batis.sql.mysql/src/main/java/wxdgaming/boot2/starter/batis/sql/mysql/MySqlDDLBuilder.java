package wxdgaming.boot2.starter.batis.sql.mysql;

import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.reflect.AnnUtil;
import wxdgaming.boot2.starter.batis.TableMapping;
import wxdgaming.boot2.starter.batis.columnconvert.ColumnConvertFactory;
import wxdgaming.boot2.starter.batis.sql.SqlDDLBuilder;
import wxdgaming.boot2.starter.batis.sql.ann.Partition;

/**
 * pgsql
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-15 17:51
 **/
public class MySqlDDLBuilder extends SqlDDLBuilder {

    public MySqlDDLBuilder() {
        super(ColumnConvertFactory.getInstance());
    }

    @Override public StringBuilder buildTableSqlString(TableMapping tableMapping, String tableName, boolean actionPartition) {
        StringBuilder tableSql = super.buildTableSqlString(tableMapping, tableName, actionPartition);
        if (actionPartition) {
            tableMapping.getColumns().values().stream()
                    .filter(v -> AnnUtil.ann(v.getField(), Partition.class) != null)
                    .findFirst()
                    .ifPresent(fieldMapping -> {
                        Partition partition = AnnUtil.ann(fieldMapping.getField(), Partition.class);
                        String minRangeValue = partition.mysqlInitMinRangeValue();
                        String[] strings = partition.initRangeArrays();
                        tableSql.append(" PARTITION BY RANGE")
                                .append("(").append(fieldMapping.getColumnName()).append(")");
                        if (strings != null && strings.length > 0 && StringUtils.isNotBlank(strings[0])) {
                            tableSql.append("(");
                            for (int i = 0; i < strings.length; i++) {
                                String s = strings[i];
                                if (StringUtils.isBlank(s)) {
                                    continue;
                                }
                                tableSql.append("PARTITION p_%s VALUES LESS THAN (%s)".formatted(i, s));
                            }
                            tableSql.append(")");
                        } else {
                            tableSql.append("(PARTITION p_min_value VALUES LESS THAN (%s))".formatted(minRangeValue));
                        }
                    });
        }
        return tableSql;
    }

}
