package ml.iamwhatiam.ag.support;

import ml.iamwhatiam.ag.constants.Generic;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author iMinusMinus
 * @date 2019-08-29
 */
public class GenericHandler extends BaseTypeHandler<Generic> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Generic quantization, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, quantization.getNumber());
    }

    @Override
    public Generic getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        int value = resultSet.getInt(columnName);
        return Generic.getInstance(value);
    }

    @Override
    public Generic getNullableResult(ResultSet resultSet, int i) throws SQLException {
        int value = resultSet.getInt(i);
        return Generic.getInstance(value);
    }

    @Override
    public Generic getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        int value = callableStatement.getInt(i);
        return Generic.getInstance(value);
    }
}
