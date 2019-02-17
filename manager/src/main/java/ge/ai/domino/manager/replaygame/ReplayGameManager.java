package ge.ai.domino.manager.replaygame;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Game;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.ai.AiPrediction;
import ge.ai.domino.domain.game.ai.AiPredictionsWrapper;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveType;
import ge.ai.domino.domain.played.GameHistory;
import ge.ai.domino.domain.played.PlayedMove;
import ge.ai.domino.domain.played.ReplayMoveInfo;
import ge.ai.domino.domain.played.SkipRoundInfo;
import ge.ai.domino.manager.game.GameManager;
import ge.ai.domino.manager.game.ai.predictor.OpponentTilesPredictorFactory;
import ge.ai.domino.manager.game.helper.initial.InitialUtil;
import ge.ai.domino.manager.heuristic.HeuristicManager;
import ge.ai.domino.manager.played.PlayedGameManager;

import java.util.ArrayList;
import java.util.List;

public class ReplayGameManager {

	private static final int MY_POINT_FOR_SKIP_ROUND = 50;

	private static final int OPPONENT_POINT_FOR_SKIP_ROUND = 50;

	private static final int LEFT_TILES_FOR_SKIP_ROUND = 0;

	private final PlayedGameManager playedGameManager = new PlayedGameManager();

	private final GameManager gameManager = new GameManager();

	private final HeuristicManager heuristicManager = new HeuristicManager();

	public ReplayMoveInfo startReplayGame(int gameId) throws DAIException {
		OpponentTilesPredictorFactory.getOpponentTilesPredictor(true);

		GameHistory gameHistory = playedGameManager.getGameHistory(gameId);
		GameProperties gameProperties = playedGameManager.getGameProperties(gameId);

		Game game = InitialUtil.getInitialGame(gameProperties, false);
		CachedGames.addGame(game);
		CachedGames.addCreatedGameHistory(game.getId(), gameHistory);

		gameManager.ifNeedSendInitialData(game, gameProperties);

		ReplayMoveInfo replayMoveInfo = new ReplayMoveInfo();
		replayMoveInfo.setGameId(game.getId());
		replayMoveInfo.setMoveIndex(1);
		List<PlayedMove> playedMoves = new ArrayList<>(gameHistory.getPlayedMoves());
		replayMoveInfo.setPreviousMove(playedMoves.get(0));
		replayMoveInfo.setNextMove(playedMoves.get(1));

		return replayMoveInfo;
	}

	public ReplayMoveInfo replayMove(int gameId, int moveIndex) throws DAIException {
		ReplayMoveInfo replayMoveInfo = new ReplayMoveInfo();
		replayMoveInfo.setGameId(gameId);

		GameHistory gameHistory = CachedGames.getCreatedGameHistory(gameId);
		List<PlayedMove> playedMoves = new ArrayList<>(gameHistory.getPlayedMoves());
		PlayedMove playedMove = playedMoves.get(moveIndex);
		replayMoveInfo.setPreviousMove(playedMove);

		if (playedMove.getType() == MoveType.ADD_INIT_TILE_FOR_ME) {
			replayMove(gameId, playedMove);
			if (playedMoves.get(moveIndex - 1).getType() == MoveType.START_NEW_ROUND) {
				for (int i = 1; i < 6; i++) {
					replayMove(gameId, playedMoves.get(moveIndex + i));
				}
				gameManager.specifyRoundBeginner(gameId, amINextRoundBeginner(playedMoves, moveIndex));
				replayMove(gameId, playedMoves.get(moveIndex + 6));
			} else {
				for (int i = 1; i < 7; i++) {
					replayMove(gameId, playedMoves.get(moveIndex + i));
				}
			}
			replayMoveInfo.setMoveIndex(moveIndex + 7);
		} else if (playedMove.getType() == MoveType.SKIP_ROUND) {
			SkipRoundInfo skipRoundInfo = playedMove.getSkipRoundInfo();
			if (skipRoundInfo == null) {
				skipRoundInfo = new SkipRoundInfo();
				skipRoundInfo.setMyPoint(MY_POINT_FOR_SKIP_ROUND);
				skipRoundInfo.setOpponentPoint(OPPONENT_POINT_FOR_SKIP_ROUND);
				skipRoundInfo.setLeftTiles(LEFT_TILES_FOR_SKIP_ROUND);
				skipRoundInfo.setStartMe(amINextRoundBeginner(playedMoves, moveIndex));
				skipRoundInfo.setFinishGame(moveIndex == playedMoves.size() - 1);

				playedMove.setSkipRoundInfo(skipRoundInfo);
			}
			replayMove(gameId, playedMove);

			replayMoveInfo.setMoveIndex(moveIndex + 1);
		} else {
			Round replayedRound = replayMove(gameId, playedMove);
			replayMoveInfo.setMoveIndex(moveIndex + 1);
			if (replayedRound.getAiPredictions() != null) {
				replayMoveInfo.setBestAiPrediction(getBestPrediction(replayedRound.getAiPredictions()));
				replayMoveInfo.setAiPredictions(replayedRound.getAiPredictions().getAiPredictions());
				replayMoveInfo.setHeuristicValue(heuristicManager.getHeuristic(replayedRound));
			}
		}

		if (replayMoveInfo.getMoveIndex() < playedMoves.size()) {
			replayMoveInfo.setNextMove(playedMoves.get(replayMoveInfo.getMoveIndex()));
		}

		return replayMoveInfo;
	}

