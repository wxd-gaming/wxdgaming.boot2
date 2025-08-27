package wxdgaming.boot2.starter.batis.build.impl;

import wxdgaming.boot2.starter.batis.ColumnType;
import wxdgaming.boot2.starter.batis.TableMapping;
import wxdgaming.boot2.starter.batis.build.IBuildColumn;
import wxdgaming.boot2.starter.batis.build.IColumnFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-26 20:51
 **/
public class BuildColumnLong implements IBuildColumn {

    @Override public void register(IColumnFactory columnFactory) {
        columnFactory.register(long.class, this);
        columnFactory.register(Long.class, this);
        columnFactory.register(AtomicLong.class, this);
    }

    public void buildColumn(TableMapping.FieldMapping fieldMapping) {
        fieldMapping.setColumnType(ColumnType.Long);
    }

}
