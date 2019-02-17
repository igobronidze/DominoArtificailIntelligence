package ge.ai.domino.console.debug.operation.minmax;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.console.debug.GameDebuggerHelper;
import ge.ai.domino.console.debug.operation.GameDebuggerOperation;
import ge.ai.domino.dao.function.FunctionDAO;
import ge.ai.domino.dao.function.FunctionDAOImpl;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.function.FunctionArgsAndValues;
import ge.ai.domino.domain.game.opponentplay.GroupedOpponentPlay;
import ge.ai.domino.domain.game.opponentplay.OpponentPlay;
import ge.ai.domino.domain.played.ReplayMoveInfo;
import ge.ai.domino.manager.function.FunctionManager;
import ge.ai.domino.manager.game.ai.minmax.CachedMinMax;
import ge.ai.domino.manager.opponentplay.OpponentPlaysManager;
import ge.ai.domino.manager.opponentplay.guess.NegativeBalancedGuessRateCounter;
import ge.ai.domino.manager.replaygame.ReplayGameManager;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import ge.ai.domino.math.optimization.OptimizationDirection;
import ge.ai.domino.math.optimization.unimodal.multipleparams.ParamInterval;
import ge.ai.domino.math.optimization.unimodal.multipleparams.UnimodalOptimizationWithMultipleParams;
import ge.ai.domino.math.optimization.unimodal.oneparam.UnimodalOptimizationType;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class OpponentTilesPredictionOptimizationOperation implements GameDebuggerOperation {

	private static final Logger logger = Logger.getLogger(OpponentTilesPredictionOptimizationOperation.class);

	private static final ReplayGameManager replayGameManager = new ReplayGameManager();

	private static final OpponentPlaysManager opponentPlaysManager = new OpponentPlaysManager();

	private static final SystemParameterManager sysParamManager = new SystemParameterManager();

	@Override
	public void process(Scanner scanner) throws DAIException {
		List<Integer> idsForProcess = GameDebuggerHelper.getIdsForProcess(scanner);

		String functionName = "opponentPlayHeuristicsDiffsFunction_initialForOptimization";
		sysParamManager.changeParameterOnlyInCache("opponentPlayHeuristicsDiffsFunctionName", functionName);

		FunctionDAO functionDAO = new FunctionDAOImpl();
		FunctionManager functionManager = new FunctionManager();
		Map<String, FunctionArgsAndValues> functionArgsAndValuesMap = functionDAO.getFunctionArgsAndValues(functionName);
		FunctionArgsAndValues functionArgsAndValues = functionArgsAndValuesMap.get(functionName);
		Collections.reverse(functionArgsAndValues.getArgs());
		Collections.reverse(functionArgsAndValues.getValues());

		logger.info("Games amount:");
		Integer gamesAmount = Integer.parseInt(scanner.nextLine());
		logger.info("Optimization iteration:");
		int optimizationIteration = Integer.parseInt(scanner.nextLine());
		logger.info("Optimization inner iteration:");
		Integer optimizationInnerIteration = Integer.parseInt(scanner.nextLine());

		UnimodalOptimizationWithMultipleParams unimodalOptimizationWithMultipleParams =
				new UnimodalOptimizationWithMultipleParams(UnimodalOptimizationType.INTERVAL_DIVISION, OptimizationDirection.MAX) {
					@Override
					public double getValue(List<Double> params) {
						functionArgsAndValues.setValues(params);
						functionArgsAndValuesMap.put(functionName, functionArgsAndValues);
						functionManager.setFunctions(functionArgsAndValuesMap, false);

						return getAverageGuess(idsForProcess.subList(0, gamesAmount), NegativeBalancedGuessRateCounter.class.getSimpleName(), params);
					}
				};

		for (int i = 1; i <= optimizationIteration; i++) {
			logger.info("Starting MinMaxOpponentTilesPredictor optimization iteration[" + i + "]");

			Collections.shuffle(idsForProcess);

			List<Double> newValues = unimodalOptimizationWithMultipleParams.getExtremaVector(functionArgsAndValues.getValues(),
					getParamIntervals(functionArgsAndValues.getValues()), optimizationInnerIteration);
			functionArgsAndValues.setValues(newValues);
			logger.info("Finished MinMaxOpponentTilesPredictor optimization iteration[" + i + "]");
			logger.info("New values: " + newValues);
			try {
				Thread.sleep(5 * 60 * 1000);
			} catch (InterruptedException ex) {
				logger.error(ex);
			}
		}
	}

	private static double getAverageGuess(List<Integer> idsForProcess, String guessRateCounterClassName, List<Double> params) {
		List<OpponentPlay> fullOpponentPlays = new ArrayList<>();
		for (int id : idsForProcess) {
			logger.info("Starting game for replay id[" + id + "]");

			int gameId = 0;
			try {
				ReplayMoveInfo replayMoveInfo = replayGameManager.startReplayGame(id);
				gameId = replayMoveInfo.getGameId();

				while (replayMoveInfo.getNextMove() != null) {
					replayMoveInfo = replayGameManager.replayMove(replayMoveInfo.getGameId(), replayMoveInfo.getMoveIndex());
				}

				fullOpponentPlays.addAll(GameDebuggerHelper.removeExtraPlays(CachedGames.getOpponentPlays(replayMoveInfo.getGameId())));
			} catch (Exception ex) {
				logger.error("Error occurred while replay game id[" + id + "]", ex);
			} finally {
				CachedGames.removeGame(gameId);
				CachedGames.removeCreatedGameHistory(gameId);
				CachedMinMax.cleanUp(gameId);
			}

			logger.info("Finished game for replay id[" + id + "]");
		}

		GroupedOpponentPlay groupedOpponentPlay = opponentPlaysManager.getGroupedOpponentPlays(fullOpponentPlays, false, false, true).get(0);
		logger.info("Opponent plays grouped in one result for params: " + params);
		logger.info(groupedOpponentPlay.getAverageGuess());
		return groupedOpponentPlay.getAverageGuess().get(guessRateCounterClassName);
	}

	private static List<ParamInterval> getParamIntervals(List<Double> params) {
		List<ParamInterval> paramIntervals = new ArrayList<>();
		for (int i = 0; i < params.size(); i++) {
			ParamInterval paramInterval = new ParamInterval(i == 0 ? 0 : params.get(i - 1) + 0.000001,
					i == params.size() - 1 ? 1 : params.get(i + 1) - 0.000001);
			paramIntervals.add(paramInterval);
		}
		return paramIntervals;
	}
}
