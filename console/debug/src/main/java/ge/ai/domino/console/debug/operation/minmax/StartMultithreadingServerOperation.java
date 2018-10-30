package ge.ai.domino.console.debug.operation.minmax;

import ge.ai.domino.console.debug.GameDebuggerHelper;
import ge.ai.domino.console.debug.operation.GameDebuggerOperation;
import ge.ai.domino.domain.exception.DAIException;

import java.util.Scanner;

public class StartMultithreadingServerOperation implements GameDebuggerOperation {

	@Override
	public void process(Scanner scanner) throws DAIException {
		new Thread(GameDebuggerHelper.multithreadingServer::startServer).start();
	}
}
