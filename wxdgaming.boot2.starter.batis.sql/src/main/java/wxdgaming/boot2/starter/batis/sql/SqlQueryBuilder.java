package wxdgaming.boot2.starter.batis.sql;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.io.Objects;
import wxdgaming.boot2.starter.batis.Entity;
import wxdgaming.boot2.starter.batis.TableMapping;

import java.util.List;

/**
 * sql 查询 构造器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-21 09:43
 **/
@Getter
@Setter
@Accessors(chain = true)
public abstract class SqlQueryBuilder {

    private final SqlDataHelper sqlDataHelper;

    /** 查询的字段 */
    private String selectField = "*";
    private String tableName = null;
    private String where = "";
    private String groupBy = "";
    private String orderBy = "";
    private int skip = 0;
    private int limit = 0;

    private Object[] parameters = new Object[0];

    public SqlQueryBuilder(SqlDataHelper sqlDataHelper) {
        this.sqlDataHelper = sqlDataHelper;
    }

    public String getTableName() {
        String tmp = tableName;
        if (!tmp.startsWith("`")) {
            tmp = "`" + tmp;
        }
        if (!tmp.endsWith("`")) {
            tmp += "`";
        }
        return tmp;
    }

    public void setSkip(int skip) {
        if (skip < 0) skip = 0;
        this.skip = skip;
    }

    public void setLimit(int limit) {
        if (limit < 0) limit = 0;
        this.limit = limit;
    }

    public void limit(int skip, int limit, int minLimit, int maxLimit) {
        setSkip(skip);
        if (limit < minLimit)
            limit = minLimit;
        if (limit > maxLimit)
            limit = maxLimit;
        setLimit(limit);
    }

    public SqlQueryBuilder sqlByEntity(Class<? extends Entity> clazz) {
        String tableName = TableMapping.tableName(clazz);
        this.setTableName(tableName);
        return this;
    }

    /**
     * 当value != null 才会push
     *
     * @param where 例如： a=?
     * @param param 1
     * @return
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2025-03-08 20:49
     */
    public SqlQueryBuilder pushWhereByValueNotNull(String where, Object param) {
        if (Objects.nonNullEmpty(param))
            pushWhere(where, param);
        return this;
    }

    /**
     * 添加 where 条件
     *
     * @param where 例如： a=?
     * @param param 1
     * @return
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2025-03-08 20:48
     */
    public SqlQueryBuilder pushWhere(String where, Object param) {
        if (Objects.nullEmpty(param)) throw new IllegalArgumentException("param null or empty");
        if (StringUtils.isNotBlank(where)) {
            if (StringUtils.isNotBlank(this.where)) {
                this.where += " and ";
            }
            this.where += where;
            pushParameter(param);
        }
        return this;
    }

    protected void pushParameter(Object... parameters) {
        this.parameters = Objects.merge(this.parameters, parameters);
    }

    protected void buildSkip(StringBuilder builder) {
        if (getSkip() > 0)
            builder.append(" OFFSET ").append(getSkip());
    }

    public String buildSelectSql() {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        builder.append(getSelectField());
        builder.append(" FROM ").append(getTableName());

        if (StringUtils.isNotBlank(getWhere())) {
            builder.append(" WHERE ").append(getWhere());
        }
        if (StringUtils.isNotBlank(getGroupBy())) {
            builder.append(" GROUP BY ").append(getGroupBy());
        }
        if (StringUtils.isNotBlank(getOrderBy())) {
            builder.append(" ORDER BY ").append(getOrderBy());
        }

        buildSkip(builder);

        if (getLimit() > 0) {
            builder.append(" LIMIT ").append(getLimit());
        }
        return builder.toString();
    }


    public String buildCountSql() {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT COUNT(1) FROM ").append(getTableName());
        if (StringUtils.isNotBlank(getWhere())) {
            builder.append(" WHERE ").append(getWhere());
        }
        return builder.toString();
    }

    /** 查询满足条件的数据库数据行数 */
    public long findCount() {
        return sqlDataHelper.tableCountBySql(buildCountSql(), getParameters());
    }

    /** 查询满足条件的所有数据 */
    public <R extends Entity> List<R> findList2Entity(Class<R> entityClass) {
        return sqlDataHelper.findListBySql(entityClass, buildSelectSql(), getParameters());
    }

    /** 返回第一行，第一列 */
    public <R> R executeScalar(Class<R> resultClass) {
        return sqlDataHelper.executeScalar(buildSelectSql(), resultClass, getParameters());
    }

    /** 返回第一列 */
    public <R> List<R> executeList(Class<R> resultClass) {
        return sqlDataHelper.executeScalarList(buildSelectSql(), resultClass, getParameters());
    }

}
