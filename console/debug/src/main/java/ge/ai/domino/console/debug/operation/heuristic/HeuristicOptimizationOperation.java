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
import ge.ai.domino.manager.game.helper.play.PossibleMovesManager;
import ge.ai.domino.manager.game.logging.RoundLogger;
import ge.ai.domino.manager.heuristic.HeuristicManager;
import ge.ai.domino.manager.multiprocessorserver.MultiProcessorServer;
import ge.ai.domino.manager.parser.GameParserManager;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
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

	private static final HeuristicManager heuristicManager = new HeuristicManager();

	private static final MultiProcessorServer multiProcessorServer = MultiProcessorServer.getInstance();

	private static final SystemParameterManager sysParamManager = new SystemParameterManager();

	private static final SysParam roundHeuristicType = new SysParam("roundHeuristicType", "POINT_DIFF_ROUND_HEURISTIC");

	private static final GameParserManager gameParserManager = new GameParserManager();

	private final SysParam useMultiProcessorMinMax = new SysParam("useMultiProcessorMinMax", "true");

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

		List<Round> rounds = new ArrayList<>();
		int allRoundSize = 0;
		for (GameFromLog gameFromLog : games) {
			Game game = new Game();
			game.setId(gameFromLog.getGameId());
			GameProperties gameProperties = new GameProperties();
			gameProperties.setPointsForWin(gameFromLog.getPointForWin());
			gameProperties.setChannel(gameFromLog.getChannel());
			game.setProperties(gameProperties);
			CachedGames.addGame(game);

			if (sysParamManager.getBooleanParameterValue(useMultiProcessorMinMax)) {
				GameInitialData gameInitialData = new GameInitialData();
				gameInitialData.setGameId(gameFromLog.getGameId());
				gameInitialData.setPointsForWin(gameFromLog.getPointForWin());
				multiProcessorServer.initGame(gameInitialData);
			}

			for (Round round : gameFromLog.getRounds()) {
				if (!round.getTableInfo().isMyMove()) {
					continue;
				}
				if (round.getTableInfo().getLeft() == null && round.getMyTiles().size() != 7) {
					continue;
				}
				if (PossibleMovesManager.getPossibleMoves(round, false).isEmpty()) {
					continue;
				}
				rounds.add(round);
			}
			allRoundSize += gameFromLog.getRounds().size();
		}

		logger.info("Parsed games[" + games.size() + "], allRound[" + allRoundSize + "], usefulRounds[" + rounds.size() + "]");

		logger.info("Enter RoundHeuristicType");
		for (RoundHeuristicType roundHeuristicType : RoundHeuristicType.values()) {
			logger.info(roundHeuristicType.name());
		}
		String chosenType = scanner.nextLine();
		sysParamManager.changeParameterOnlyInCache(roundHeuristicType.getKey(), chosenType);

		int iterationCount = GameDebuggerHelper.readInt(logger, scanner, "Iteration count:");
		int optimizationInnerIteration = GameDebuggerHelper.readInt(logger, scanner, "Optimization inner iteration count:");
		int roundCountInIteration = GameDebuggerHelper.readInt(logger, scanner, "Rounds count in iteration:");

		UnimodalOptimizationWithMultipleParams unimodalOptimizationWithMultipleParams =
				new UnimodalOptimizationWithMultipleParams(UnimodalOptimizationType.INTERVAL_DIVISION, OptimizationDirection.MIN) {
			@Override
			public double getValue(List<Double> params) {
				try {
					return getHeuristicsAverage(rounds.subList(0, roundCountInIteration), chosenType, params);
				} catch (Exception ex) {
					logger.error(ex);
					return 0.0;
				}
			}
		};

		List<Double> params = getInitialParams();
		List<ParamInterval> intervals = getInitialParamIntervals();

		for (int i = 1; i <= iterationCount; i++) {
			logger.info("Starting heuristic optimization iteration[" + i + "]");
			Collections.shuffle(games);

			params = new ArrayList<>(unimodalOptimizationWithMultipleParams.getExtremaVector(params, intervals, optimizationInnerIteration));
			logger.info("Finished heuristic optimization iteration[" + i + "]");
			logger.info("New values: " + params);
			try {
				Thread.sleep(5 * 1000);
			} catch (InterruptedException ex) {
				logger.error(ex);
			}
		}
	}

	private double getHeuristicsAverage(List<Round> rounds, String chosenType, List<Double> params) throws DAIException {
		List<Heuristic> heuristics = new ArrayList<>();
		logger.info("Executing check round heuristic, params: " + params);
		for (Round round : rounds) {
			Heuristic heuristic = heuristicManager.getHeuristic(round, RoundHeuristicType.valueOf(chosenType), params);
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
		logger.info("Average heuristic diff: " + average);
		return average;
	}

	private List<Double> getInitialParams() {
		List<Double> params = new ArrayList<>();
		params.add(0.5);   // roundStatisticProcessorParam1
		params.add(0.15);   // roundStatisticProcessorParam2
		params.add(0.08);   // roundStatisticProcessorParam3
		params.add(1.8);   // mixedRoundHeuristicTilesDiffRate
		params.add(8.0);   // mixedRoundHeuristicMovesDiffRate
		params.add(0.3);   // mixedRoundHeuristicPointsBalancingRate
		params.add(0.15);   // mixedRoundHeuristicOpenTilesSumBalancingRate
		params.add(0.5);   // mixedRoundHeuristicPointsDiffCoefficientRate
		params.add(0.3);   // mixedRoundHeuristicPLayTurnRate
		return params;
	}

	private List<ParamInterval> getInitialParamIntervals() {
		List<ParamInterval> params = new ArrayList<>();
		params.add(new ParamInterval(0.3, 0.7));   // roundStatisticProcessorParam1
		params.add(new ParamInterval(0.1, 0.3));   // roundStatisticProcessorParam2
		params.add(new ParamInterval(0.01, 0.15));   // roundStatisticProcessorParam3
		params.add(new ParamInterval(1.5, 2.0));   // mixedRoundHeuristicTilesDiffRate
		params.add(new ParamInterval(5, 12));   // mixedRoundHeuristicMovesDiffRate
		params.add(new ParamInterval(0.1, 0.5));   // mixedRoundHeuristicPointsBalancingRate
		params.add(new ParamInterval(0.05, 0.25));   // mixedRoundHeuristicOpenTilesSumBalancingRate
		params.add(new ParamInterval(0.3, 0.7));   // mixedRoundHeuristicPointsDiffCoefficientRate
		params.add(new ParamInterval(0.15, 0.45));   // mixedRoundHeuristicPLayTurnRate
		return params;
	}
}
