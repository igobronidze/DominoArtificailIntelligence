package ge.ai.domino.manager.game.ai.predictor;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.function.FunctionManager;
import ge.ai.domino.manager.game.ai.minmax.CachedMinMax;
import ge.ai.domino.manager.game.ai.minmax.CachedPrediction;
import ge.ai.domino.manager.game.helper.game.ProbabilitiesDistributor;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class MinMaxPredictor implements OpponentTilesPredictor {

	private static final Logger logger = Logger.getLogger(MinMaxPredictor.class);

	private final SystemParameterManager systemParameterManager = new SystemParameterManager();

	private final SysParam useMinMaxPredictor = new SysParam("useMinMaxPredictor", "false");

	private FunctionManager functionManager = new FunctionManager();

	@Override
	public void predict(Round round, Move move) throws DAIException {
		CachedPrediction cachedPrediction = CachedMinMax.getCachePrediction(round.getGameInfo().getGameId());
		if (cachedPrediction == null) {
			logger.warn("Last cached prediction is null");
			return;
		}

		double playedHeuristic = 0.0;
		boolean heuristicFounded = false;
		for (CachedPrediction child : cachedPrediction.getChildren().values()) {
			if (move.equals(child.getMove())) {
				playedHeuristic = child.getHeuristicValue();
				heuristicFounded = true;
			}
		}
		if (!heuristicFounded) {
			logger.warn("Can't find played heuristic for predictor, move[" + move + "]");
			throw new DAIException("cantFindPlayedHeuristic");
		}

		Map<Move, Double> balancedHeuristic = new HashMap<>();
		for (CachedPrediction child : cachedPrediction.getChildren().values()) {
			if (!move.equals(child.getMove())) {
				balancedHeuristic.put(child.getMove(), playedHeuristic - child.getHeuristicValue());
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
