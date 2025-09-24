package wxdgaming.game.login.inner;

import com.alibaba.fastjson.JSONObject;
import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.ann.StopBefore;
import wxdgaming.boot2.core.util.SignUtil;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.boot2.starter.net.httpclient5.HttpRequestPost;
import wxdgaming.game.common.global.GlobalDataService;
import wxdgaming.game.login.LoginServerProperties;
import wxdgaming.game.login.bean.global.GlobalDataConst;
import wxdgaming.game.login.bean.global.ServerShowName;
import wxdgaming.game.login.bean.global.ServerShowNameGlobalData;
import wxdgaming.game.login.entity.ServerInfoEntity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

    final LoginServerProperties loginServerProperties;
    final SqlDataHelper sqlDataHelper;
    final ConcurrentSkipListMap<Integer, ServerInfoEntity> innerGameServerInfoMap = new ConcurrentSkipListMap<>();
    final GlobalDataService globalDataService;

    public InnerService(LoginServerProperties loginServerProperties, PgsqlDataHelper sqlDataHelper, GlobalDataService globalDataService) {
        this.loginServerProperties = loginServerProperties;
        this.sqlDataHelper = sqlDataHelper;
        this.globalDataService = globalDataService;
        this.sqlDataHelper.checkTable(ServerInfoEntity.class);

        sqlDataHelper.findList(ServerInfoEntity.class).forEach(bean -> {
            log.info("InnerService: {}", bean);
            innerGameServerInfoMap.put(bean.getServerId(), bean);
        });

    }

    public List<JSONObject> gameServerList(boolean white, int gmLevel) {
        ServerShowNameGlobalData showNameGlobalData = globalDataService.get(GlobalDataConst.ServerNameGlobalData);
        ConcurrentHashMap<Integer, ServerShowName> serverNameMap = showNameGlobalData.getServerNameMap();
        return getInnerGameServerInfoMap().values().stream()
                .filter(bean -> System.currentTimeMillis() > bean.getOpenTime())
                .map(bean -> {
                    JSONObject map = new JSONObject();
                    map.put("id", bean.getServerId());
                    map.put("name", bean.getName());
                    ServerShowName serverShowName = serverNameMap.get(bean.getServerId());
                    if (serverShowName != null && StringUtils.isNotBlank(serverShowName.getName())
                        && System.currentTimeMillis() < serverShowName.getExpireTime()) {
                        /*TODO 服务器冠名有效期*/
                        map.put("name", serverShowName.getName());
                    }
                    map.put("host", bean.getHost());
                    map.put("port", bean.getPort());
                    return map;
                })
                .toList();
    }

    public void executeAll(String flag, String url, Map<String, ?> params) {
        for (ServerInfoEntity serverInfo : getInnerGameServerInfoMap().values()) {
            executeServer(flag, url, params, serverInfo);
        }
    }

    public void executeServer(String flag, String url, Map<String, ?> params, ServerInfoEntity serverInfo) {
        String sign = SignUtil.signByFormData(params, loginServerProperties.getJwtKey());
        String host = serverInfo.getHost();
        int httpPort = serverInfo.getHttpPort();
        if (StringUtils.isBlank(host) || httpPort < 1000) {
            return;
        }
        String formatted = "http://%s:%s/%s".formatted(host, httpPort, url);
        HttpRequestPost.of(formatted, params)
                .addHeader(HttpHeaderNames.AUTHORIZATION.toString(), sign)
                .executeAsync()
                .subscribe(
                        httpResponse -> {
                            log.info("远程调用：{}-{} {}：{}, {}", serverInfo.getServerId(), serverInfo.getName(), flag, params, httpResponse);
                        },
                        throwable -> {
                            log.info("远程调用：{}-{} {}: {} 请求异常", serverInfo.getServerId(), serverInfo.getName(), flag, params, throwable);
                        }
                );
    }

    @Order(10)
    @StopBefore
    public void stopBefore() {
        for (ServerInfoEntity bean : innerGameServerInfoMap.values()) {
            sqlDataHelper.getDataBatch().save(bean);
        }
    }

}
