package wxdgaming.game.login.inner;

import com.alibaba.fastjson.JSONObject;
import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.event.StopBeforeEvent;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.boot2.starter.net.httpclient5.HttpRequestPost;
import wxdgaming.game.authority.SignUtil;
import wxdgaming.game.common.global.GlobalDataService;
import wxdgaming.game.login.LoginServerProperties;
import wxdgaming.game.login.bean.global.GlobalDataConst;
import wxdgaming.game.login.bean.global.ServerShowNameGlobalData;
import wxdgaming.game.login.entity.ServerInfoEntity;
import wxdgaming.game.login.entity.UserData;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Function;

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

    public List<JSONObject> gameServerList(UserData userData) {
        ServerShowNameGlobalData showNameGlobalData = globalDataService.get(GlobalDataConst.ServerNameGlobalData);
        boolean checkUser = (userData != null && userData.isWhite() || userData != null && userData.getGmLevel() > 0);
        final Function<Integer, String> serverRoleInfo = new Function<Integer, String>() {
            @Override public String apply(Integer integer) {
                if (userData == null)
                    return "{}";
                return userData.getGameRoleMap().getOrDefault(integer, "{}");
            }
        };
        return getInnerGameServerInfoMap().values().stream()
                .filter(bean -> checkUser || System.currentTimeMillis() > bean.getOpenTime())
                .map(bean -> {
                    JSONObject map = new JSONObject();
                    map.put("id", bean.getServerId());
                    map.put("name", showNameGlobalData.showName(bean.getServerId(), bean.getName()));
                    map.put("host", bean.getHost());
                    map.put("port", bean.getPort());
                    map.put("openTime", bean.getOpenTime());
                    map.put("maintenanceTime", bean.getMaintenanceTime());
                    map.put("roleInfo", serverRoleInfo.apply(bean.getServerId()));
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
    @EventListener
    public void stopBefore(StopBeforeEvent event) {
        for (ServerInfoEntity bean : innerGameServerInfoMap.values()) {
            sqlDataHelper.getDataBatch().save(bean);
        }
    }

}
