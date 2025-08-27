package wxdgaming.boot2.starter.batis.build.impl;

import wxdgaming.boot2.starter.batis.ColumnType;
import wxdgaming.boot2.starter.batis.TableMapping;
import wxdgaming.boot2.starter.batis.build.IBuildColumn;
import wxdgaming.boot2.starter.batis.build.IColumnFactory;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-26 20:51
 **/
public class BuildColumnByte implements IBuildColumn {

    @Override public void register(IColumnFactory columnFactory) {
        columnFactory.register(byte.class, this);
        columnFactory.register(Byte.class, this);
    }

    public void buildColumn(TableMapping.FieldMapping fieldMapping) {
        fieldMapping.setColumnType(ColumnType.Byte);
    }

}
