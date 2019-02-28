package ge.ai.domino.console.debug.operation.minmax;

import ge.ai.domino.console.debug.operation.GameDebuggerOperation;
import ge.ai.domino.domain.game.GameInitialData;
import ge.ai.domino.manager.multiprocessorserver.MultiProcessorServer;
import org.apache.log4j.Logger;

import java.util.Scanner;

public class SendGameToMultiProcessorClientOperation implements GameDebuggerOperation {

	private static final Logger logger = Logger.getLogger(SendGameToMultiProcessorClientOperation.class);

	private static final MultiProcessorServer multiProcessorServer = MultiProcessorServer.getInstance();

	@Override
	public void process(Scanner scanner) {
		logger.info("Game ID:");
		int gameId = Integer.parseInt(scanner.nextLine());
		logger.info("Point for win:");
		int pointForWin = Integer.parseInt(scanner.nextLine());
		GameInitialData gameInitialData = new GameInitialData();
		gameInitialData.setGameId(gameId);
		gameInitialData.setPointsForWin(pointForWin);
		multiProcessorServer.initGame(gameInitialData);
	}
}
