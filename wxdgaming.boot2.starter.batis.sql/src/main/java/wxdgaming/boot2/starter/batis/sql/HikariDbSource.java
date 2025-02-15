package wxdgaming.boot2.starter.batis.sql;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * HikariDataSource
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2023-12-11 10:45
 **/
public class HikariDbSource implements AutoCloseable {

    private final HikariDataSource hikariDataSource;

    public HikariDbSource(SqlConfig sqlConfig) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(sqlConfig.getDriverClassName());
        config.setJdbcUrl(sqlConfig.getUrl());
        config.setUsername(sqlConfig.getUsername());
        config.setPassword(sqlConfig.getPassword());
        config.setAutoCommit(false);
        config.setPoolName("wxd.db");
        config.setConnectionTimeout(2000);
        config.setIdleTimeout(TimeUnit.MINUTES.toMillis(10));
        config.setValidationTimeout(TimeUnit.SECONDS.toMillis(10));
        config.setKeepaliveTime(TimeUnit.MINUTES.toMillis(3));/*连接存活时间，这个值必须小于 maxLifetime 值。*/
        config.setMaxLifetime(TimeUnit.MINUTES.toMillis(6));/*池中连接最长生命周期。*/
        config.setMinimumIdle(sqlConfig.getMinPoolSize());/*池中最小空闲连接数，包括闲置和使用中的连接。*/
        config.setMaximumPoolSize(sqlConfig.getMaxPoolSize());/*池中最大连接数，包括闲置和使用中的连接。*/
        config.setConnectionTestQuery("SELECT 1");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("autoReconnect", "true");
        config.addDataSourceProperty("characterEncoding", "utf-8");
        this.hikariDataSource = new HikariDataSource(config);
    }

    public Connection getConnection() {
        try {
            return hikariDataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        hikariDataSource.close();
    }
}
