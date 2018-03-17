package ge.ai.domino.server.manager.game.heuristic;

import ge.ai.domino.domain.game.Round;

public class SimpleRoundHeuristic implements RoundHeuristic {

    @Override
    public float getHeuristic(Round round) {
        return (float) (round.getGameInfo().getMyPoint() - round.getGameInfo().getOpponentPoint());
    }
}
