package wxdgaming.boot2.starter.batis.build;

import wxdgaming.boot2.starter.batis.TableMapping;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-26 21:05
 **/
public interface IBuildColumn {

    void register(IColumnFactory columnFactory);

    void buildColumn(TableMapping.FieldMapping fieldMapping);

}
