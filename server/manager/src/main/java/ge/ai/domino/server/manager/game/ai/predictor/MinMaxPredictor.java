package ge.ai.domino.server.manager.game.ai.predictor;

import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveType;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.server.manager.function.FunctionManager;
import ge.ai.domino.server.manager.game.ai.minmax.CachedMinMax;
import ge.ai.domino.server.manager.game.ai.minmax.NodeRound;
import ge.ai.domino.server.manager.game.helper.ProbabilitiesDistributor;
import ge.ai.domino.server.manager.sysparam.SystemParameterManager;

import java.util.HashMap;
import java.util.Map;

public class MinMaxPredictor implements OpponentTilesPredictor {

	private final SystemParameterManager systemParameterManager = new SystemParameterManager();

	private final SysParam useMinMaxPredictor = new SysParam("useMinMaxPredictor", "false");

	private FunctionManager functionManager = new FunctionManager();

	@Override
	public void predict(Round round, Move move) {
		NodeRound nodeRound = CachedMinMax.getNodeRound(round.getGameInfo().getGameId());

		double playedHeuristic = 0.0;
		for (NodeRound child : nodeRound.getChildren()) {
			if (Move.equals(move, child.getLastPlayedMove())) {
				playedHeuristic = child.getHeuristic();
			}
		}

		Map<Move, Double> balancedHeuristic = new HashMap<>();
		for (NodeRound child : nodeRound.getChildren()) {
			if (!Move.equals(move, child.getLastPlayedMove()) && child.getLastPlayedMove().getType() != MoveType.ADD_FOR_OPPONENT) {
				balancedHeuristic.put(new Move(child.getLastPlayedMove().getLeft(), child.getLastPlayedMove().getRight(), child.getLastPlayedMove().getDirection()),
						playedHeuristic - child.getHeuristic());
			}
		}

		Map<Tile, Double> opponentTiles = round.getOpponentTiles();
		double probForAdd = 0.0;
		for (Map.Entry<Move, Double> entry : balancedHeuristic.entrySet()) {
			Tile tile = new Tile(entry.getKey().getLeft(), entry.getKey().getRight());
			if (entry.getKey().getLeft() != move.getLeft() || entry.getKey().getRight() != move.getRight()) {
                double oldProb = opponentTiles.get(tile);
                double newProb = oldProb * (1 - functionManager.getOpponentPlayHeuristicsDiffsFunctionValue(entry.getValue()));
                opponentTiles.put(tile, newProb);
                probForAdd += oldProb - newProb;
            }

		}
		ProbabilitiesDistributor.distributeProbabilitiesOpponentProportional(opponentTiles, probForAdd);
	}

	@Override
	public boolean usePredictor() {
		return systemParameterManager.getBooleanParameterValue(useMinMaxPredictor);
	}
}
