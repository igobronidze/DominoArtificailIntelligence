package ge.ai.domino.server.manager.domino.heuristic;

import ge.ai.domino.domain.domino.game.Hand;

public interface HandHeuristic {

    double getHeuristic(Hand hand);
}
