package ge.ai.domino.console.debug.operation.game;

import ge.ai.domino.console.debug.GameDebuggerHelper;
import ge.ai.domino.console.debug.operation.GameDebuggerOperation;
import ge.ai.domino.domain.exception.DAIException;
import org.apache.log4j.Logger;

import java.util.Scanner;

public class ChangeSysParamOperation implements GameDebuggerOperation {

	private static final Logger logger = Logger.getLogger(ChangeSysParamOperation.class);

	@Override
	public void process(Scanner scanner) throws DAIException {
		logger.info("key:");
		String key = scanner.nextLine();
		logger.info("value:");
		String value = scanner.nextLine();
		GameDebuggerHelper.sysParamManager.changeParameterOnlyInCache(key, value);
		logger.info("Sys param changed successfully");
	}
}
