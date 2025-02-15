package wxdgaming.boot2.starter.batis.sql.pgsql;

import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.core.util.AnnUtil;
import wxdgaming.boot2.starter.batis.ColumnType;
import wxdgaming.boot2.starter.batis.TableMapping;
import wxdgaming.boot2.starter.batis.sql.SqlDDLBuilder;
import wxdgaming.boot2.starter.batis.sql.ann.Partition;

/**
 * pgsql
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-15 17:51
 **/
public class MySqlDDLBuilder extends SqlDDLBuilder {

    @Override public StringBuilder buildTableSqlString(TableMapping tableMapping, String tableName) {
        StringBuilder table = super.buildTableSqlString(tableMapping, tableName);
        tableMapping.getColumns().values()
                .stream()
                .filter(v -> AnnUtil.ann(v.getField(), Partition.class) != null)
                .findFirst()
                .ifPresent(fieldMapping -> {
                    String yyyyMMdd = MyClock.formatDate("yyyyMMdd");
                    table.append(" PARTITION BY RANGE").append("(")
                            .append(fieldMapping.getColumnName())
                            .append(")")
                            .append("(PARTITION %s_%s VALUES LESS THAN (%s))".formatted(tableName, yyyyMMdd, yyyyMMdd));
                });
        return table;
    }

}
