package ge.ai.domino.console.ui.util.service;

import ge.ai.domino.console.ui.util.dialog.WarnDialog;
import ge.ai.domino.domain.exception.DAIException;
import org.apache.log4j.Logger;

public abstract class ServiceExecutor {

	private static final Logger logger = Logger.getLogger(ServiceExecutor.class);

	public static void execute(ServiceExecutorTask serviceExecutorTask) {
		try {
			serviceExecutorTask.task();
		} catch (DAIException ex) {
			WarnDialog.showWarnDialog(ex);
		} catch (Exception ex) {
			logger.error(ex);
			WarnDialog.showUnexpectedError();
		}
	}
}
