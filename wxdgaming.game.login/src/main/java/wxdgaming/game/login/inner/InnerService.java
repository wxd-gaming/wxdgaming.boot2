package wxdgaming.game.login.inner;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.ann.Stop;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.game.basic.login.bean.info.InnerServerInfoBean;
import wxdgaming.game.common.global.GlobalDataService;
import wxdgaming.game.login.bean.global.GlobalDataConst;
import wxdgaming.game.login.bean.global.ServerShowName;
import wxdgaming.game.login.bean.global.ServerShowNameGlobalData;

import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 内网服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-10 20:52
 **/
@Slf4j
@Getter
@Service
public class InnerService {

    final SqlDataHelper sqlDataHelper;
    final ConcurrentSkipListMap<Integer, InnerServerInfoBean> innerGameServerInfoMap = new ConcurrentSkipListMap<>();
    final GlobalDataService globalDataService;

    public InnerService(PgsqlDataHelper sqlDataHelper, GlobalDataService globalDataService) {
        this.sqlDataHelper = sqlDataHelper;
        this.globalDataService = globalDataService;
        this.sqlDataHelper.checkTable(InnerServerInfoBean.class);

        sqlDataHelper.findList(InnerServerInfoBean.class).forEach(bean -> {
            log.info("InnerService: {}", bean);
            innerGameServerInfoMap.put(bean.getServerId(), bean);
        });

    }

    public List<JSONObject> gameServerList() {
        return getInnerGameServerInfoMap().values().stream()
                .map(bean -> {
                    JSONObject jsonObject = bean.toJSONObject();
                    jsonObject.put("id", bean.getServerId());
                    ServerShowNameGlobalData showNameGlobalData = globalDataService.get(GlobalDataConst.ServerNameGlobalData);
                    jsonObject.put("name", bean.getName());
                    ServerShowName serverShowName = showNameGlobalData.getServerNameMap().get(bean.getServerId());
                    if (serverShowName != null && StringUtils.isNotBlank(serverShowName.getName())
                        && System.currentTimeMillis() < serverShowName.getExpireTime()) {
                        /*TODO 服务器冠名有效期*/
                        jsonObject.put("name", serverShowName.getName());
                    }
                    jsonObject.put("host", bean.getHost());
                    jsonObject.put("port", bean.getPort());
                    return jsonObject;
                })
                .toList();
    }

    @Order(10)
    @Stop
    public void stop() {
        innerGameServerInfoMap.values().forEach(bean -> {
            sqlDataHelper.getDataBatch().save(bean);
        });
    }

}
