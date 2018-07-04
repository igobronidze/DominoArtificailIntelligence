package ge.ai.domino.console.ui.util.service;

import ge.ai.domino.domain.exception.DAIException;

@FunctionalInterface
public interface ServiceExecutorTask {

	void task() throws DAIException;
}
