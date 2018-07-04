package ge.ai.domino.console.ui.util.service;

import ge.ai.domino.console.ui.util.dialog.WarnDialog;
import ge.ai.domino.domain.exception.DAIException;

public abstract class ServiceExecutor {

	public static void execute(ServiceExecutorTask serviceExecutorTask) {
		try {
			serviceExecutorTask.task();
		} catch (DAIException ex) {
			WarnDialog.showWarnDialog(ex);
		} catch (Exception ex) {
			ex.printStackTrace();
			WarnDialog.showUnexpectedError();
		}
	}
}
