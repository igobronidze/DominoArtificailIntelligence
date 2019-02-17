package ge.ai.domino.manager.game.ai.predictor;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.manager.function.FunctionManager;
import ge.ai.domino.manager.game.helper.play.PossibleMovesManager;
import ge.ai.domino.manager.game.helper.play.ProbabilitiesDistributor;
import ge.ai.domino.manager.game.logging.RoundLogger;
import ge.ai.domino.manager.game.move.MoveProcessor;
import ge.ai.domino.manager.game.move.PlayForOpponentProcessorVirtual;
import ge.ai.domino.manager.heuristic.HeuristicManager;
import ge.ai.domino.serverutil.CloneUtil;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OneMoveHeuristicOpponentTilesPredictor implements OpponentTilesPredictor {

    private static final Logger logger = Logger.getLogger(OneMoveHeuristicOpponentTilesPredictor.class);

    private final MoveProcessor playForOpponentProcessorVirtual = new PlayForOpponentProcessorVirtual();

    private final HeuristicManager heuristicManager = new HeuristicManager();

    private FunctionManager functionManager = new FunctionManager();

    @Override
    public void predict(Round round, Round roundBeforePlay, Move move) throws DAIException {
        logger.info("Start minMaxPredictor for move[" + move + "]");

        Round oldRound = CloneUtil.getClone(round);

        List<Move> possibleMoves = PossibleMovesManager.getPossibleMoves(roundBeforePlay, false);

        if (!possibleMoves.contains(move)) {
            logger.error("Can't find played heuristic for one move heuristic predictor, move: " + move + ", possibleMoves: " + possibleMoves);
            throw new DAIException("cantFindPlayedHeuristic");
        }

        Map<Move, Double> heuristics = new HashMap<>();
        Map<Move, Double> balancedHeuristic = new HashMap<>();

        for (Move possibleMove : possibleMoves) {
           Round nextRound = playForOpponentProcessorVirtual.move(CloneUtil.getClone(roundBeforePlay), possibleMove);
           heuristics.put(possibleMove, heuristicManager.getHeuristic(nextRound));
        }
        double playedHeuristic = heuristics.get(move);

        for (Move possibleMove : possibleMoves) {
            balancedHeuristic.put(possibleMove, playedHeuristic - heuristics.get(possibleMove));
        }
        logger.info("Heuristics: " + heuristics);
        logger.info("Balanced heuristics: " + balancedHeuristic);

        Map<Tile, Double> opponentTiles = round.getOpponentTiles();
        double probForAdd = 0.0;
        for (Map.Entry<Move, Double> entry : balancedHeuristic.entrySet()) {
            Tile tile = new Tile(entry.getKey().getLeft(), entry.getKey().getRight());
            if (entry.getKey().getLeft() != move.getLeft() || entry.getKey().getRight() != move.getRight()) {
                double oldProb = opponentTiles.get(tile);
                double newProb = oldProb * (1 - functionManager.getOpponentPlayHeuristicsDiffsFunctionValue(entry.getValue()));
                logger.info("Move: " + entry.getKey() + "   Before predictor: " + oldProb + "  |  After predictor: " + newProb);
                opponentTiles.put(tile, newProb);
                probForAdd += oldProb - newProb;
            }

        }
        ProbabilitiesDistributor.distributeProbabilitiesOpponentProportional(opponentTiles, probForAdd);
        logger.info("Opponent tiles before predictor:");
        logger.info(RoundLogger.opponentTileToString(oldRound.getOpponentTiles()));
        logger.info("Opponent tiles after predictor:");
        logger.info(RoundLogger.opponentTileToString(round.getOpponentTiles()));
    }
}
