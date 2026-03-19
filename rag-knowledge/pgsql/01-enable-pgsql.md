# 如何开启 PostgreSQL

## 1) 引入依赖

```xml
<dependency>
    <groupId>io.github.wxd-gaming</groupId>
    <artifactId>wxd-gaming.boot2.starter.batis.sql.pgsql</artifactId>
    <version>1.0.3</version>
</dependency>
```

## 2) 启动时加入扫描类

在启动代码中加入 `PgsqlScan.class`：

```java
RunApplication run = WxdApplication.run(
        CoreScan.class,
        ScheduledScan.class,
        NetScan.class,
        PgsqlScan.class,
        Main.class
);
```

说明：

- `PgsqlScan` 会通过 `@ComponentScan` 自动加载 pgsql 相关配置和组件。
- 未加入 `PgsqlScan` 时，`PgsqlDataHelper` 不会被自动注入。
