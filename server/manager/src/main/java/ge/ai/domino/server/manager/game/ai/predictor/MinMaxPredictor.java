package ge.ai.domino.server.manager.game.ai.predictor;

import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.server.manager.game.ai.minmax.CachedMinMax;
import ge.ai.domino.server.manager.game.ai.minmax.NodeRound;
import ge.ai.domino.server.manager.game.helper.ProbabilitiesDistributor;

import java.util.HashMap;
import java.util.Map;

public class MinMaxPredictor implements OpponentTilesPredictor {

	@Override
	public void predict(Round round, Move move) {
		NodeRound nodeRound = CachedMinMax.getNodeRound(round.getGameInfo().getGameId());
		double min = Double.MAX_VALUE;
		for (NodeRound child : nodeRound.getChildren()) {
			if (!Move.equals(move, child.getLastPlayedMove())) {
				min = Math.min(min, child.getHeuristic());
			}
		}

		double delta = 0.0;
		if (min < 0.0) {
			delta = -2 * min;
		}
		Map<Move, Double> balancedHeuristic = new HashMap<>();
		double sum = 0.0;
		for (NodeRound child : nodeRound.getChildren()) {
			if (!Move.equals(move, child.getLastPlayedMove())) {
				balancedHeuristic.put(new Move(child.getLastPlayedMove().getLeft(), child.getLastPlayedMove().getRight(), child.getLastPlayedMove().getDirection()), child.getHeuristic() + delta);
				sum += child.getHeuristic() + delta;
			}
		}
		Map<Move, Double> balancedPercentage = new HashMap<>();
		if (balancedHeuristic.size() > 1) {
			for (Map.Entry<Move, Double> entry : balancedHeuristic.entrySet()) {
				balancedPercentage.put(entry.getKey(), entry.getValue() / sum);
			}
		}

		Map<Tile, Double> opponentTiles = round.getOpponentTiles();
		double probForAdd = 0.0;
		for (Map.Entry<Move, Double> entry : balancedPercentage.entrySet()) {
			Tile tile = new Tile(entry.getKey().getLeft(), entry.getKey().getRight());
			double oldProb = opponentTiles.get(tile);
			double newProb = oldProb * entry.getValue();
			opponentTiles.put(tile, newProb);
			probForAdd += oldProb - newProb;
		}
		ProbabilitiesDistributor.distributeProbabilitiesOpponentProportional(opponentTiles, probForAdd);
	}
}
