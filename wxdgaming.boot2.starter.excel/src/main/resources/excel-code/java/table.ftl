package ${packageName};


import lombok.Getter;
import wxdgaming.boot2.starter.excel.store.DataTable;
import ${packageName}.bean.${codeClassName};

import java.io.Serializable;
import java.util.Map;


/**
 * excel 构建 ${tableComment}, ${filePath}, ${tableName},
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: ${.now?string("yyyy-MM-dd HH:mm:ss")}
 **/
@Getter
public class ${codeClassName}Table extends DataTable<${codeClassName}> implements Serializable {

    @Override public void initDb() {
        /*todo 实现一些数据分组*/

    }

    @Override public void checkData(Map<Class<?>, DataTable<?>> store) {
        /*todo 实现数据校验 */
    }

}