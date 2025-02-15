package wxdgaming.boot2.starter.batis.sql.pgsql;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.batis.sql.SqlConfig;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;

/**
 * 数据集
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-15 12:36
 **/
@Getter
@Setter
public class PgsqlDataHelper extends SqlDataHelper {

    public PgsqlDataHelper(SqlConfig sqlConfig) {
        super(sqlConfig);
    }

}
