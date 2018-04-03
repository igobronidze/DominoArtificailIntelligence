package ge.ai.domino.server.manager.game.ai.minmax;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.AiPrediction;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.played.PlayedTile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.server.caching.game.CachedGames;
import ge.ai.domino.server.manager.game.ai.AiSolver;
import ge.ai.domino.server.manager.game.helper.ComparisonHelper;
import ge.ai.domino.server.manager.game.helper.MoveHelper;
import ge.ai.domino.server.manager.game.helper.ProbabilitiesDistributor;
import ge.ai.domino.server.manager.game.heuristic.ComplexRoundHeuristic;
import ge.ai.domino.server.manager.game.heuristic.RoundHeuristic;
import ge.ai.domino.server.manager.game.heuristic.RoundHeuristicHelper;
import ge.ai.domino.server.manager.game.logging.GameLoggingProcessor;
import ge.ai.domino.server.manager.game.move.AddForMeProcessor;
import ge.ai.domino.server.manager.game.move.AddForOpponentProcessor;
import ge.ai.domino.server.manager.game.move.MoveProcessor;
import ge.ai.domino.server.manager.game.move.PlayForMeProcessor;
import ge.ai.domino.server.manager.game.move.PlayForOpponentProcessor;
import ge.ai.domino.server.manager.sysparam.SystemParameterManager;
import ge.ai.domino.serverutil.CloneUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public class MinMax implements AiSolver {

	private static Logger logger = Logger.getLogger(MinMax.class);

	private final SystemParameterManager systemParameterManager = new SystemParameterManager();

	private final RoundHeuristic roundHeuristic = new ComplexRoundHeuristic();

	private static final SysParam checkOpponentProbabilities = new SysParam("checkOpponentProbabilities", "false");

	private final SysParam minMaxTreeHeight = new SysParam("minMaxTreeHeight", "8");

	private final MoveProcessor playForMeProcessor = new PlayForMeProcessor();

	private final MoveProcessor playForOpponentProcessor = new PlayForOpponentProcessor();

	private final MoveProcessor addForMeProcessor = new AddForMeProcessor();

	private final MoveProcessor addForOpponentProcessor = new AddForOpponentProcessor();

	private int treeHeight;

	private int recursionCount;

	private int gameId;

	private NodeRound notValidRound;

	private String errorMsg;

	@Override
	public List<AiPrediction> solve(Round round) throws DAIException {
		NodeRound nodeRound = new NodeRound();
		nodeRound.setRound(round);
		return minMax(nodeRound);
	}

	private List<AiPrediction> minMax(NodeRound nodeRound) throws DAIException {
		long ms = System.currentTimeMillis();
		this.gameId = nodeRound.getRound().getGameInfo().getGameId();
		treeHeight = systemParameterManager.getIntegerParameterValue(minMaxTreeHeight);
		List<Move> moves = getPossibleMoves(nodeRound.getRound());
		AiPrediction bestAiPrediction = null;
		NodeRound bestNodeRound = null;
		List<AiPrediction> aiPredictions = new ArrayList<>();
		logger.info("Ai predictions:");
		for (Move move : moves) {
			Round nextRound = playForMeProcessor.move(CloneUtil.getClone(nodeRound.getRound()), move, true);
			NodeRound nextNodeRound = new NodeRound();
			nextNodeRound.setRound(nextRound);
			nextNodeRound.setLastPlayedMove(MoveHelper.getPlayForMeMove(move));
			nextNodeRound.setParent(nodeRound);
			nextNodeRound.setTreeHeight(1);
			nodeRound.getChildren().add(nextNodeRound);
			validateOpponentTiles(nextNodeRound, "playForMe");
			float heuristic = getHeuristicValue(nextNodeRound, 2);
			AiPrediction aiPrediction = new AiPrediction();
			aiPrediction.setMove(move);
			aiPrediction.setHeuristicValue(heuristic);
			aiPredictions.add(aiPrediction);
			if (bestAiPrediction == null || bestAiPrediction.getHeuristicValue() < aiPrediction.getHeuristicValue()) {
				if (bestAiPrediction != null) {
					bestAiPrediction.setBestMove(false);
				}
				bestAiPrediction = aiPrediction;
				bestAiPrediction.setBestMove(true);
				if (bestNodeRound != null) {
					bestNodeRound.setLastPlayedProbability(0.0F);
				}
				bestNodeRound = nextNodeRound;
				bestNodeRound.setLastPlayedProbability(1.0F);
			}
			logger.info("PlayedMove- " + move.getLeft() + ":" + move.getRight() + " " + move.getDirection() + ", heuristic: " + heuristic);
		}
		float tookMs = System.currentTimeMillis() - ms;
		logger.info("MinMax took " + tookMs + "ms, recursion count " + recursionCount + ", average " + (tookMs / recursionCount));
		recursionCount = 0;
		if (aiPredictions.isEmpty() || bestAiPrediction == null) {
			logger.info("No AIPrediction");
			return null;
		}
		nodeRound.setHeuristic(bestAiPrediction.getHeuristicValue());
		logger.info("AIPrediction is [" + bestAiPrediction.getMove().getLeft() + "-" + bestAiPrediction.getMove().getRight() + " " +
				bestAiPrediction.getMove().getDirection().name() + "], " + "heuristic: " + bestAiPrediction.getHeuristicValue());

		applyValidation();
		return aiPredictions;
	}

	private float getHeuristicValue(NodeRound nodeRound, int height) throws DAIException {
		recursionCount++;
		Round round = nodeRound.getRound();
		TableInfo tableInfo = round.getTableInfo();
		GameInfo gameInfo = round.getGameInfo();

		// Recursion end conditions: Game is finished, Started new round, reached tree root
		if (round.getGameInfo().isFinished()) {
			return RoundHeuristicHelper.getFinishedGameHeuristic(gameInfo, CachedGames.getGameProperties(gameId).getPointsForWin());
		}
		if (isNewRound(round)) {
			nodeRound.setHeuristic(RoundHeuristicHelper.getFinishedRoundHeuristic(gameInfo, !tableInfo.isMyMove()));
			return nodeRound.getHeuristic();
		}
		if (height == treeHeight) {
			nodeRound.setHeuristic(roundHeuristic.getHeuristic(round));
			return nodeRound.getHeuristic();
		}

		List<Move> moves = getPossibleMoves(round);
		if (round.getTableInfo().isMyMove()) {
			// Best move for me
			NodeRound bestNodeRound = null;
			for (Move move : moves) {
				Round nextRound = playForMeProcessor.move(CloneUtil.getClone(round), move, true);
				NodeRound nextNodeRound = new NodeRound();
				nextNodeRound.setRound(nextRound);
				nextNodeRound.setLastPlayedMove(MoveHelper.getPlayForMeMove(move));
				nextNodeRound.setParent(nodeRound);
				nextNodeRound.setTreeHeight(height);
				nodeRound.getChildren().add(nextNodeRound);
				validateOpponentTiles(nextNodeRound, "playForMe");
				getHeuristicValue(nextNodeRound, height + 1);
				if (bestNodeRound == null || bestNodeRound.getHeuristic() > bestNodeRound.getHeuristic()) {
					if (bestNodeRound != null) {
						bestNodeRound.setLastPlayedProbability(0.0F);
					}
					bestNodeRound = nextNodeRound;
					bestNodeRound.setLastPlayedProbability(1.0F);
				}
			}
			// If there are no available move, use bazaar tiles
			if (bestNodeRound == null) {
				float heuristic = 0.0F;
				float bazaarProbSum = round.getTableInfo().getBazaarTilesCount();
				for (Map.Entry<Tile, Float> entry : round.getOpponentTiles().entrySet()) {
					Tile tile = entry.getKey();
					float prob = entry.getValue();
					if (prob != 1.0) {
						float probForPickTile = (1 - prob) / bazaarProbSum; // Probability fot choose this tile
						Move move = new Move(tile.getLeft(), tile.getRight(), MoveDirection.LEFT);
						Round nextRound = addForMeProcessor.move(CloneUtil.getClone(round), move, true);
						NodeRound nextNodeRound = new NodeRound();
						nextNodeRound.setRound(nextRound);
						nextNodeRound.setLastPlayedMove(MoveHelper.getAddTileForMeMove(move));
						nextNodeRound.setParent(nodeRound);
						nextNodeRound.setTreeHeight(height);
						nextNodeRound.setLastPlayedProbability(probForPickTile);
						nodeRound.getChildren().add(nextNodeRound);
						validateOpponentTiles(nextNodeRound, "addForMe");
						heuristic += getHeuristicValue(nextNodeRound, height + 1) * probForPickTile;
					}
				}
				nodeRound.setHeuristic(heuristic);
				return heuristic;
			} else {
				nodeRound.setHeuristic(bestNodeRound.getHeuristic());
				return nodeRound.getHeuristic();
			}
		} else {
			// Possible moves sorted ASC
			Queue<NodeRound> possibleRounds = new PriorityQueue<>((o1, o2) -> Float.compare(o1.getHeuristic(), o2.getHeuristic()));
			// Play all possible move and add in queue
			Map<Tile, Float> opponentTilesClone = CloneUtil.getClone(round.getOpponentTiles());
			for (Move move : moves) {
				Round nextRound = playForOpponentProcessor.move(CloneUtil.getClone(round), move, true);
				NodeRound nextNodeRound = new NodeRound();
				nextNodeRound.setRound(nextRound);
				nextNodeRound.setLastPlayedMove(MoveHelper.getPlayForOpponentMove(move));
				nextNodeRound.setParent(nodeRound);
				nextNodeRound.setTreeHeight(height);
				nodeRound.getChildren().add(nextNodeRound);
				validateOpponentTiles(nextNodeRound, "playForOpponent");
				getHeuristicValue(nextNodeRound, height + 1);
				possibleRounds.add(nextNodeRound);
			}

			float heuristic = 0.0F;
			float remainingProbability = 1.0F;
			for (NodeRound nextNodeRound : possibleRounds) {
				if (ComparisonHelper.equal(remainingProbability, 0.0F)) {
					break;
				}
				float prob = remainingProbability * opponentTilesClone.get(new Tile(nextNodeRound.getLastPlayedMove().getLeft(), nextNodeRound.getLastPlayedMove().getRight()));
				heuristic += nextNodeRound.getHeuristic() * prob;
				nextNodeRound.setLastPlayedProbability(prob);
				ProbabilitiesDistributor.distributeProbabilitiesOpponentProportional(opponentTilesClone, prob);
				remainingProbability -= prob;
			}

			// Bazaar case
			if (!ComparisonHelper.equal(remainingProbability, 0.0F)) {
				Round nextRound = addForOpponentProcessor.move(CloneUtil.getClone(round), null, true);
				NodeRound nextNodeRound = new NodeRound();
				nextNodeRound.setRound(nextRound);
				nextNodeRound.setLastPlayedMove(MoveHelper.getAddTileForOpponentMove());
				nextNodeRound.setParent(nodeRound);
				nextNodeRound.setTreeHeight(height);
				nextNodeRound.setLastPlayedProbability(remainingProbability);
				nodeRound.getChildren().add(nextNodeRound);
				validateOpponentTiles(nextNodeRound, "addForOpponent");
				heuristic += getHeuristicValue(nextNodeRound, height + 1) * remainingProbability;
			}
			nodeRound.setHeuristic(heuristic);
			return heuristic;
		}
	}

	private List<Move> getPossibleMoves(Round round) {
		List<Move> moves = new ArrayList<>();
		TableInfo tableInfo = round.getTableInfo();
		PlayedTile left = tableInfo.getLeft();
		PlayedTile right = tableInfo.getRight();
		PlayedTile top = tableInfo.getTop();
		PlayedTile bottom = tableInfo.getBottom();
		// First move
		if (tableInfo.getLeft() == null) {
			moves.addAll(round.getMyTiles().stream().map(tile -> new Move(tile.getLeft(), tile.getRight(), MoveDirection.LEFT)).collect(Collectors.toList()));
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
		if (!played.contains(hashForPlayedTile(left))) {
			if (left.getOpenSide() == tile.getLeft() || left.getOpenSide() == tile.getRight()) {
				moves.add(new Move(tile.getLeft(), tile.getRight(), MoveDirection.LEFT));
				played.add(hashForPlayedTile(left));
			}
		}
		if (!played.contains(hashForPlayedTile(right))) {
			if (right.getOpenSide() == tile.getLeft() || right.getOpenSide() == tile.getRight()) {
				moves.add(new Move(tile.getLeft(), tile.getRight(), MoveDirection.RIGHT));
				played.add(hashForPlayedTile(right));
			}
		}
		if (top != null && !played.contains(hashForPlayedTile(top))) {
			if ((top.getOpenSide() == tile.getLeft() || top.getOpenSide() == tile.getRight()) && !left.isCenter() && !right.isCenter()) {
				moves.add(new Move(tile.getLeft(), tile.getRight(), MoveDirection.TOP));
				played.add(hashForPlayedTile(top));
			}
		}
		if (bottom != null && !played.contains(hashForPlayedTile(bottom))) {
			if ((bottom.getOpenSide() == tile.getLeft() || bottom.getOpenSide() == tile.getRight()) && !left.isCenter() && !right.isCenter()) {
				moves.add(new Move(tile.getLeft(), tile.getRight(), MoveDirection.BOTTOM));
				played.add(hashForPlayedTile(bottom));
			}
		}
	}

	private int hashForPlayedTile(PlayedTile playedTile) {
		int p = 10;
		return (playedTile.getOpenSide() + 1) * (playedTile.isTwin() ? p : 1);
	}

	private boolean isNewRound(Round round) {
		return round.getMyTiles().size() == 0 && round.getTableInfo().getOpponentTilesCount() == 7 && round.getTableInfo().getBazaarTilesCount() == 21;
	}

	private void validateOpponentTiles(NodeRound nodeRound, String msg) {
		if (systemParameterManager.getBooleanParameterValue(checkOpponentProbabilities)) {
			float sum = 0.0F;
			Round round = nodeRound.getRound();
			for (Map.Entry<Tile, Float> entry : round.getOpponentTiles().entrySet()) {
				float prob = entry.getValue();
				int left = entry.getKey().getLeft();
				int right = entry.getKey().getRight();
				if (prob > 1.0F) {
					notValidRound = nodeRound;
					errorMsg = "Opponent tile probability is more than one, tile[" + left + "-" + right + "] method[" + msg + "]";
					break;
				} else if (prob < 0.0) {
					notValidRound = nodeRound;
					errorMsg = "Opponent tile probability is less than zero, tile[" + left + "-" + right + "] method[" + msg + "]";
				}
				sum += prob;
			}
			if (!ComparisonHelper.equal(sum, round.getTableInfo().getOpponentTilesCount())) {
				notValidRound = nodeRound;
				errorMsg = "Opponent tile count and probabilities sum is not same... count:" + round.getTableInfo().getOpponentTilesCount() + "  sum:" + sum + ", method[" + msg + "]";
			}
		}
	}

	private void applyValidation() throws DAIException {
		if (notValidRound != null) {
			logger.info(System.lineSeparator() + "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
			logger.warn(errorMsg);
			logger.info("Rounds full info");

			List<NodeRound> parentRounds = new ArrayList<>();
			while (notValidRound != null) {
				parentRounds.add(notValidRound);
				notValidRound = notValidRound.getParent();
			}
			for (int i = parentRounds.size() - 1; i > 0; i--) {
				notValidRound = parentRounds.get(i);
				logger.info("Height: " + notValidRound.getTreeHeight());
				GameLoggingProcessor.logRoundFullInfo(notValidRound.getRound(), false); // Still print if virtual
				logger.info("Play move with probability[" + notValidRound.getLastPlayedProbability() + "], move[" + notValidRound.getLastPlayedMove() + "]");
			}
			GameLoggingProcessor.logRoundFullInfo(parentRounds.get(0).getRound(), false);  // Last(notValid) round

			logger.info(System.lineSeparator() + "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
			throw new DAIException("probabilitiesSumIsNoEqualToOpponentTilesCount");
		}
	}
}
