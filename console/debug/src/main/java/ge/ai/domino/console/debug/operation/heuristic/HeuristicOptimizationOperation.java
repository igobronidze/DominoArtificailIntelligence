package ge.ai.domino.console.debug.operation.heuristic;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.console.debug.GameDebuggerHelper;
import ge.ai.domino.console.debug.operation.GameDebuggerOperation;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Game;
import ge.ai.domino.domain.game.GameFromLog;
import ge.ai.domino.domain.game.GameInitialData;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.heuristic.Heuristic;
import ge.ai.domino.domain.heuristic.RoundHeuristicType;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.game.helper.game.GameOperations;
import ge.ai.domino.manager.game.logging.RoundLogger;
import ge.ai.domino.manager.parser.GameParserManager;
import ge.ai.domino.math.optimization.OptimizationDirection;
import ge.ai.domino.math.optimization.unimodal.multipleparams.ParamInterval;
import ge.ai.domino.math.optimization.unimodal.multipleparams.UnimodalOptimizationWithMultipleParams;
import ge.ai.domino.math.optimization.unimodal.oneparam.UnimodalOptimizationType;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class HeuristicOptimizationOperation implements GameDebuggerOperation {

	private static final Logger logger = Logger.getLogger(HeuristicOptimizationOperation.class);

	private static final SysParam roundHeuristicType = new SysParam("roundHeuristicType", "POINT_DIFF_ROUND_HEURISTIC");

	private static final GameParserManager gameParserManager = new GameParserManager();

	private final SysParam useMultithreadingMinMax = new SysParam("useMultithreadingMinMax", "true");

	@Override
	public void process(Scanner scanner) throws DAIException {
		logger.info("Enter log files path(please type " + GameDebuggerHelper.LOG_END + " for finish)");
		List<String> paths = new ArrayList<>();

		String path;
		while (!(path = scanner.nextLine()).equals(GameDebuggerHelper.LOG_END)) {
			paths.add(path);
		}

		List<GameFromLog> games = new ArrayList<>();
		for (String filePath : paths) {
			File file = new File(filePath);
			if (file.exists()) {
				games.addAll(gameParserManager.parseAllGameInFile(new File(filePath)));
			} else {
				logger.warn("File not exists[" + filePath + "]");
			}
		}
		logger.info("Parsed " + games.size() + " game");

		logger.info("Enter RoundHeuristicType");
		for (RoundHeuristicType roundHeuristicType : RoundHeuristicType.values()) {
			logger.info(roundHeuristicType.name());
		}
		String chosenType = scanner.nextLine();
		GameDebuggerHelper.sysParamManager.changeParameterOnlyInCache(roundHeuristicType.getKey(), chosenType);

		logger.info("Iteration count");
		int iterationCount = Integer.valueOf(scanner.nextLine());

		logger.info("Rounds count in iteration");
		int roundCountInIteration = Integer.valueOf(scanner.nextLine());

		List<Round> allRound = new ArrayList<>();
		for (GameFromLog gameFromLog : games) {
			Game game = new Game();
			game.setId(gameFromLog.getGameId());
			GameProperties gameProperties = new GameProperties();
			gameProperties.setPointsForWin(gameFromLog.getPointForWin());
			gameProperties.setChannel(gameFromLog.getChannel());
			game.setProperties(gameProperties);
			CachedGames.addGame(game);

			if (GameDebuggerHelper.sysParamManager.getBooleanParameterValue(useMultithreadingMinMax)) {
				GameInitialData gameInitialData = new GameInitialData();
				gameInitialData.setGameId(gameFromLog.getGameId());
				gameInitialData.setPointsForWin(gameFromLog.getPointForWin());
				GameDebuggerHelper.multithreadingServer.initGame(gameInitialData);
			}

			allRound.addAll(gameFromLog.getRounds());
		}

		UnimodalOptimizationWithMultipleParams unimodalOptimizationWithMultipleParams =
				new UnimodalOptimizationWithMultipleParams(UnimodalOptimizationType.INTERVAL_DIVISION, OptimizationDirection.MIN) {
			@Override
			public double getValue(List<Double> params) {
				try {
					return getHeuristicsAverage(allRound.subList(0, roundCountInIteration), chosenType, params);
				} catch (Exception ex) {
					logger.error(ex);
					return 0.0;
				}
			}
		};

		List<Double> params = new ArrayList<>();  // TODO[IG]
		// TODO[IG] Use Heuristic change param names
		List<ParamInterval> intervals = getParamIntervals(params);

		for (int i = 1; i <= iterationCount; i++) {
			logger.info("Starting heuristic optimization iteration[" + i + "]");
			Collections.shuffle(games);

			params = unimodalOptimizationWithMultipleParams.getExtremaVector(params, intervals, iterationCount);
			logger.info("Finished heuristic optimization iteration[" + i + "]");
			logger.info("New values: " + params);
			try {
				Thread.sleep(1 * 60 * 1000);
			} catch (InterruptedException ex) {
				logger.error(ex);
			}
		}
	}

	private double getHeuristicsAverage(List<Round> rounds, String chosenType, List<Double> params) throws DAIException {
		List<Heuristic> heuristics = new ArrayList<>();
		for (Round round : rounds) {
			logger.info("Executing check round heuristic");
			if (!round.getTableInfo().isMyMove()) {
				logger.info("Not my turn");
				continue;
			}
			if (round.getTableInfo().getLeft() == null && round.getMyTiles().size() != 7) {
				logger.info("Add initial tile move");
				continue;
			}
			if (GameOperations.getPossibleMoves(round, false).isEmpty()) {
				logger.info("No possible move");
				continue;
			}

			Heuristic heuristic = GameDebuggerHelper.heuristicManager.getHeuristic(round, RoundHeuristicType.valueOf(chosenType), params);
			heuristics.add(heuristic);
		}

		heuristics.sort(Comparator.comparingDouble(o -> Math.abs(o.getValue() - o.getAiValue())));
		for (Heuristic heuristic : heuristics) {
			RoundLogger.logRoundFullInfo(heuristic.getRound());
			logger.info("Heuristic value: " + heuristic.getValue());
			logger.info("AI heuristic value: " + heuristic.getAiValue());
		}

		double sum = 0.0;
		for (Heuristic heuristic : heuristics) {
			sum += Math.abs(heuristic.getValue() - heuristic.getAiValue());
		}
		double average = sum / heuristics.size();
		System.out.println("Average: " + average);
		return average;
	}

	private List<ParamInterval> getParamIntervals(List<Double> params) {
		return null; //TODO[IG]
	}
}
