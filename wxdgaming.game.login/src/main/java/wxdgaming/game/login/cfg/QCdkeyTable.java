package wxdgaming.game.login.cfg;


import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.starter.excel.store.DataTable;
import wxdgaming.game.login.cfg.bean.QCdkey;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * excel 构建 任务集合, src/main/cfg/激活码.xlsx, q_cdkey,
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-01 11:13:07
 **/
@Getter
public class QCdkeyTable extends DataTable<QCdkey> implements Serializable {

    private Map<String, QCdkey> codeMap;

    @Override public void initDb() {
        /*todo 实现一些数据分组*/
        List<QCdkey> dataList = getDataList();
        HashMap<String, QCdkey> tmpMap = new HashMap<>();
        for (QCdkey qCdkey : dataList) {
            if (StringUtils.isNotBlank(qCdkey.getCode())) {
                tmpMap.put(qCdkey.getCode().toUpperCase(), qCdkey);
            }
        }
        codeMap = Collections.unmodifiableMap(tmpMap);
    }

    @Override public void checkData(Map<Class<?>, DataTable<?>> store) {
        /*todo 实现数据校验 */
    }

}