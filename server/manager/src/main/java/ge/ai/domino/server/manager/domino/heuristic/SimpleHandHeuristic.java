package ge.ai.domino.server.manager.domino.heuristic;

import ge.ai.domino.domain.domino.Hand;

public class SimpleHandHeuristic implements HandHeuristic {

    @Override
    public double getHeuristic(Hand hand) {
        return (double) (hand.getGameInfo().getMyPoints() - hand.getGameInfo().getHimPoints());
    }
}
