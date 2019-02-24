package ge.ai.domino.console.debug.operation.minmax;

import ge.ai.domino.console.debug.operation.GameDebuggerOperation;
import ge.ai.domino.manager.multithreadingserver.MultithreadingServer;

import java.util.Scanner;

public class StartMultithreadingServerOperation implements GameDebuggerOperation {

	private static final MultithreadingServer multithreadingServer = MultithreadingServer.getInstance();

	@Override
	public void process(Scanner scanner) {
		new Thread(multithreadingServer::startServer).start();
	}
}
