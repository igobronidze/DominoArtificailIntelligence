package ge.ai.domino.server.manager.game.ai.minmax;

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
import ge.ai.domino.server.caching.game.CachedGames;
import ge.ai.domino.server.manager.game.ai.heuristic.ComplexRoundHeuristic;
import ge.ai.domino.server.manager.game.ai.heuristic.RoundHeuristic;
import ge.ai.domino.server.manager.game.ai.heuristic.RoundHeuristicHelper;
import ge.ai.domino.server.manager.game.ai.predictor.MinMaxPredictor;
import ge.ai.domino.server.manager.game.helper.ComparisonHelper;
import ge.ai.domino.server.manager.game.helper.MoveHelper;
import ge.ai.domino.server.manager.game.helper.ProbabilitiesDistributor;
import ge.ai.domino.server.manager.game.move.AddForMeProcessor;
import ge.ai.domino.server.manager.game.move.AddForOpponentProcessor;
import ge.ai.domino.server.manager.game.move.MoveProcessor;
import ge.ai.domino.server.manager.game.move.PlayForMeProcessor;
import ge.ai.domino.server.manager.game.move.PlayForOpponentProcessor;
import ge.ai.domino.server.manager.sysparam.SystemParameterManager;
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

	private final RoundHeuristic roundHeuristic = new ComplexRoundHeuristic();

	private final SysParam checkOpponentProbabilities = new SysParam("checkOpponentProbabilities", "false");

	private final SysParam minMaxTreeHeight = new SysParam("minMaxTreeHeight", "8");

	private final MoveProcessor playForMeProcessor = new PlayForMeProcessor();

	private final MoveProcessor playForOpponentProcessor = new PlayForOpponentProcessor();

	private final MoveProcessor addForMeProcessor = new AddForMeProcessor();

	private final MoveProcessor addForOpponentProcessor = new AddForOpponentProcessor();

	private int treeHeight;

	private int recursionCount;

	private int gameId;

	@Override
	public AiPredictionsWrapper solve(Round round) throws DAIException {
		this.gameId = round.getGameInfo().getGameId();
		treeHeight = systemParameterManager.getIntegerParameterValue(minMaxTreeHeight);

		NodeRound nodeRound = new NodeRound();
		nodeRound.setRound(round);
		AiPredictionsWrapper aiPredictionsWrapper = minMax(nodeRound);
		if (new MinMaxPredictor().usePredictor()) {
			CachedMinMax.setLastNodeRound(round.getGameInfo().getGameId(), nodeRound, true);
		}
		return aiPredictionsWrapper;
	}

	@Override
	public void minMaxForCachedNodeRound(Round round) throws DAIException {
		this.gameId = round.getGameInfo().getGameId();
		treeHeight = systemParameterManager.getIntegerParameterValue(minMaxTreeHeight);

		NodeRound nodeRound = new NodeRound();
		nodeRound.setRound(round);
		nodeRound.setHeuristic(getHeuristicValue(nodeRound, 2));   // height -1
		CachedMinMax.setLastNodeRound(round.getGameInfo().getGameId(), nodeRound, false);
	}

	@Override
	public String getType() {
		return "DFS";
	}

	private AiPredictionsWrapper minMax(NodeRound nodeRound) throws DAIException {
		long ms = System.currentTimeMillis();
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
			double heuristic = getHeuristicValue(nextNodeRound, 2);
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
		Round round = nodeRound.getRound();
		TableInfo tableInfo = round.getTableInfo();
		GameInfo gameInfo = round.getGameInfo();

		// Recursion end conditions: Game is finished, Started new round, reached tree root
		if (round.getGameInfo().isFinished()) {
			return RoundHeuristicHelper.getFinishedGameHeuristic(gameInfo, CachedGames.getGameProperties(gameId).getPointsForWin());
		}
		if (isNewRound(round)) {
			nodeRound.setHeuristic(RoundHeuristicHelper.getFinishedRoundHeuristic(gameInfo, tableInfo.isMyMove()));
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
						Round nextRound = addForMeProcessor.move(CloneUtil.getClone(round), move, true);
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
				Round nextRound = addForOpponentProcessor.move(CloneUtil.getClone(round), null, true);
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
