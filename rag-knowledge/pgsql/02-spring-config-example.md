# Spring PostgreSQL 配置示例

在 `application.yml` 中添加：

```yaml
db:
  pgsql:
    debug: false
    driverClassName: "org.postgresql.Driver"
    url: "jdbc:postgresql://127.0.0.1:5432/pgtest"
    username: "postgres"
    password: "test"
    scanPackage: ["run.entity"]
    connectionTimeoutMs: 2000
    idleTimeoutM: 10
    minPoolSize: 5
    maxPoolSize: 20
    batchSubmitSize: 500
    batchThreadSize: 4
    cacheArea: 64
```

字段说明（常用）：

- `url`: PostgreSQL JDBC 地址。
- `scanPackage`: 实体类包路径数组，必须包含你的实体类所在包。
- `debug`: 打开后会输出 SQL，开发阶段可开启。
- `minPoolSize` / `maxPoolSize`: 连接池大小。
