package ${packageName}.bean;


import lombok.Getter;
import wxdgaming.boot2.starter.excel.store.DataChecked;
import ${packageName}.bean.mapping.${codeClassName}Mapping;

import java.io.Serializable;


/**
 * excel 构建 ${tableComment}, ${filePath}, ${tableName},
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: ${.now?string("yyyy-MM-dd HH:mm:ss")}
 **/
@Getter
public class ${codeClassName} extends ${codeClassName}Mapping implements Serializable, DataChecked {

    @Override public void initAndCheck() throws Exception {
        /*todo 实现数据检测和初始化*/

    }

}
