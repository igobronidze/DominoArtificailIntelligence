package ge.ai.domino.manager.game.ai.heuristic;

import ge.ai.domino.domain.game.Round;

public class SimpleRoundHeuristic implements RoundHeuristic {

    @Override
    public double getHeuristic(Round round) {
        return (double) (round.getGameInfo().getMyPoint() - round.getGameInfo().getOpponentPoint());
    }
}
