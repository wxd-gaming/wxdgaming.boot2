package wxdgaming.boot2.starter.batis.build.impl;

import wxdgaming.boot2.starter.batis.ColumnType;
import wxdgaming.boot2.starter.batis.TableMapping;
import wxdgaming.boot2.starter.batis.build.IBuildColumn;
import wxdgaming.boot2.starter.batis.build.IColumnFactory;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-26 20:51
 **/
public class BuildColumnBlob implements IBuildColumn {

    @Override public void register(IColumnFactory columnFactory) {
        columnFactory.register(byte[].class, this);
    }

    public void buildColumn(TableMapping.FieldMapping fieldMapping) {
        if (fieldMapping.getLength() == 0)
            fieldMapping.setLength(65535);
        fieldMapping.setColumnType(ColumnType.Blob);
    }

}
