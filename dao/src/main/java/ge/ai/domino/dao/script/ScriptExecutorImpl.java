package ge.ai.domino.dao.script;

import ge.ai.domino.dao.connection.ConnectionUtil;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ScriptExecutorImpl implements ScriptExecutor {

	private Logger logger = Logger.getLogger(ScriptExecutorImpl.class);

	private PreparedStatement pstmt;

	@Override
	public void executeUpdate(String script) {
		try {
			logger.info("Start executing update script[" + script + "]");
			pstmt = ConnectionUtil.getConnection().prepareStatement(script);
			pstmt.executeUpdate();
			logger.info("Script executed successfully");
		} catch (SQLException ex) {
			logger.error("Error occurred while add system parameter", ex);
		} finally {
			ConnectionUtil.closeConnection();
		}
	}
}
