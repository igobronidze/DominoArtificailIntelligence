package ge.ai.domino.console.ui.util.service;

import ge.ai.domino.console.ui.util.dialog.WarnDialog;
import ge.ai.domino.domain.exception.DAIException;
import javafx.application.Platform;
import org.apache.log4j.Logger;

public abstract class ServiceExecutor {

	private static final Logger logger = Logger.getLogger(ServiceExecutor.class);

	public void execute(ServiceExecutorTask serviceExecutorTask) {
		Thread thread = new Thread(() -> {
			try {
				serviceExecutorTask.task();

				if (isAsync()) {
					Platform.runLater(this::onAsyncProcessFinish);
				}
			} catch (DAIException ex) {
				Platform.runLater(this::onError);
				Platform.runLater(() -> WarnDialog.showWarnDialog(ex));
			} catch (Exception e) {
				Platform.runLater(this::onError);
				logger.error("Unexpected Error", e);
				Platform.runLater(WarnDialog::showUnexpectedError);
			}
		});

		thread.start();

		if (!isAsync()) {
			try {
				thread.join();
			} catch (InterruptedException ignored) {}
		}
	}

	public void onAsyncProcessFinish() {}

	public boolean isAsync() {
		return false;
	}

	public void onError() {}
}
