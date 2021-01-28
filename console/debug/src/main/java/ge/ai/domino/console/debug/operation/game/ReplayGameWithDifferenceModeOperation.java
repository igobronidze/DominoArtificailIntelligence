package ge.ai.domino.console.debug.operation.game;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.console.debug.GameDebuggerHelper;
import ge.ai.domino.console.debug.operation.GameDebuggerOperation;
import ge.ai.domino.dao.script.ScriptExecutorImpl;
import ge.ai.domino.domain.game.ai.AiPrediction;
import ge.ai.domino.domain.game.opponentplay.GroupedOpponentPlay;
import ge.ai.domino.domain.game.opponentplay.OpponentPlay;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.played.ReplayMoveInfo;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.function.FunctionManager;
import ge.ai.domino.manager.game.ai.minmax.CachedMinMax;
import ge.ai.domino.manager.game.logging.RoundLogger;
import ge.ai.domino.manager.multiprocessorserver.MultiProcessorServer;
import ge.ai.domino.manager.opponentplay.OpponentPlaysManager;
import ge.ai.domino.manager.replaygame.ReplayGameManager;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

public class ReplayGameWithDifferenceModeOperation implements GameDebuggerOperation {

	private Logger logger = Logger.getLogger(ScriptExecutorImpl.class);

	private static final FunctionManager functionManager = new FunctionManager();

	private static final SystemParameterManager sysParamManager = new SystemParameterManager();

	private static final ReplayGameManager replayGameManager = new ReplayGameManager();

	private static final MultiProcessorServer multiProcessorServer = MultiProcessorServer.getInstance();

	private static final List<String> MODE_FILE_NAMES = Arrays.asList("/mode/without_predictor_mode.properties",
			"/mode/min_max_predictor_mode.properties",
			"/mode/one_move_heuristic_predictor_mode.properties",
			"/mode/one_move_heuristic_predictor_mode_2.properties");

	private final SysParam useMultiProcessorMinMax = new SysParam("useMultiProcessorMinMax", "true");

	private static final OpponentPlaysManager opponentPlaysManager = new OpponentPlaysManager();

	@Override
	public void process(Scanner scanner) {
		List<Integer> idsForProcess = GameDebuggerHelper.getIdsForProcess(scanner);
		functionManager.initFunctions();

		Map<String, List<OpponentPlay>> fullOpponentPlaysMap = new HashMap<>();
		for (String modeFile : MODE_FILE_NAMES) {
			fullOpponentPlaysMap.put(modeFile, new ArrayList<>());
		}
		for (int id : idsForProcess) {
			int count = 0;
			Map<String, List<ReplayMoveInfo>> replayMoveInfoMap = new HashMap<>();

			for (String modeFile : MODE_FILE_NAMES) {
				logger.info("Starting game for replay id[" + id + "], mode[" + modeFile + "]");
				updateSysParams(modeFile);

				replayMoveInfoMap.put(modeFile, new ArrayList<>());
				int gameId = 0;
				count = 0;

				try {
					ReplayMoveInfo replayMoveInfo = replayGameManager.startReplayGame(id);
					gameId = replayMoveInfo.getGameId();
					while (replayMoveInfo.getNextMove() != null) {
						replayMoveInfo.setRound(CachedGames.getCurrentRound(gameId, true));
						replayMoveInfo = replayGameManager.replayMove(replayMoveInfo.getGameId(), replayMoveInfo.getMoveIndex());
						if (replayMoveInfo.getNextMove() != null && replayMoveInfo.getBestAiPrediction() != null) {
							replayMoveInfoMap.get(modeFile).add(replayMoveInfo);
							count++;
						}
					}

					List<OpponentPlay> opponentPlays = GameDebuggerHelper.removeExtraPlays(CachedGames.getOpponentPlays(replayMoveInfo.getGameId()));

					fullOpponentPlaysMap.get(modeFile).addAll(opponentPlays);
				} catch (Exception ex) {
					logger.error("Error occurred while replay game id[" + id + "], mode[" + modeFile + "]", ex);
				} finally {
					CachedGames.removeGame(gameId);
					CachedGames.removeCreatedGameHistory(gameId);
					CachedMinMax.cleanUp(gameId);
				}

				logger.info("Finished game for replay id[" + id + "], mode[" + modeFile + "]");
			}

			for (int i = 0; i < count; i++) {
				logger.info("Move info index[" + i + "]");

				boolean first = true;
				boolean samePrediction = true;
				Move bestMove = null;
				for (String mode : MODE_FILE_NAMES) {
					ReplayMoveInfo replayMoveInfo = replayMoveInfoMap.get(mode).get(i);
					if (first) {
						RoundLogger.logRoundFullInfo(replayMoveInfo.getRound());
						first = false;
					}
					logger.info("Mode: " + mode);
					replayMoveInfo.getAiPredictions().sort(Comparator.comparingDouble(AiPrediction::getHeuristicValue));
					replayMoveInfo.getAiPredictions().forEach(logger::info);
					logger.info("Best prediction: " + replayMoveInfo.getBestAiPrediction());
					logger.info("Round heuristic: " + replayMoveInfo.getHeuristicValue());

					if (bestMove != null && !bestMove.equals(replayMoveInfo.getBestAiPrediction())) {
						samePrediction = false;
					}
					bestMove = replayMoveInfo.getBestAiPrediction();
				}
				if (!samePrediction) {
					logger.info("All best prediction is not same");
				}
			}

			for (String moveFile : MODE_FILE_NAMES) {
				GroupedOpponentPlay groupedOpponentPlay = opponentPlaysManager.getGroupedOpponentPlays(fullOpponentPlaysMap.get(moveFile), false, false, true).get(0);
				logger.info("Opponent plays grouped in one result, Mode: " + moveFile);
				logger.info(groupedOpponentPlay.getAverageGuess());
			}
		}
	}

	private void updateSysParams(String filePath) {
		try {
			Properties props = new Properties();
			props.load(new FileInputStream(this.getClass().getResource(filePath).getFile()));

			Map<String, String> params = new HashMap<>();
			for (Map.Entry<Object, Object> entry : props.entrySet()) {
				sysParamManager.changeParameterOnlyInCache((String) entry.getKey(), (String) entry.getValue());
				params.put((String) entry.getKey(), (String) entry.getValue());
			}

			if (sysParamManager.getBooleanParameterValue(useMultiProcessorMinMax)) {
				multiProcessorServer.updateSysParams(params);
			}
		} catch (IOException ex) {
			logger.error("Error occurred while read property file[" + filePath + "]", ex);
		}
	}
}
