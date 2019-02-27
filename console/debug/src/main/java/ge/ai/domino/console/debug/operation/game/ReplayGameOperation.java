package ge.ai.domino.console.debug.operation.game;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.console.debug.GameDebuggerHelper;
import ge.ai.domino.console.debug.operation.GameDebuggerOperation;
import ge.ai.domino.domain.game.opponentplay.GroupedOpponentPlay;
import ge.ai.domino.domain.game.opponentplay.OpponentPlay;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.played.ReplayMoveInfo;
import ge.ai.domino.manager.function.FunctionManager;
import ge.ai.domino.manager.game.ai.minmax.CachedMinMax;
import ge.ai.domino.manager.opponentplay.OpponentPlaysManager;
import ge.ai.domino.manager.replaygame.ReplayGameManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class ReplayGameOperation implements GameDebuggerOperation {

	private static final Logger logger = Logger.getLogger(ReplayGameOperation.class);

	private static final FunctionManager functionManager = new FunctionManager();

	private static final ReplayGameManager replayGameManager = new ReplayGameManager();

	private static final OpponentPlaysManager opponentPlaysManager = new OpponentPlaysManager();

	@Override
	public void process(Scanner scanner) {
		List<Integer> idsForProcess = GameDebuggerHelper.getIdsForProcess(scanner);

		functionManager.initFunctions();

		Map<Integer, List<GroupedOpponentPlay>> groupOpponentPlaysMap = new TreeMap<>();
		List<OpponentPlay> fullOpponentPlays = new ArrayList<>();

		for (int id : idsForProcess) {
			logger.info("Starting game for replay id[" + id + "]");

			int gameId = 0;
			try {
				ReplayMoveInfo replayMoveInfo = replayGameManager.startReplayGame(id);
				gameId = replayMoveInfo.getGameId();

				long ms = System.currentTimeMillis();
				while (replayMoveInfo.getNextMove() != null) {
					replayMoveInfo = replayGameManager.replayMove(replayMoveInfo.getGameId(), replayMoveInfo.getMoveIndex());
					if (replayMoveInfo.getNextMove() != null) {
						Move nextMove = new Move(replayMoveInfo.getNextMove().getLeft(), replayMoveInfo.getNextMove().getRight(), replayMoveInfo.getNextMove().getDirection());
						if (replayMoveInfo.getBestAiPrediction() != null && !replayMoveInfo.getBestAiPrediction().equals(nextMove)) {
							logger.warn("Next move and best prediction is not same");
							logger.info("Next move: " + nextMove);
							logger.info("Best prediction: " + replayMoveInfo.getBestAiPrediction());
							replayMoveInfo.getAiPredictions().forEach(logger::info);
						}
					}
				}
				logger.info(String.format("Replaying moves took %s ms", (System.currentTimeMillis() - ms)));

				List<OpponentPlay> opponentPlays = GameDebuggerHelper.removeExtraPlays(CachedGames.getOpponentPlays(replayMoveInfo.getGameId()));
				List<GroupedOpponentPlay> groupedOpponentPlays = opponentPlaysManager.getGroupedOpponentPlays(opponentPlays, true, false, false);
				groupOpponentPlaysMap.put(id, groupedOpponentPlays);

				fullOpponentPlays.addAll(opponentPlays);

				logger.info("GroupedOpponentPlay info for replayed game, id[" + replayMoveInfo.getGameId() + "], replayedGameId[" + id + "]");
				for (GroupedOpponentPlay groupedOpponentPlay : groupedOpponentPlays) {
					logger.info(groupedOpponentPlay.getAverageGuess());
				}
			} catch (Exception ex) {
				logger.error("Error occurred while replay game id[" + id + "]", ex);
			} finally {
				CachedGames.removeGame(gameId);
				CachedGames.removeCreatedGameHistory(gameId);
				CachedMinMax.cleanUp(gameId);
			}

			logger.info("Finished game for replay id[" + id + "]");
		}

		for (Map.Entry<Integer, List<GroupedOpponentPlay>> entry : groupOpponentPlaysMap.entrySet()) {
			logger.info("GroupedOpponentPlay info for replayed game, id[" + entry.getValue().get(0).getGameId() + "], replayedGameId[" + entry.getKey() + "]");
			for (GroupedOpponentPlay groupedOpponentPlay : entry.getValue()) {
				logger.info(groupedOpponentPlay.getAverageGuess());
			}
		}
		GroupedOpponentPlay groupedOpponentPlay = opponentPlaysManager.getGroupedOpponentPlays(fullOpponentPlays, false, false, true).get(0);
		logger.info("Opponent plays grouped in one result");
		logger.info(groupedOpponentPlay.getAverageGuess());
	}
}
