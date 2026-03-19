# AI 提问模板（给使用你项目的人）

把这个目录作为知识库后，可直接使用下面问题：

## 模板 1：如何引用 pgsql

```text
基于我指定的知识库，请告诉我在 wxd-gaming.boot2 项目里如何启用 PostgreSQL。
请给出：
1) Maven 依赖
2) 启动类需要加的 Scan 类
3) 最小可用的 application.yml 配置
```

## 模板 2：生成 Spring 配置

```text
请根据知识库生成一个可直接使用的 Spring PostgreSQL 配置示例，
数据库地址使用 127.0.0.1:5432/demo，用户名 postgres，密码 123456，
scanPackage 使用 com.demo.entity。
```

## 模板 3：实体插入代码

```text
请根据知识库生成一个实体类和插入示例代码：
1) 实体包含 uid/day/name 字段
2) 使用 PgsqlDataHelper 做 insert、update、save
3) 给出按主键查询 findByKey 示例
```

## 模板 4：完整上手答案

```text
我第一次接入这个项目的 pgsql 能力。
请按“依赖 -> 启动 -> 配置 -> 实体 -> 插入测试”顺序输出完整步骤，
每一步都给可复制代码。
```
