package wxdgaming.boot2.starter.batis.sql;

import com.alibaba.fastjson.annotation.JSONField;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import wxdgaming.boot2.core.Throw;
import wxdgaming.boot2.core.lang.ObjectBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 数据库配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-15 12:43
 **/
@Slf4j
@Getter
@Setter
public class SqlConfig extends ObjectBase {

    @JSONField(ordinal = 1)
    private boolean debug;
    @JSONField(ordinal = 2)
    private String driverClassName = "";
    @JSONField(ordinal = 3)
    private String url;
    @JSONField(ordinal = 4)
    private String username;
    @JSONField(ordinal = 5)
    private String password;
    @JSONField(ordinal = 6)
    private int connectionTimeoutMs = 2000;
    @JSONField(ordinal = 7)
    private int minPoolSize = 5;
    @JSONField(ordinal = 8)
    private int maxPoolSize = 20;
    /** 单位分钟 */
    @JSONField(ordinal = 9)
    private int idleTimeoutM = 10;
    @JSONField(ordinal = 11)
    private String[] scanPackage = new String[0];
    /** 默认异步批处理线程数量 */
    @JSONField(ordinal = 12)
    private int batchThreadSize = 1;
    /** 线程池单次批处理提交的数量 */
    @JSONField(ordinal = 13)
    private int batchSubmitSize = 500;
    /** 连接池配置缓存限制 */
    @JSONField(ordinal = 14)
    private int prepStmtCacheSize = 500;
    /** 连接池配置缓存限制 */
    @JSONField(ordinal = 15)
    private int prepStmtCacheSqlLimit = 100;
    /** cache分区 */
    @JSONField(ordinal = 15)
    private int cacheArea = 1;
    /** cache过期时间 */
    @JSONField(ordinal = 15)
    private int cacheExpireAfterAccessM = 120;

    public String dbName() {
        String dbName = url;
        int indexOf = dbName.indexOf("?");
        if (indexOf > 0) {
            dbName = dbName.substring(0, indexOf);
        }
        int indexOf1 = dbName.lastIndexOf('/');
        dbName = dbName.substring(indexOf1 + 1);
        return dbName;
    }

    public HikariDataSource hikariDataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(getDriverClassName());
        config.setJdbcUrl(getUrl());
        config.setUsername(getUsername());
        config.setPassword(getPassword());
        config.setAutoCommit(true);
        config.setPoolName(dbName());
        config.setConnectionTimeout(connectionTimeoutMs);
        config.setIdleTimeout(TimeUnit.MINUTES.toMillis(idleTimeoutM));
        config.setValidationTimeout(TimeUnit.SECONDS.toMillis(10));
        config.setKeepaliveTime(TimeUnit.MINUTES.toMillis(3));/*连接存活时间，这个值必须小于 maxLifetime 值。*/
        config.setMaxLifetime(TimeUnit.MINUTES.toMillis(6));/*池中连接最长生命周期。*/
        config.setMinimumIdle(getMinPoolSize());/*池中最小空闲连接数，包括闲置和使用中的连接。*/
        config.setMaximumPoolSize(getMaxPoolSize());/*池中最大连接数，包括闲置和使用中的连接。*/
        config.setConnectionTestQuery("SELECT 1");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", prepStmtCacheSize);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", prepStmtCacheSqlLimit);
        config.addDataSourceProperty("autoReconnect", "true");
        config.addDataSourceProperty("characterEncoding", "utf-8");
        return new HikariDataSource(config);
    }

    /** 创建数据库 , 吃方法创建数据库后会自动使用 use 语句 */
    public void createDatabase() {
        if (url.contains("jdbc:mysql")) {
            String dbName = dbName();
            try (Connection connection = connection("INFORMATION_SCHEMA");) {
                try (Statement statement = connection.createStatement()) {
                    String formatted = "SHOW DATABASES LIKE '%s';".formatted(dbName);
                    ResultSet resultSet = statement.executeQuery(formatted);
                    if (resultSet.next()) {
                        log.debug("mysql 数据库 {} 已经存在", dbName);
                        return;
                    }
                    Consumer<String> stringConsumer = (character) -> {
                        String databaseString = "CREATE DATABASE IF NOT EXISTS `%s` DEFAULT CHARACTER SET %s COLLATE %s_unicode_ci"
                                .formatted(dbName.toLowerCase(), character, character);
                        try {
                            statement.executeUpdate(databaseString);
                            log.info("mysql 数据库 {} 创建完成", dbName);
                        } catch (Exception e) {
                            throw ExceptionUtils.asRuntimeException(e);
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
            } catch (Exception e) {
                log.error("mysql 数据库连接失败 {}", dbName, e);
            }
        } else if (url.contains("jdbc:postgresql:")) {
            String dbName = dbName();
            try (Connection connection = connection("postgres")) {
                try (Statement statement = connection.createStatement()) {
                    String formatted = "SELECT 1 as t FROM pg_database WHERE datname = '%s'".formatted(dbName);
                    ResultSet resultSet = statement.executeQuery(formatted);
                    if (resultSet.next()) {
                        log.debug("pgsql 数据库 {} 已经存在", dbName);
                        return;
                    }
                    statement.execute("CREATE DATABASE %s".formatted(dbName));
                    log.info("pgsql 数据库 {} 创建完成", dbName);
                } catch (Exception e) {
                    log.error("pgsql 创建数据库 {}", dbName, e);
                }
            } catch (Exception e) {
                log.error("pgsql 数据库连接失败 {}", dbName, e);
            }
        }
    }

    public Connection connection(String databaseName) {
        String replace = "";
        try {
            Class.forName(getDriverClassName());
            replace = url.replace(dbName(), databaseName);
            return DriverManager.getConnection(
                    replace,
                    username,
                    password
            );
        } catch (Exception e) {
            throw Throw.of(replace, e);
        }
    }

    public SqlConfig clone(String dbName) {
        SqlConfig clone = (SqlConfig) super.clone();
        String replace = clone.url.replace(dbName(), dbName);
        clone.setUrl(replace);
        return clone;
    }
}
