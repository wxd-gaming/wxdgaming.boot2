package wxdgaming.boot2.starter.batis.sql;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 查询结果
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-11 20:02
 **/
@Getter
public class SqlQueryResult implements AutoCloseable {

    private final Connection conn;
    private final PreparedStatement statement;
    private final ResultSet resultSet;

    public SqlQueryResult(Connection conn, String sql, Object... args) throws SQLException {
        this.conn = conn;
        this.statement = this.conn.prepareStatement(sql);
        for (int i = 0; i < args.length; i++) {
            Object param = args[i];
            statement.setObject(i + 1, param);
        }
        this.resultSet = statement.executeQuery();
    }

    @Override public void close() throws Exception {
        resultSet.close();
        statement.close();
        conn.close();
    }

    public boolean hasNext() throws SQLException {
        return resultSet.next();
    }

    public JSONObject row() throws SQLException {
        JSONObject jsonObject = new JSONObject(true);
        for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
            jsonObject.put(resultSet.getMetaData().getColumnName(i), resultSet.getObject(i));
        }
        return jsonObject;
    }

    public Object get(int columnIndex) throws SQLException {
        return resultSet.getObject(columnIndex);
    }

    public Object get(String columnName) throws SQLException {
        return resultSet.getObject(columnName);
    }

    public String getString(int columnIndex) throws SQLException {
        return resultSet.getString(columnIndex);
    }

    public String getString(String columnName) throws SQLException {
        return resultSet.getString(columnName);
    }

    public int getInt(int columnIndex) throws SQLException {
        return resultSet.getInt(columnIndex);
    }

    public int getInt(String columnName) throws SQLException {
        return resultSet.getInt(columnName);
    }

    public long getLong(int columnIndex) throws SQLException {
        return resultSet.getLong(columnIndex);
    }

    public long getLong(String columnName) throws SQLException {
        return resultSet.getLong(columnName);
    }

}
