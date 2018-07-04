package ge.ai.domino.dao.function;

import ge.ai.domino.dao.connection.ConnectionUtil;
import ge.ai.domino.domain.function.FunctionArgsAndValues;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class FunctionDAOImpl implements FunctionDAO {

	private Logger logger = Logger.getLogger(FunctionDAOImpl.class);

	private static final String ARG_AND_VALUE_TABLE_NAME = "arg_and_value";

	private static final String FUNCTION_NAME_COLUMN_NAME = "function_name";

	private static final String ARG_COLUMN_NAME = "arg";

	private static final String VALUE_COLUMN_NAME = "value";

	private PreparedStatement pstmt;

	@Override
	public Map<String, FunctionArgsAndValues> getFunctionArgsAndValues(String namePrefix) {
		Map<String, FunctionArgsAndValues> functionArgsAndValuesMap = new HashMap<>();
		try {
			String sql = String.format("SELECT %s, %s, %s FROM %s WHERE %s LIKE ?",
					FUNCTION_NAME_COLUMN_NAME, ARG_COLUMN_NAME, VALUE_COLUMN_NAME, ARG_AND_VALUE_TABLE_NAME, FUNCTION_NAME_COLUMN_NAME);
			pstmt = ConnectionUtil.getConnection().prepareStatement(sql);
			pstmt.setString(1, namePrefix + "%");
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String name = rs.getString("function_name");
				functionArgsAndValuesMap.putIfAbsent(name, new FunctionArgsAndValues());
				FunctionArgsAndValues functionArgsAndValues = functionArgsAndValuesMap.get(name);
				functionArgsAndValues.getArgs().add(rs.getDouble("arg"));
				functionArgsAndValues.getValues().add(rs.getDouble("value"));
			}
		} catch (SQLException ex) {
			logger.error("Error occurred while getting function args and values [namePrefix=" + namePrefix + "]", ex);
		} finally {
			ConnectionUtil.closeConnection();
		}
		return functionArgsAndValuesMap;
	}
}
