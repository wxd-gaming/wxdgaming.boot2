package wxdgaming.boot2.starter.batis.sql.pgsql;

import wxdgaming.boot2.starter.batis.sql.SqlDataBatch;

/**
 * 批量
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-16 20:49
 **/
public class PgsqlDataBatch extends SqlDataBatch {

    public PgsqlDataBatch(PgsqlDataHelper dataHelper) {
        super(dataHelper);
    }

    @Override public PgsqlDataHelper dataHelper() {
        return super.dataHelper();
    }
}
