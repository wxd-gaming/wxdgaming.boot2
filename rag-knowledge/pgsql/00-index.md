# PostgreSQL 知识索引

适用项目：`wxd-gaming.boot2`

本知识库回答以下问题：

1. 如何在项目中启用 pgsql 模块？
2. Spring 配置里 `db.pgsql` 应该怎么写？
3. 如何定义实体类映射数据库表？
4. 如何插入/更新/保存实体对象？

关键类与路径：

- `wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlScan`
- `wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlDataHelper`
- `wxdgaming.boot2.starter.batis.sql.pgsql/src/test/resources/application.yml`
- `wxdgaming.boot2.starter.batis.sql.pgsql/src/test/java/run/PgsqlTest.java`
- `wxdgaming.boot2.starter.batis.sql.pgsql/src/test/java/run/entity/EntityTest.java`

依赖坐标：

```xml
<dependency>
    <groupId>io.github.wxd-gaming</groupId>
    <artifactId>wxd-gaming.boot2.starter.batis.sql.pgsql</artifactId>
    <version>1.0.3</version>
</dependency>
```
