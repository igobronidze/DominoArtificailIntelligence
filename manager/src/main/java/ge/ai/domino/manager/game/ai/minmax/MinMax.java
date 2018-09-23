package ge.ai.domino.manager.game.ai.minmax;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.played.PlayedTile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.game.ai.AiSolver;
import ge.ai.domino.manager.game.ai.heuristic.RoundHeuristic;
import ge.ai.domino.manager.game.helper.ComparisonHelper;
import ge.ai.domino.manager.game.logging.RoundLogger;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import ge.ai.domino.serverutil.TileAndMoveHelper;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class MinMax implements AiSolver {

	private final Logger logger = Logger.getLogger(super.getClass());

	private static final String MOVE_PRIORITY_KEY = "movePriority";

	private static final String MOVE_PRIORITY_DELIMITER = ",";

	private static final String MOVE_PRIORITY_DEFAULT_VALUE = "LEFT,RIGHT,TOP,BOTTOM";

	private final SystemParameterManager systemParameterManager = new SystemParameterManager();

	private final SysParam checkOpponentProbabilities = new SysParam("checkOpponentProbabilities", "false");

	private final SysParam logAboutRoundHeuristic = new SysParam("logAboutRoundHeuristic", "true");

	protected final SysParam useMinMaxPredictor = new SysParam("useMinMaxPredictor", "false");

	protected static final SysParam bestMoveAutoPlay = new SysParam("bestMoveAutoPlay", "true");

	protected static final SysParam roundHeuristicType = new SysParam("roundHeuristicType", "POINT_DIFF_ROUND_HEURISTIC");

	private NodeRound notValidRound;

	private String errorMsg;

	private String errorMsgKey;

	protected boolean multithreadingMinMax;

	protected int threadCount;

	public void setMultithreadingMinMax(boolean multithreadingMinMax) {
		this.multithreadingMinMax = multithreadingMinMax;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public abstract void minMaxForCachedNodeRound(Round round) throws DAIException;

	public abstract String getType();

	protected List<Move> getPossibleMoves(Round round) {
		List<Move> moves = new ArrayList<>();
		TableInfo tableInfo = round.getTableInfo();
		PlayedTile left = tableInfo.getLeft();
		PlayedTile right = tableInfo.getRight();
		PlayedTile top = tableInfo.getTop();
		PlayedTile bottom = tableInfo.getBottom();
		// First move
		if (tableInfo.getLeft() == null) {
			moves.addAll(round.getMyTiles().stream().map(tile -> TileAndMoveHelper.getMove(tile, MoveDirection.LEFT)).collect(Collectors.toList()));
		} else {
			if (round.getTableInfo().isMyMove()) {
				for (Tile tile : round.getMyTiles()) {
					addPossibleMovesForTile(round.getGameInfo().getGameId(), tile, left, right, top, bottom, moves);
				}
			} else {
				round.getOpponentTiles().entrySet().stream().filter(entry -> entry.getValue() > 0.0).forEach(
						entry -> addPossibleMovesForTile(round.getGameInfo().getGameId(), entry.getKey(), left, right, top, bottom, moves));
			}
		}
		return moves;
	}

	private void addPossibleMovesForTile(int gameId, Tile tile, PlayedTile left, PlayedTile right, PlayedTile top, PlayedTile bottom, List<Move> moves) {
		Set<Integer> played = new HashSet<>();

		for (MoveDirection moveDirection : getMovePriority(gameId)) {
			switch (moveDirection) {
				case LEFT:
					addLeftPossibleMove(tile, left, moves, played);
					break;
				case RIGHT:
					addRightPossibleMove(tile, right, moves, played);
					break;
				case TOP:
					addTopPossibleMove(tile, top, left, right, moves, played);
					break;
				case BOTTOM:
					addBottomPossibleMove(tile, bottom, left, right, moves,played);
					break;
			}
		}
	}

	private List<MoveDirection> getMovePriority(int gameId) {
		Map<String, String> params = CachedGames.getGameProperties(gameId).getChannel().getParams();
		String movePriority = params.containsKey(MOVE_PRIORITY_KEY) ? params.get(MOVE_PRIORITY_KEY) : MOVE_PRIORITY_DEFAULT_VALUE;

		List<MoveDirection> moveDirections = new ArrayList<>();
		for (String direction : movePriority.split(MOVE_PRIORITY_DELIMITER)) {
			moveDirections.add(MoveDirection.valueOf(direction));
		}
		return moveDirections;
	}

	private void addLeftPossibleMove(Tile tile, PlayedTile left, List<Move> moves, Set<Integer> played) {
		if (!played.contains(TileAndMoveHelper.hashForPlayedTile(left))) {
			if (left.getOpenSide() == tile.getLeft() || left.getOpenSide() == tile.getRight()) {
				moves.add(TileAndMoveHelper.getMove(tile, MoveDirection.LEFT));
				played.add(TileAndMoveHelper.hashForPlayedTile(left));
			}
		}
	}

	private void addRightPossibleMove(Tile tile, PlayedTile right, List<Move> moves, Set<Integer> played) {
		if (!played.contains(TileAndMoveHelper.hashForPlayedTile(right))) {
			if (right.getOpenSide() == tile.getLeft() || right.getOpenSide() == tile.getRight()) {
				moves.add(TileAndMoveHelper.getMove(tile, MoveDirection.RIGHT));
				played.add(TileAndMoveHelper.hashForPlayedTile(right));
			}
		}
	}

	private void addTopPossibleMove(Tile tile, PlayedTile top, PlayedTile left, PlayedTile right, List<Move> moves, Set<Integer> played) {
		if (top != null && !played.contains(TileAndMoveHelper.hashForPlayedTile(top))) {
			if ((top.getOpenSide() == tile.getLeft() || top.getOpenSide() == tile.getRight()) && !left.isCenter() && !right.isCenter()) {
				moves.add(TileAndMoveHelper.getMove(tile, MoveDirection.TOP));
				played.add(TileAndMoveHelper.hashForPlayedTile(top));
			}
		}
	}

	private void addBottomPossibleMove(Tile tile, PlayedTile bottom, PlayedTile left, PlayedTile right, List<Move> moves, Set<Integer> played) {
		if (bottom != null && !played.contains(TileAndMoveHelper.hashForPlayedTile(bottom))) {
			if ((bottom.getOpenSide() == tile.getLeft() || bottom.getOpenSide() == tile.getRight()) && !left.isCenter() && !right.isCenter()) {
				moves.add(TileAndMoveHelper.getMove(tile, MoveDirection.BOTTOM));
				played.add(TileAndMoveHelper.hashForPlayedTile(bottom));
			}
		}
	}

	protected boolean isNewRound(Round round) {
		return round.getMyTiles().size() == 0 && round.getTableInfo().getOpponentTilesCount() == 7 && round.getTableInfo().getBazaarTilesCount() == 21;
	}

	protected void validateOpponentTiles(NodeRound nodeRound, String msg) {
		if (systemParameterManager.getBooleanParameterValue(checkOpponentProbabilities)) {
			double sum = 0.0;
			Round round = nodeRound.getRound();
			for (Map.Entry<Tile, Double> entry : round.getOpponentTiles().entrySet()) {
				double prob = entry.getValue();
				int left = entry.getKey().getLeft();
				int right = entry.getKey().getRight();
				if (prob > 1.0) {
					notValidRound = nodeRound;
					errorMsg = "Opponent tile probability is more than one, tile[" + left + "-" + right + "] method[" + msg + "]";
					errorMsgKey = "opponentTileProbabilityIsMoreThanOne";
					break;
				} else if (prob < 0.0) {
					notValidRound = nodeRound;
					errorMsg = "Opponent tile probability is less than zero, tile[" + left + "-" + right + "] method[" + msg + "]";
					errorMsgKey = "opponentTileProbabilityIsLessThanZero";
				}
				sum += prob;
			}
			if (!ComparisonHelper.equal(sum, round.getTableInfo().getOpponentTilesCount())) {
				notValidRound = nodeRound;
				errorMsg = "Opponent tile count and probabilities sum is not same... count:" + round.getTableInfo().getOpponentTilesCount() + "  sum:" + sum + ", method[" + msg + "]";
				errorMsgKey = "probabilitiesSumIsNoEqualToOpponentTilesCount";
			}
		}
	}

	protected String applyValidation() {
		if (notValidRound != null) {
			logger.info(System.lineSeparator() + "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
			logger.warn(errorMsg);
			logger.info("Rounds full info");

			List<NodeRound> parentRounds = new ArrayList<>();
			while (notValidRound.getParent() != null) {
				parentRounds.add(notValidRound);
				notValidRound = notValidRound.getParent();
			}
			RoundLogger.logRoundFullInfo(notValidRound.getRound());
			for (int i = parentRounds.size() - 1; i >= 0; i--) {
				notValidRound = parentRounds.get(i);
				logger.info("ID: " + notValidRound.getId() + ", Height: " + notValidRound.getTreeHeight());
				logger.info("Play move with probability[" + notValidRound.getLastPlayedProbability() + "], move[" + notValidRound.getLastPlayedMove() + "]");
				RoundLogger.logRoundFullInfo(notValidRound.getRound());
			}

			logger.info(System.lineSeparator() + "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
			return errorMsgKey;
		}
		return null;
	}

	protected double getHeuristic(Round round, RoundHeuristic roundHeuristic) {
		Random random = new Random();
		double r = random.nextDouble();
		boolean logHeuristicInfo = (r < (1.0 / 1000)) && systemParameterManager.getBooleanParameterValue(logAboutRoundHeuristic);
		if (logHeuristicInfo) {
			logger.info("******************************RoundHeuristic(" + roundHeuristic.getClass().getSimpleName() + ")******************************");
			RoundLogger.logRoundFullInfo(round);
		}

		double heuristic = roundHeuristic.getHeuristic(round, logHeuristicInfo);

		if (logHeuristicInfo) {
			logger.info("Heuristic: " + heuristic);
			logger.info("************************************************************");
		}

		return heuristic;
	}
}