	public ReplayMoveInfo undoReplayedMove(int gameId, int moveIndex) throws DAIException {
		gameManager.getLastPlayedRound(gameId);

		GameHistory gameHistory = CachedGames.getCreatedGameHistory(gameId);
		List<PlayedMove> playedMoves = new ArrayList<>(gameHistory.getPlayedMoves());

		ReplayMoveInfo replayMoveInfo = new ReplayMoveInfo();
		replayMoveInfo.setGameId(gameId);
		replayMoveInfo.setMoveIndex(moveIndex - 1);
		replayMoveInfo.setNextMove(playedMoves.get(replayMoveInfo.getMoveIndex()));
		replayMoveInfo.setPreviousMove(playedMoves.get(replayMoveInfo.getMoveIndex() - 1));
		return replayMoveInfo;
	}

	private boolean amINextRoundBeginner(List<PlayedMove> playedMoves, int moveIndex) {
		for (int i = moveIndex; i < playedMoves.size(); i++) {
			if (playedMoves.get(i).getType() == MoveType.PLAY_FOR_ME) {
				return true;
			}
			if (playedMoves.get(i).getType() == MoveType.PLAY_FOR_OPPONENT) {
				return false;
			}
		}
		return true;
	}

	private Round replayMove(int gameId, PlayedMove playedMove) throws DAIException {
		switch (playedMove.getType()) {
			case ADD_INIT_TILE_FOR_ME:
			case ADD_FOR_ME:
			case I_OMIT:
				return gameManager.addTileForMe(gameId, playedMove.getLeft(), playedMove.getRight());
			case ADD_FOR_OPPONENT:
			case OPPONENT_OMIT:
				return gameManager.addTileForOpponent(gameId);
			case PLAY_FOR_ME:
				return gameManager.playForMe(gameId, new Move(playedMove.getLeft(), playedMove.getRight(), playedMove.getDirection()));
			case PLAY_FOR_OPPONENT:
				return gameManager.playForOpponent(gameId, new Move(playedMove.getLeft(), playedMove.getRight(), playedMove.getDirection()));
			case SKIP_ROUND:
				SkipRoundInfo skipRoundInfo = playedMove.getSkipRoundInfo();
				return gameManager.skipRound(gameId, skipRoundInfo.getMyPoint(), skipRoundInfo.getOpponentPoint(), skipRoundInfo.getLeftTiles(),
						skipRoundInfo.isStartMe(), skipRoundInfo.isFinishGame());
			case START_NEW_ROUND:
			default:
				return CachedGames.getCurrentRound(gameId, false);
		}
	}

	private Move getBestPrediction(AiPredictionsWrapper aiPredictionsWrapper) {
		for (AiPrediction aiPrediction : aiPredictionsWrapper.getAiPredictions()) {
			if (aiPrediction.getMoveProbability() == 1.0) {
				return aiPrediction.getMove();
			}
		}
		return null;
	}
}
