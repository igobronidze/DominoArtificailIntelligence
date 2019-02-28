package ge.ai.domino.console.debug.operation.minmax;

import ge.ai.domino.console.debug.operation.GameDebuggerOperation;
import ge.ai.domino.manager.multiprocessorserver.MultiProcessorServer;

import java.util.Scanner;

public class StartMultiProcessorServerOperation implements GameDebuggerOperation {

	private static final MultiProcessorServer multiProcessorServer = MultiProcessorServer.getInstance();

	@Override
	public void process(Scanner scanner) {
		new Thread(multiProcessorServer::startServer).start();
	}
}
