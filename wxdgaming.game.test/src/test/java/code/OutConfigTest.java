package code;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mysql.cj.jdbc.Driver;
import org.junit.Test;
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.threading.ExecutorConfig;
import wxdgaming.boot2.core.util.YamlUtil;
import wxdgaming.boot2.starter.batis.sql.SqlConfig;
import wxdgaming.boot2.starter.net.client.SocketClientConfig;
import wxdgaming.boot2.starter.net.httpclient.HttpClientConfig;
import wxdgaming.boot2.starter.net.server.SocketServerConfig;
import wxdgaming.boot2.starter.net.server.http.HttpServerConfig;
import wxdgaming.boot2.starter.scheduled.ScheduledConfig;

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
        config.put("executor", FastJsonUtil.parse(ExecutorConfig.INSTANCE.toJsonString()));
        config.put("scheduled", FastJsonUtil.parse(ScheduledConfig.INSTANCE.toJsonString()));
        config.put("http", FastJsonUtil.parse(
                        new JSONObject()
                                .fluentPut("client", HttpClientConfig.DEFAULT)
                                .fluentPut("server", HttpServerConfig.INSTANCE)
                                .toString()
                )
        );
        config.put(
                "socket",
                new JSONObject()
                        .fluentPut("server", FastJsonUtil.parse(new SocketServerConfig().toJsonString()))
                        .fluentPut("client", FastJsonUtil.parse(new SocketClientConfig().toJsonString()))
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
            db.put("pgsql", FastJsonUtil.parse(sqlConfig.toString()));
            db.put("pgsql-second", FastJsonUtil.parse(sqlConfig.toString()));
        }
        {
            SqlConfig sqlConfig = new SqlConfig();
            sqlConfig.setDebug(true);
            sqlConfig.setUrl("jdbc:mysql://192.168.137.10:3306/test2");
            sqlConfig.setUsername("root");
            sqlConfig.setPassword("test");
            sqlConfig.setDriverClassName(Driver.class.getName());
            db.put("mysql", FastJsonUtil.parse(sqlConfig.toString()));
            db.put("mysql-second", FastJsonUtil.parse(sqlConfig.toString()));

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
