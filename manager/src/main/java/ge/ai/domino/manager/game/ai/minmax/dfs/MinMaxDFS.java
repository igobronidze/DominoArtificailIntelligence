package ge.ai.domino.manager.game.ai.minmax.dfs;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.game.ai.AiPrediction;
import ge.ai.domino.domain.game.ai.AiPredictionsWrapper;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.game.ai.heuristic.RoundHeuristic;
import ge.ai.domino.manager.game.ai.heuristic.RoundHeuristicFactory;
import ge.ai.domino.manager.game.ai.heuristic.RoundHeuristicHelper;
import ge.ai.domino.manager.game.ai.minmax.CachedMinMax;
import ge.ai.domino.manager.game.ai.minmax.CachedPrediction;
import ge.ai.domino.manager.game.ai.minmax.MinMax;
import ge.ai.domino.manager.game.ai.minmax.NodeRound;
import ge.ai.domino.manager.game.ai.predictor.MinMaxPredictor;
import ge.ai.domino.manager.game.helper.ComparisonHelper;
import ge.ai.domino.manager.game.helper.game.GameOperations;
import ge.ai.domino.manager.game.helper.game.MoveHelper;
import ge.ai.domino.manager.game.helper.game.ProbabilitiesDistributor;
import ge.ai.domino.manager.game.move.AddForMeProcessorVirtual;
import ge.ai.domino.manager.game.move.AddForOpponentProcessorVirtual;
import ge.ai.domino.manager.game.move.MoveProcessor;
import ge.ai.domino.manager.game.move.PlayForMeProcessorVirtual;
import ge.ai.domino.manager.game.move.PlayForOpponentProcessorVirtual;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import ge.ai.domino.serverutil.CloneUtil;
import ge.ai.domino.serverutil.TileAndMoveHelper;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class MinMaxDFS extends MinMax {

	private Logger logger = Logger.getLogger(MinMaxDFS.class);

	private final SystemParameterManager systemParameterManager = new SystemParameterManager();

	private final SysParam minMaxTreeHeight = new SysParam("minMaxTreeHeight", "8");

	private final MoveProcessor playForMeProcessorVirtual = new PlayForMeProcessorVirtual();

	private final MoveProcessor playForOpponentProcessorVirtual = new PlayForOpponentProcessorVirtual();

	private final MoveProcessor addForMeProcessorVirtual = new AddForMeProcessorVirtual();

	private final MoveProcessor addForOpponentProcessorVirtual = new AddForOpponentProcessorVirtual();

	private int treeHeight;

	private int recursionCount;

	private int gameId;

	@Override
	public AiPredictionsWrapper solve(Round round) throws DAIException {
		this.gameId = round.getGameInfo().getGameId();
		logger.info("Executing MinMaxDFS gameId[" + gameId + "]");

		treeHeight = systemParameterManager.getIntegerParameterValue(minMaxTreeHeight);

		NodeRound nodeRound = new NodeRound();
		nodeRound.setRound(round);
		long ms = System.currentTimeMillis();
		List<Move> moves = GameOperations.getPossibleMoves(nodeRound.getRound(), false);
		logger.info("Ai predictions:");
		if (moves.isEmpty()) {
			logger.info("No AIPrediction");
			return null;
		}
		if (moves.size() == 1 && systemParameterManager.getBooleanParameterValue(bestMoveAutoPlay)) {
			if (systemParameterManager.getBooleanParameterValue(useMinMaxPredictor)) {
				new Thread(() -> {
					try {
						CachedMinMax.changeMinMaxInProgress(gameId, true);
						minMaxForMoves(moves, nodeRound, ms);
						if (new MinMaxPredictor().usePredictor()) {
							CachedMinMax.setCachedPrediction(nodeRound.getRound().getGameInfo().getGameId(), CachedPrediction.getCachedPrediction(nodeRound, 2), true);
						}
						CachedMinMax.changeMinMaxInProgress(gameId, false);
					} catch (DAIException ex) {
						logger.error(ex);
					}
				}).start();
			}
			AiPredictionsWrapper aiPredictionsWrapper = new AiPredictionsWrapper();
			AiPrediction aiPrediction = new AiPrediction();
			aiPrediction.setMove(moves.get(0));
			aiPrediction.setBestMove(true);
			aiPrediction.setHeuristicValue(Integer.MIN_VALUE);
			aiPredictionsWrapper.getAiPredictions().add(aiPrediction);
			return aiPredictionsWrapper;
		} else {
			AiPredictionsWrapper aiPredictionsWrapper = minMaxForMoves(moves, nodeRound, ms);
			if (new MinMaxPredictor().usePredictor()) {
				CachedMinMax.setCachedPrediction(nodeRound.getRound().getGameInfo().getGameId(), CachedPrediction.getCachedPrediction(nodeRound, 2), true);
			}
			return aiPredictionsWrapper;
		}
	}

	@Override
	public void minMaxForCachedNodeRound(Round round) throws DAIException {
		this.gameId = round.getGameInfo().getGameId();
		logger.info("Executing MinMaxDFSForCachedNodeRound gameId[" + gameId + "]");
		long ms = System.currentTimeMillis();

		treeHeight = systemParameterManager.getIntegerParameterValue(minMaxTreeHeight);

		NodeRound nodeRound = new NodeRound();
		nodeRound.setRound(round);
		nodeRound.setHeuristic(getHeuristicValue(nodeRound, 2));   // height -1
		CachedMinMax.setCachedPrediction(round.getGameInfo().getGameId(), GameOperations.fillCachedPrediction(round, CachedPrediction.getCachedPrediction(nodeRound, 1)), false);
		logger.info("MinMaxDFSForCachedNodeRound took " + (System.currentTimeMillis() - ms) + "ms");
	}

	@Override
	public String getType() {
		return "DFS";
	}

	private AiPredictionsWrapper minMaxForMoves(List<Move> moves, NodeRound nodeRound, long ms) throws DAIException {
		List<AiPrediction> aiPredictions = new ArrayList<>();
		AiPrediction bestAiPrediction = null;
		NodeRound bestNodeRound = null;

		for (Move move : moves) {
			Round nextRound = playForMeProcessorVirtual.move(CloneUtil.getClone(nodeRound.getRound()), move);
			NodeRound nextNodeRound = new NodeRound();
			nextNodeRound.setRound(nextRound);
			nextNodeRound.setLastPlayedMove(MoveHelper.getPlayForMeMove(move));
			nextNodeRound.setParent(nodeRound);
			nextNodeRound.setTreeHeight(1);
			nodeRound.getChildren().add(nextNodeRound);
			validateOpponentTiles(nextNodeRound, "playForMe");
			double heuristic = getHeuristicValue(nextNodeRound, 2);
			AiPrediction aiPrediction = new AiPrediction();
			aiPrediction.setMove(move);
			aiPrediction.setHeuristicValue(heuristic);
			aiPredictions.add(aiPrediction);
			boolean better = false;
			if (nodeRound.getRound().getTableInfo().isMyMove()) {
				if (bestAiPrediction == null || bestAiPrediction.getHeuristicValue() < aiPrediction.getHeuristicValue()) {
					better = true;
				}
			} else {
				if (bestAiPrediction == null || bestAiPrediction.getHeuristicValue() > aiPrediction.getHeuristicValue()) {
					better = true;
				}
			}
			if (better) {
				if (bestAiPrediction != null) {
					bestAiPrediction.setBestMove(false);
				}
				bestAiPrediction = aiPrediction;
				bestAiPrediction.setBestMove(true);
				if (bestNodeRound != null) {
					bestNodeRound.setLastPlayedProbability(0.0);
				}
				bestNodeRound = nextNodeRound;
				bestNodeRound.setLastPlayedProbability(1.0);
			}
			logger.info("PlayedMove: " + move.getLeft() + ":" + move.getRight() + " " + move.getDirection() + ", heuristic: " + heuristic);
		}
		double tookMs = System.currentTimeMillis() - ms;
		logger.info("MinMaxDFS took " + tookMs + "ms, recursion count " + recursionCount + ", average " + (tookMs / recursionCount));
		recursionCount = 0;
		if (aiPredictions.isEmpty() || bestAiPrediction == null) {
			logger.info("No AIPrediction");
			return null;
		}
		nodeRound.setHeuristic(bestAiPrediction.getHeuristicValue());
		logger.info("AIPrediction is [" + bestAiPrediction.getMove().getLeft() + "-" + bestAiPrediction.getMove().getRight() + " " +
				bestAiPrediction.getMove().getDirection().name() + "], " + "heuristic: " + bestAiPrediction.getHeuristicValue());

		AiPredictionsWrapper aiPredictionsWrapper = new AiPredictionsWrapper();
		aiPredictionsWrapper.setAiPredictions(aiPredictions);
		aiPredictionsWrapper.setWarnMsgKey(applyValidation());
		return aiPredictionsWrapper;
	}

	private double getHeuristicValue(NodeRound nodeRound, int height) throws DAIException {
		recursionCount++;
		nodeRound.setId(recursionCount);
		Round round = nodeRound.getRound();
		TableInfo tableInfo = round.getTableInfo();
		GameInfo gameInfo = round.getGameInfo();

		// Recursion end conditions: Game is finished, Started new round, reached tree root
		if (round.getGameInfo().isFinished()) {
			nodeRound.setHeuristic(RoundHeuristicHelper.getFinishedGameHeuristic(gameInfo, CachedGames.getGameProperties(gameId).getPointsForWin()));
		}
		if (isNewRound(round)) {
			nodeRound.setHeuristic(RoundHeuristicHelper.getFinishedRoundHeuristic(gameInfo, tableInfo.isMyMove()));
			return nodeRound.getHeuristic();
		}
		if (height == treeHeight) {
			nodeRound.setHeuristic(getHeuristic(round, roundHeuristic));
			return nodeRound.getHeuristic();
		}

		List<Move> moves = GameOperations.getPossibleMoves(round, false);
		if (round.getTableInfo().isMyMove()) {
			// Best move for me
			NodeRound bestNodeRound = null;
			for (Move move : moves) {
				Round nextRound = playForMeProcessorVirtual.move(CloneUtil.getClone(round), move);
				NodeRound nextNodeRound = new NodeRound();
				nextNodeRound.setRound(nextRound);
				nextNodeRound.setLastPlayedMove(MoveHelper.getPlayForMeMove(move));
				nextNodeRound.setParent(nodeRound);
				nextNodeRound.setTreeHeight(height);
				nodeRound.getChildren().add(nextNodeRound);
				validateOpponentTiles(nextNodeRound, "playForMe");
				getHeuristicValue(nextNodeRound, height + 1);
				if (bestNodeRound == null || nextNodeRound.getHeuristic() > bestNodeRound.getHeuristic()) {
					if (bestNodeRound != null) {
						bestNodeRound.setLastPlayedProbability(0.0);
					}
					bestNodeRound = nextNodeRound;
					bestNodeRound.setLastPlayedProbability(1.0);
				}
			}
			// If there are no available move, use bazaar tiles
			if (bestNodeRound == null) {
				double heuristic = 0.0;
				double bazaarProbSum = round.getTableInfo().getBazaarTilesCount();
				for (Map.Entry<Tile, Double> entry : round.getOpponentTiles().entrySet()) {
					Tile tile = entry.getKey();
					double prob = entry.getValue();
					if (prob != 1.0) {
						double probForPickTile = (1 - prob) / bazaarProbSum; // Probability fot choose this tile
						Move move = TileAndMoveHelper.getMove(tile, MoveDirection.LEFT);
						Round nextRound = addForMeProcessorVirtual.move(CloneUtil.getClone(round), move);
						NodeRound nextNodeRound = new NodeRound();
						nextNodeRound.setRound(nextRound);
						nextNodeRound.setLastPlayedMove(MoveHelper.getAddTileForMeMove(move));
						nextNodeRound.setParent(nodeRound);
						nextNodeRound.setTreeHeight(height);
						nextNodeRound.setLastPlayedProbability(probForPickTile);
						nodeRound.setBazaarNodeRound(nextNodeRound);
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
			Queue<NodeRound> possibleRounds = new PriorityQueue<>(Comparator.comparingDouble(NodeRound::getHeuristic));
			// Play all possible move and add in queue
			Map<Tile, Double> opponentTilesClone = CloneUtil.getClone(round.getOpponentTiles());
			for (Move move : moves) {
				Round nextRound = playForOpponentProcessorVirtual.move(CloneUtil.getClone(round), move);
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

			double heuristic = 0.0;
			double remainingProbability = 1.0;
			for (NodeRound nextNodeRound : possibleRounds) {
				if (ComparisonHelper.equal(remainingProbability, 0.0)) {
					break;
				}
				double prob = opponentTilesClone.get(new Tile(nextNodeRound.getLastPlayedMove().getLeft(), nextNodeRound.getLastPlayedMove().getRight()));
				heuristic += nextNodeRound.getHeuristic() * prob * remainingProbability;
				nextNodeRound.setLastPlayedProbability(prob * remainingProbability);
				opponentTilesClone.put(new Tile(nextNodeRound.getLastPlayedMove().getLeft(), nextNodeRound.getLastPlayedMove().getRight()), 0.0);
				ProbabilitiesDistributor.distributeProbabilitiesOpponentProportional(opponentTilesClone, prob);
				remainingProbability -= prob * remainingProbability;
			}

			// Bazaar case
			if (!ComparisonHelper.equal(remainingProbability, 0.0)) {
				Round nextRound = addForOpponentProcessorVirtual.move(CloneUtil.getClone(round), null);
				NodeRound nextNodeRound = new NodeRound();
				nextNodeRound.setRound(nextRound);
				nextNodeRound.setLastPlayedMove(MoveHelper.getAddTileForOpponentMove());
				nextNodeRound.setParent(nodeRound);
				nextNodeRound.setTreeHeight(height);
				nextNodeRound.setLastPlayedProbability(remainingProbability);
				nodeRound.setBazaarNodeRound(nextNodeRound);
				validateOpponentTiles(nextNodeRound, "addForOpponent");
				heuristic += getHeuristicValue(nextNodeRound, height + 1) * remainingProbability;
			}
			nodeRound.setHeuristic(heuristic);
			return heuristic;
		}
	}
}
