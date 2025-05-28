package code;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mysql.cj.jdbc.Driver;
import org.junit.Test;
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.core.executor.ExecutorConfig;
import wxdgaming.boot2.core.util.YamlUtil;
import wxdgaming.boot2.starter.batis.sql.SqlConfig;
import wxdgaming.boot2.starter.net.client.SocketClientConfig;
import wxdgaming.boot2.starter.net.httpclient.HttpClientConfig;
import wxdgaming.boot2.starter.net.server.SocketServerConfig;
import wxdgaming.boot2.starter.net.server.http.HttpServerConfig;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-14 16:05
 **/
public class OutConfigTest {

    @Test
    public void out() {
        JSONObject config = BootConfig.getIns().getConfig();
        config.put("debug", true);
        config.put("sid", 1);
        config.put("executor",
                MapOf.newJSONObject()
                        .fluentPut("default", ExecutorConfig.DEFAULT_INSTANCE.toJSONObject())
                        .fluentPut("logic", ExecutorConfig.LOGIC_INSTANCE.toJSONObject())
                        .fluentPut("virtual", ExecutorConfig.VIRTUAL_INSTANCE.toJSONObject())
                        .fluentPut("scheduled", ExecutorConfig.VIRTUAL_INSTANCE.toJSONObject())
        );
        config.put("http",
                MapOf.newJSONObject()
                        .fluentPut("client", HttpClientConfig.DEFAULT.toJSONObject())

        );
        config.put(
                "socket",
                new JSONObject()
                        .fluentPut(
                                "server",
                                new SocketServerConfig().toJSONObject()
                                        .fluentPut("http", HttpServerConfig.INSTANCE.toJSONObject())
                        )
                        .fluentPut("client", new SocketClientConfig().toJSONObject())
        );
        JSONObject db = new JSONObject();
        config.put("db", db);
        {
            SqlConfig sqlConfig = new SqlConfig();
            sqlConfig.setDebug(true);
            sqlConfig.setDriverClassName(org.postgresql.Driver.class.getName());
            sqlConfig.setUrl("jdbc:postgresql://192.168.137.10:5432/test2");
            sqlConfig.setUsername("postgres");
            sqlConfig.setPassword("test");
            db.put("pgsql", sqlConfig.toJSONObject());
            db.put("pgsql-second", sqlConfig.toJSONObject());
        }
        {
            SqlConfig sqlConfig = new SqlConfig();
            sqlConfig.setDebug(true);
            sqlConfig.setUrl("jdbc:mysql://192.168.137.10:3306/test2");
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
