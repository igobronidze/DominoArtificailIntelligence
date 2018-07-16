package ge.ai.domino.manager.game.ai.minmax;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.played.PlayedTile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.game.ai.AiSolver;
import ge.ai.domino.manager.game.helper.ComparisonHelper;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import ge.ai.domino.manager.game.logging.GameLoggingProcessor;
import ge.ai.domino.serverutil.TileAndMoveHelper;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class MinMax implements AiSolver {

	private final Logger logger = Logger.getLogger(super.getClass());

	private final SystemParameterManager systemParameterManager = new SystemParameterManager();

	private final SysParam checkOpponentProbabilities = new SysParam("checkOpponentProbabilities", "false");

	protected final SysParam useMinMaxPredictor = new SysParam("useMinMaxPredictor", "false");

	protected static final SysParam bestMoveAutoPlay = new SysParam("bestMoveAutoPlay", "true");

	protected static final SysParam roundHeuristicType = new SysParam("roundHeuristicType", "POINT_DIFF_ROUND_HEURISTIC");

	private NodeRound notValidRound;

	private String errorMsg;

	private String errorMsgKey;

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
					addPossibleMovesForTile(tile, left, right, top, bottom, moves);
				}
			} else {
				round.getOpponentTiles().entrySet().stream().filter(entry -> entry.getValue() > 0.0).forEach(
						entry -> addPossibleMovesForTile(entry.getKey(), left, right, top, bottom, moves));
			}
		}
		return moves;
	}

	private void addPossibleMovesForTile(Tile tile, PlayedTile left, PlayedTile right, PlayedTile top, PlayedTile bottom, List<Move> moves) {
		Set<Integer> played = new HashSet<>();
		// LEFT RIGHT TOP BOTTOM sequence is important
		if (!played.contains(TileAndMoveHelper.hashForPlayedTile(left))) {
			if (left.getOpenSide() == tile.getLeft() || left.getOpenSide() == tile.getRight()) {
				moves.add(TileAndMoveHelper.getMove(tile, MoveDirection.LEFT));
				played.add(TileAndMoveHelper.hashForPlayedTile(left));
			}
		}
		if (!played.contains(TileAndMoveHelper.hashForPlayedTile(right))) {
			if (right.getOpenSide() == tile.getLeft() || right.getOpenSide() == tile.getRight()) {
				moves.add(TileAndMoveHelper.getMove(tile, MoveDirection.RIGHT));
				played.add(TileAndMoveHelper.hashForPlayedTile(right));
			}
		}
		if (top != null && !played.contains(TileAndMoveHelper.hashForPlayedTile(top))) {
			if ((top.getOpenSide() == tile.getLeft() || top.getOpenSide() == tile.getRight()) && !left.isCenter() && !right.isCenter()) {
				moves.add(TileAndMoveHelper.getMove(tile, MoveDirection.TOP));
				played.add(TileAndMoveHelper.hashForPlayedTile(top));
			}
		}
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
			GameLoggingProcessor.logRoundFullInfo(notValidRound.getRound(), false); // Still print if virtual
			for (int i = parentRounds.size() - 1; i >= 0; i--) {
				notValidRound = parentRounds.get(i);
				logger.info("ID: " + notValidRound.getId() + ", Height: " + notValidRound.getTreeHeight());
				logger.info("Play move with probability[" + notValidRound.getLastPlayedProbability() + "], move[" + notValidRound.getLastPlayedMove() + "]");
				GameLoggingProcessor.logRoundFullInfo(notValidRound.getRound(), false); // Still print if virtual
			}

			logger.info(System.lineSeparator() + "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
			return errorMsgKey;
		}
		return null;
	}
}
