package ge.ai.domino.console.debug.operation.sysparam;

import ge.ai.domino.console.debug.operation.GameDebuggerOperation;
import ge.ai.domino.dao.sysparam.SystemParameterDAO;
import ge.ai.domino.dao.sysparam.SystemParameterDAOImpl;
import ge.ai.domino.domain.exception.DAIException;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

public class SysParamsUpdateOperation implements GameDebuggerOperation {

	private Logger logger = Logger.getLogger(SysParamsUpdateOperation.class);

	private final SystemParameterDAO systemParameterDAO = new SystemParameterDAOImpl();

	@Override
	public void process(Scanner scanner) throws DAIException {
		logger.info("Properties file path:");
		String path = scanner.nextLine();

		try {
			Properties props = new Properties();
			props.load(new FileInputStream(path));
			for (Map.Entry<Object, Object> entry : props.entrySet()) {
				systemParameterDAO.editSystemParameter((String) entry.getKey(), (String) entry.getValue());
			}
		} catch (IOException ex) {
			logger.error("Error occurred while read properties file for update sys params[" + path + "]");
		}
	}
}
