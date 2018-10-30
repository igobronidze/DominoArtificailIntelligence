package ge.ai.domino.console.debug.operation.minmax;

import ge.ai.domino.console.debug.GameDebuggerHelper;
import ge.ai.domino.console.debug.operation.GameDebuggerOperation;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.GameInitialData;
import org.apache.log4j.Logger;

import java.util.Scanner;

public class SendGameToMultithreadingClientOperation implements GameDebuggerOperation {

	private static final Logger logger = Logger.getLogger(SendGameToMultithreadingClientOperation.class);

	@Override
	public void process(Scanner scanner) throws DAIException {
		logger.info("Game ID:");
		int gameId = Integer.parseInt(scanner.nextLine());
		logger.info("Point for win:");
		int pointForWin = Integer.parseInt(scanner.nextLine());
		GameInitialData gameInitialData = new GameInitialData();
		gameInitialData.setGameId(gameId);
		gameInitialData.setPointsForWin(pointForWin);
		GameDebuggerHelper.multithreadingServer.initGame(gameInitialData);
	}
}
