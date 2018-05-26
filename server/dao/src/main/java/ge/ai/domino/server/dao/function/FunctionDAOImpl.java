package ge.ai.domino.server.dao.function;

import ge.ai.domino.domain.function.FunctionArgsAndValues;
import ge.ai.domino.server.dao.DatabaseUtil;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class FunctionDAOImpl implements FunctionDAO {

	private Logger logger = Logger.getLogger(FunctionDAOImpl.class);

	private PreparedStatement pstmt;

	@Override
	public Map<String, FunctionArgsAndValues> getFunctionArgsAndValues(String namePrefix) {
		Map<String, FunctionArgsAndValues> functionArgsAndValuesMap = new HashMap<>();
		try {
			String sql = "SELECT function_name, arg, value FROM arg_and_value WHERE function_name LIKE ?";
			pstmt = DatabaseUtil.getConnection().prepareStatement(sql);
			pstmt.setString(1, namePrefix + "%");
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String name = rs.getString("function_name");
				if (functionArgsAndValuesMap.get(name) == null) {
					functionArgsAndValuesMap.put(name, new FunctionArgsAndValues());
				}
				FunctionArgsAndValues functionArgsAndValues = functionArgsAndValuesMap.get(name);
				functionArgsAndValues.getArgs().add(rs.getDouble("arg"));
				functionArgsAndValues.getValues().add(rs.getDouble("value"));
			}
		} catch (SQLException ex) {
			logger.error("Error occurred while getting function args and values [namePrefix=" + namePrefix + "]", ex);
		} finally {
			DatabaseUtil.closeConnection();
		}
		return functionArgsAndValuesMap;
	}
}
