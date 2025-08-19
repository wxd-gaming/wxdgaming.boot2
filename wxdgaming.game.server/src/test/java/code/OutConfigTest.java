package code;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mysql.cj.jdbc.Driver;
import org.junit.Test;
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.core.executor.ExecutorProperties;
import wxdgaming.boot2.core.util.YamlUtil;
import wxdgaming.boot2.starter.batis.sql.SqlConfig;
import wxdgaming.boot2.starter.net.client.SocketClientConfig;
import wxdgaming.boot2.starter.net.httpclient5.HttpClientProperties;
import wxdgaming.boot2.starter.net.server.SocketServerConfig;
import wxdgaming.boot2.starter.net.server.http.HttpServerConfig;
import wxdgaming.boot2.starter.scheduled.ScheduledProperties;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-14 16:05
 **/
public class OutConfigTest {

    @Test
    public void out() {
        JSONObject config = BootConfig.getIns().getConfigNode();
        config.put(
                "boot",
                MapOf.newJSONObject()
                        .fluentPut("debug", true)
                        .fluentPut("sid", 1)
        );
        config.put("core", MapOf.newJSONObject().fluentPut("executor", new ExecutorProperties().toJSONObject())
        );
        config.put("scheduled", new ScheduledProperties().toJSONObject());
        config.put("http", MapOf.newJSONObject().fluentPut("client", ((HttpClientProperties) HttpClientProperties.DEFAULT.get()).toJSONObject()));
        config.put(
                "socket",
                new JSONObject()
                        .fluentPut(
                                "server",
                                new SocketServerConfig().toJSONObject()
                                        .fluentPut("http", FastJsonUtil.toJSONString(HttpServerConfig.INSTANCE.get()))
                        )
                        .fluentPut("client", new SocketClientConfig().toJSONObject())
        );
        JSONObject db = new JSONObject();
        config.put("db", db);
        {
            SqlConfig sqlConfig = new SqlConfig();
            sqlConfig.setDebug(true);
            sqlConfig.setDriverClassName(org.postgresql.Driver.class.getName());
            sqlConfig.setUrl("jdbc:postgresql://127.0.0.1:5432/test2");
            sqlConfig.setUsername("postgres");
            sqlConfig.setPassword("test");
            db.put("pgsql", sqlConfig.toJSONObject());
            db.put("pgsql-second", sqlConfig.toJSONObject());
        }
        {
            SqlConfig sqlConfig = new SqlConfig();
            sqlConfig.setDebug(true);
            sqlConfig.setUrl("jdbc:mysql://127.0.0.1:3306/test2");
            sqlConfig.setUsername("root");
            sqlConfig.setPassword("test");
            sqlConfig.setDriverClassName(Driver.class.getName());
            db.put("mysql", sqlConfig.toJSONObject());
            db.put("mysql-second", sqlConfig.toJSONObject());

        }
        String jsonFmt = JSON.toJSONString(
                config,
                SerializerFeature.PrettyFormat,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullStringAsEmpty
        );
        // System.out.println(jsonFmt);
        System.out.println(YamlUtil.dumpYaml(config));
    }

}
