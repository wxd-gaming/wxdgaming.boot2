package wxdgaming.boot2.starter.batis.sql;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import wxdgaming.boot2.core.Throw;
import wxdgaming.boot2.core.json.FastJsonUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 查询结果
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-11 20:02
 **/
@Slf4j
@Getter
public class SqlQueryResult implements AutoCloseable {

    private final Connection conn;
    private final PreparedStatement statement;
    private final ResultSet resultSet;

    public SqlQueryResult(SqlDataHelper dataHelper, String sql, Object... args) {
        try {
            this.conn = dataHelper.connection();
            this.statement = this.conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                Object param = args[i];
                statement.setObject(i + 1, param);
            }
            if (dataHelper.sqlConfig.isDebug()) {
                log.info(
                        "\n {} query sql: \n{}",
                        dataHelper.getDbName(), statement.toString()
                );
            }
            this.resultSet = statement.executeQuery();
        } catch (SQLException e) {
            throw Throw.of(sql, e);
        }
    }

    @Override public void close() {
        try {
            resultSet.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public boolean hasNext() {
        try {
            return resultSet.next();
        } catch (SQLException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public <R> R scalar(Class<R> cls) {
        Object object = getScalar();
        if (object != null) {
            if (cls.isAssignableFrom(object.getClass())) {
                return cls.cast(object);
            } else {
                return FastJsonUtil.parse(String.valueOf(object), cls);
            }
        }
        return null;
    }

    public Object getScalar() {
        if (hasNext()) {
            return get(1);
        }
        return null;
    }

    public <R> List<R> scalarList(Class<R> cls) {
        ArrayList<R> objectList = new ArrayList<>();
        while (hasNext()) {
            Object object = get(1);
            if (object == null) {
                continue;
            }
            if (cls.isAssignableFrom(object.getClass())) {
                objectList.add(cls.cast(object));
            } else {
                objectList.add(FastJsonUtil.parse(String.valueOf(object), cls));
            }
        }
        return objectList;
    }

    public List<Object> getScalarList() {
        ArrayList<Object> objectList = new ArrayList<>();
        while (hasNext()) {
            objectList.add(get(1));
        }
        return objectList;
    }

    public List<JSONObject> rowList() {
        ArrayList<JSONObject> jsonObjectList = new ArrayList<>();
        while (hasNext()) {
            jsonObjectList.add(row());
        }
        return jsonObjectList;
    }

    public JSONObject row() {
        try {
            JSONObject jsonObject = new JSONObject(true);
            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                jsonObject.put(resultSet.getMetaData().getColumnName(i), resultSet.getObject(i));
            }
            return jsonObject;
        } catch (SQLException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public Object get(int columnIndex) {
        try {
            return resultSet.getObject(columnIndex);
        } catch (SQLException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public Object get(String columnName) {
        try {
            return resultSet.getObject(columnName);
        } catch (SQLException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public String getString(int columnIndex) {
        try {
            return resultSet.getString(columnIndex);
        } catch (SQLException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public String getString(String columnName) {
        try {
            return resultSet.getString(columnName);
        } catch (SQLException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public int getInt(int columnIndex) {
        try {
            return resultSet.getInt(columnIndex);
        } catch (SQLException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public int getInt(String columnName) {
        try {
            return resultSet.getInt(columnName);
        } catch (SQLException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public long getLong(int columnIndex) {
        try {
            return resultSet.getLong(columnIndex);
        } catch (SQLException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public long getLong(String columnName) {
        try {
            return resultSet.getLong(columnName);
        } catch (SQLException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

}
