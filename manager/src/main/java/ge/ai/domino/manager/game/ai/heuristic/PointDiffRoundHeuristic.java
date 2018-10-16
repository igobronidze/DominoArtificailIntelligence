package ge.ai.domino.manager.game.ai.heuristic;

import ge.ai.domino.domain.game.Round;
import ge.ai.domino.manager.game.ai.heuristic.statistic.RoundStatisticType;

public class PointDiffRoundHeuristic extends RoundHeuristic {

    @Override
    public double getNotFinishedRoundHeuristic(Round round) {
        roundStatisticProcessor.replaceRound(round);

        double myPoint = roundStatisticProcessor.getStatistic(RoundStatisticType.MY_POINT);
        double opponentPoint = roundStatisticProcessor.getStatistic(RoundStatisticType.OPPONENT_POINT);

        return myPoint - opponentPoint;
    }
}
