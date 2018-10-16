package ge.ai.domino.manager.game.ai.heuristic;

import ge.ai.domino.domain.game.Round;

public class PointDiffRoundHeuristic extends RoundHeuristic {

    @Override
    public double getNotFinishedRoundHeuristic(Round round, boolean logTrace) {
        return (double) (round.getGameInfo().getMyPoint() - round.getGameInfo().getOpponentPoint());
    }
}
