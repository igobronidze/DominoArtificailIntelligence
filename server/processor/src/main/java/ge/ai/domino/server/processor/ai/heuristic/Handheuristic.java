package ge.ai.domino.server.processor.ai.heuristic;

import ge.ai.domino.domain.domino.Hand;

public interface HandHeuristic {

    double getHeuristicValue(Hand hand);
}
