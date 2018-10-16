package ge.ai.domino.manager.game.ai.heuristic.factory;

import ge.ai.domino.manager.game.ai.heuristic.MixedRoundHeuristic;
import ge.ai.domino.manager.game.ai.heuristic.PointDiffRoundHeuristic;
import ge.ai.domino.manager.game.ai.heuristic.PossibleMovesRoundHeuristic;
import ge.ai.domino.manager.game.ai.heuristic.RoundHeuristic;
import org.apache.log4j.Logger;

public class RoundHeuristicFactory {

    private static final Logger logger = Logger.getLogger(RoundHeuristicFactory.class);

    public static RoundHeuristic getRoundHeuristic(String type) {
        RoundHeuristicType roundHeuristicType;
        try {
            roundHeuristicType = RoundHeuristicType.valueOf(type);
        } catch (Exception ex) {
            logger.warn("Round heuristic type is not known[" + type + "]");
            roundHeuristicType = RoundHeuristicType.POINT_DIFF_ROUND_HEURISTIC;
        }
        switch (roundHeuristicType) {
            case POINT_DIFF_ROUND_HEURISTIC:
                return new PointDiffRoundHeuristic();
            case POSSIBLE_MOVES_ROUND_HEURISTIC:
                return new PossibleMovesRoundHeuristic();
            case MIXED_ROUND_HEURISTIC:
                return new MixedRoundHeuristic();
        }
        return new PointDiffRoundHeuristic();
    }
}
