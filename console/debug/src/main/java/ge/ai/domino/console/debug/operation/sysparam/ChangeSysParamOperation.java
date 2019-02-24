package ge.ai.domino.console.debug.operation.sysparam;

import ge.ai.domino.console.debug.operation.GameDebuggerOperation;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import org.apache.log4j.Logger;

import java.util.Scanner;

public class ChangeSysParamOperation implements GameDebuggerOperation {

	private static final Logger logger = Logger.getLogger(ChangeSysParamOperation.class);

	private static final SystemParameterManager sysParamManager = new SystemParameterManager();

	@Override
	public void process(Scanner scanner) {
		logger.info("key:");
		String key = scanner.nextLine();
		logger.info("value:");
		String value = scanner.nextLine();
		sysParamManager.changeParameterOnlyInCache(key, value);
		logger.info("Sys param changed successfully");
	}
}
