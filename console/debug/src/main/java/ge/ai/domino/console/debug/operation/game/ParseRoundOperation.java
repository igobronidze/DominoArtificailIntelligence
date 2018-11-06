package ge.ai.domino.console.debug.operation.game;

import ge.ai.domino.console.debug.GameDebuggerHelper;
import ge.ai.domino.console.debug.operation.GameDebuggerOperation;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.manager.game.logging.RoundLogger;
import ge.ai.domino.manager.parser.RoundParserManager;
import org.apache.log4j.Logger;

import java.util.Scanner;

public class ParseRoundOperation implements GameDebuggerOperation {

	private static final Logger logger = Logger.getLogger(ParseRoundOperation.class);

	private static final RoundParserManager roundParserManager = new RoundParserManager();

	public void process(Scanner scanner) throws DAIException {
		String s;
		StringBuilder log = new StringBuilder();
		while (!(s = scanner.nextLine()).equals(GameDebuggerHelper.LOG_END)) {
			log.append(s).append(RoundLogger.END_LINE);
		}
		GameDebuggerHelper.round = roundParserManager.parseRound(log.toString());
		GameDebuggerHelper.round.getGameInfo().setGameId(GameDebuggerHelper.GAME_ID);
		logger.info("Round parsed successfully");
	}
}
