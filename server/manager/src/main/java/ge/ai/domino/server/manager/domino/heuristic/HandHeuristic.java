package ge.ai.domino.server.manager.domino.heuristic;

import ge.ai.domino.domain.domino.Hand;

public interface HandHeuristic {

    double getHeuristic(Hand hand);
}
