package ge.ai.domino.server.dao.function;

import ge.ai.domino.domain.function.FunctionArgsAndValues;
import ge.ai.domino.server.dao.DatabaseUtil;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FunctionDAOImpl implements FunctionDAO {

	private Logger logger = Logger.getLogger(FunctionDAOImpl.class);

	private PreparedStatement pstmt;

	@Override
	public FunctionArgsAndValues getFunctionArgsAndValues(String name) {
		FunctionArgsAndValues functionArgsAndValues = new FunctionArgsAndValues();
		functionArgsAndValues.setName(name);
		try {
			String sql = "SELECT arg, value FROM arg_and_value WHERE function_name = ?";
			pstmt = DatabaseUtil.getConnection().prepareStatement(sql);
			pstmt.setString(1, name);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				functionArgsAndValues.getArgs().add(rs.getDouble("arg"));
				functionArgsAndValues.getValues().add(rs.getDouble("value"));
			}
		} catch (SQLException ex) {
			logger.error("Error occurred while getting function args and values [name=" + name + "]", ex);
		} finally {
			DatabaseUtil.closeConnection();
		}
		return functionArgsAndValues;
	}
}
