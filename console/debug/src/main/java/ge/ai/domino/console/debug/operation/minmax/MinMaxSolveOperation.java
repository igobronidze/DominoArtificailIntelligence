package ge.ai.domino.console.debug.operation.minmax;

import ge.ai.domino.console.debug.GameDebuggerHelper;
import ge.ai.domino.console.debug.operation.GameDebuggerOperation;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.manager.game.ai.minmax.MinMax;
import ge.ai.domino.manager.game.ai.minmax.MinMaxFactory;
import ge.ai.domino.manager.game.logging.RoundLogger;
import ge.ai.domino.manager.parser.RoundParserManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MinMaxSolveOperation implements GameDebuggerOperation {

	private static final Logger logger = Logger.getLogger(MinMaxSolveOperation.class);

	private static final RoundParserManager roundParserManager = new RoundParserManager();

	private static final String NEXT_LOG = "NEXT";

	@Override
	public void process(Scanner scanner) throws DAIException {
		logger.info("Enter round logs(Pleas type " + NEXT_LOG + " for next log and " + GameDebuggerHelper.LOG_END + " for finish");

		List<Round> rounds = new ArrayList<>();
		int gameId = GameDebuggerHelper.GAME_ID;
		String s;
		StringBuilder log = new StringBuilder();
		while (!(s = scanner.nextLine()).equals(GameDebuggerHelper.LOG_END)) {
			if (s.equals(NEXT_LOG)) {
				Round round = roundParserManager.parseRound(log.toString());
				logger.info("Round parsed successfully id[" + gameId + "]");

				round.getGameInfo().setGameId(gameId);
				gameId--;
				rounds.add(round);
				log = new StringBuilder();
			} else {
				log.append(s).append(RoundLogger.END_LINE);
			}
		}

		logger.info("Use multithreading:");
		boolean useMultithreading = Boolean.valueOf(scanner.nextLine());

		for (Round round : rounds) {
			MinMax minMax = MinMaxFactory.getMinMax(useMultithreading);
			minMax.solve(round);
		}
		logger.info("Executed MinMax solve for " + rounds.size() + " nodeRounds");
	}
}
