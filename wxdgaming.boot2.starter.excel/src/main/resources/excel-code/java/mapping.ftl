package ${packageName}.bean.mapping;


import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.starter.excel.store.DataKey;
import wxdgaming.boot2.starter.excel.store.DataMapping;

import java.io.Serializable;
import java.util.*;

/**
 * excel 构建 ${tableComment}, ${filePath}, ${tableName},
 *
 * @author: wxd-gaming(無心道, 15388152619)
 **/
@Getter
@Setter
@DataMapping(name = "${tableName}", comment = "${tableComment}", excelPath = "${filePath}", sheetName = "${tableName}")
public abstract class ${codeClassName}Mapping extends ObjectBase implements Serializable, DataKey {

<#list columns as column>
    /** ${column.columnComment} */
    <#if column.fieldTypeString?starts_with("List<")>
    protected final ${column.fieldTypeString} ${column.fieldNameLower} = new ArrayList<>();
    <#elseif column.fieldTypeString?starts_with("Map<")>
    protected final ${column.fieldTypeString} ${column.fieldNameLower} = new LinkedHashMap<>();
    <#else>
    protected ${column.fieldTypeString} ${column.fieldNameLower};
    </#if>
</#list>

    public Object key() {
        return ${keyColumn};
    }

}
