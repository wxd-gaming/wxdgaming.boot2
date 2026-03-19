# RAG Knowledge Base

这个目录用于给 AI/RAG 系统提供项目知识库，重点覆盖 PostgreSQL 使用。

建议加载顺序：

1. `pgsql/00-index.md`
2. `pgsql/01-enable-pgsql.md`
3. `pgsql/02-spring-config-example.md`
4. `pgsql/03-entity-and-insert-example.md`
5. `pgsql/04-ai-query-templates.md`

推荐切片策略：

- chunk_size: `500 ~ 1200`
- chunk_overlap: `80 ~ 150`
- 检索 top_k: `3 ~ 6`
- 优先保留代码块（不要在代码块中间切断）

最小问答流程：

1. 用户指定 `rag-knowledge/pgsql` 目录作为知识库。
2. 用户提问（例如“怎么引用 pgsql 并插入实体数据”）。
3. AI 先检索，再按知识库中的配置与代码示例回答。
