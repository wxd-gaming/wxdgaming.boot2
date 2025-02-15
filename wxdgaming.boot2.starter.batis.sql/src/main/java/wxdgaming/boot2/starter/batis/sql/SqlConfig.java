package wxdgaming.boot2.starter.batis.sql;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.Throw;
import wxdgaming.boot2.core.lang.ObjectBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.function.Consumer;

/**
 * 数据库配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-15 12:43
 **/
@Slf4j
@Getter
@Setter
public class SqlConfig extends ObjectBase {

    private boolean debug;
    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private int minPoolSize = 5;
    private int maxPoolSize = 20;

    public String getDbName() {
        String dbName = url;
        int indexOf = dbName.indexOf("?");
        if (indexOf > 0) {
            dbName = dbName.substring(0, indexOf);
        }
        int indexOf1 = dbName.lastIndexOf('/');
        dbName = dbName.substring(indexOf1 + 1);
        return dbName;
    }

    /** 创建数据库 , 吃方法创建数据库后会自动使用 use 语句 */
    public void createDatabase() {
        if (url.contains("jdbc:mysql")) {
            String dbName = getDbName();
            try (Connection connection = getConnection("INFORMATION_SCHEMA")) {
                Consumer<String> stringConsumer = (character) -> {
                    String databaseString = "CREATE DATABASE IF NOT EXISTS `%s` DEFAULT CHARACTER SET %s COLLATE %s_unicode_ci"
                            .formatted(dbName.toLowerCase(), character, character);
                    try (Statement statement = connection.createStatement()) {
                        int update = statement.executeUpdate(databaseString);
                        log.info("mysql 数据库 {} 创建 {}", dbName, update);
                    } catch (Exception e) {
                        throw Throw.of(e);
                    }
                };
                try {
                    stringConsumer.accept("utf8mb4");
                } catch (Throwable t) {
                    if (t.getMessage().contains("utf8mb4")) {
                        log.warn("mysql 数据库 {} 不支持 utf8mb4 格式 重新用 utf8 字符集创建数据库", dbName, new RuntimeException());
                        stringConsumer.accept("utf8");
                    } else {
                        log.error("mysql 创建数据库 {}", dbName, t);
                    }
                }
            } catch (Exception e) {
                log.error("mysql 创建数据库 {}", dbName, e);
            }
        } else if (url.contains("jdbc:postgresql:")) {
            String dbName = getDbName();
            try (Connection connection = getConnection("postgres"); Statement statement = connection.createStatement()) {
                String formatted = "SELECT 1 as t FROM pg_database WHERE datname = '%s'".formatted(dbName);
                ResultSet resultSet = statement.executeQuery(formatted);
                if (resultSet.next()) {
                    log.debug("pgsql 数据库 {} 已经存在", dbName);
                    return;
                }
                boolean execute = statement.execute("CREATE DATABASE %s".formatted(dbName));
                log.info("pgsql 数据库 {} 创建 {}", dbName, execute);
            } catch (Exception e) {
                log.error("pgsql 创建数据库 {}", dbName, e);
            }
        }
    }

    public Connection getConnection(String databaseName) {
        try {
            Class.forName(getDriverClassName());
            return DriverManager.getConnection(
                    url.replace(getDbName(), databaseName),
                    username,
                    password
            );
        } catch (Exception e) {
            throw Throw.of(e);
        }
    }


}
