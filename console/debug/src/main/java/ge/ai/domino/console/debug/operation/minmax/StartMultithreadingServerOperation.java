package ge.ai.domino.console.debug.operation.minmax;

import ge.ai.domino.console.debug.operation.GameDebuggerOperation;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.manager.multithreadingserver.MultithreadingServer;

import java.util.Scanner;

public class StartMultithreadingServerOperation implements GameDebuggerOperation {

	private static final MultithreadingServer multithreadingServer = MultithreadingServer.getInstance();

	@Override
	public void process(Scanner scanner) throws DAIException {
		new Thread(multithreadingServer::startServer).start();
	}
}
